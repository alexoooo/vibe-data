package io.github.alexoooo.vibe.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class HamtIntObjectPersistentMap<T> implements IntObjectPersistentMap<T> {

    private static final int BITS_PER_LEVEL = 5;
    private static final int CHUNK_SIZE = 1 << BITS_PER_LEVEL;
    private static final int BIT_MASK = CHUNK_SIZE - 1;
    private static final int MAX_SHIFT = 30;
    private static final int MAX_BITMAP_DEPTH = (Integer.SIZE + BITS_PER_LEVEL - 1) / BITS_PER_LEVEL;
    private static final Object[] EMPTY_NODE = {};
    private static final int[] EMPTY_KEYS = {};
    private static final Object[] EMPTY_VALUES = {};
    private static final SparseNode<?>[] EMPTY_CHILDREN = {};
    private static final DenseChunk<?> EMPTY_DENSE_CHUNK = new DenseChunk<>(0, new Object[CHUNK_SIZE]);
    private static final DensePrefix<?> EMPTY_DENSE_PREFIX =
            new DensePrefix<>(0, 0, 0, EMPTY_NODE, 0, emptyDenseChunk());
    private static final HamtIntObjectPersistentMap<?> EMPTY =
            new HamtIntObjectPersistentMap<>(emptyDensePrefix(), null, 0);

    private final DensePrefix<T> densePrefix;
    private final @Nullable SparseNode<T> sparseRoot;
    private final int sparseSize;

    private HamtIntObjectPersistentMap(DensePrefix<T> densePrefix, @Nullable SparseNode<T> sparseRoot, int sparseSize) {
        this.densePrefix = densePrefix;
        this.sparseRoot = sparseRoot;
        this.sparseSize = sparseSize;
    }

    @SuppressWarnings("unchecked")
    public static <T> HamtIntObjectPersistentMap<T> empty() {
        return (HamtIntObjectPersistentMap<T>) EMPTY;
    }

    @Override
    public @Nullable T find(int key) {
        if (key >= 0 && key < densePrefix.span()) {
            return densePrefix.find(key);
        }

        SparseNode<T> currentSparseRoot = sparseRoot;
        return currentSparseRoot == null ? null : currentSparseRoot.find(key, hash(key), 0);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> denseIterator = densePrefix.iterator();
        SparseNode<T> currentSparseRoot = sparseRoot;
        if (currentSparseRoot == null) {
            return denseIterator;
        }

        Iterator<T> sparseIterator = new SparseValueIterator<>(currentSparseRoot);
        return denseIterator.hasNext() ? new CombinedIterator<>(denseIterator, sparseIterator) : sparseIterator;
    }

    @Override
    public int size() {
        return densePrefix.size() + sparseSize;
    }

    @Override
    public HamtIntObjectPersistentMap<T> remove(int key) {
        if (key >= 0 && key < densePrefix.span()) {
            DensePrefixUpdate<T> update = densePrefix.remove(key);
            return update.modified ? create(update.prefix, sparseRoot, sparseSize) : this;
        }

        SparseNode<T> currentSparseRoot = sparseRoot;
        if (currentSparseRoot == null) {
            return this;
        }

        SparseChange change = new SparseChange();
        SparseNode<T> updatedSparseRoot = currentSparseRoot.remove(key, hash(key), 0, change);
        return change.modified ? create(densePrefix, updatedSparseRoot, sparseSize + change.sizeDelta) : this;
    }

    @Override
    public HamtIntObjectPersistentMap<T> put(int key, T value) {
        T requiredValue = Objects.requireNonNull(value, "value");
        if (key >= 0) {
            if (key < densePrefix.span()) {
                DensePrefixUpdate<T> update = densePrefix.put(key, requiredValue);
                return update.modified ? create(update.prefix, sparseRoot, sparseSize) : this;
            }

            if (key == densePrefix.span()) {
                SparseNode<T> currentSparseRoot = sparseRoot;
                int keyHash = hash(key);
                if (currentSparseRoot == null || currentSparseRoot.find(key, keyHash, 0) == null) {
                    return create(densePrefix.append(requiredValue), currentSparseRoot, sparseSize);
                }
            }
        }

        int keyHash = hash(key);
        SparseNode<T> currentSparseRoot = sparseRoot;
        if (currentSparseRoot == null) {
            return create(densePrefix, new SparseLeafNode<>(key, keyHash, requiredValue), 1);
        }

        SparseChange change = new SparseChange();
        SparseNode<T> updatedSparseRoot = currentSparseRoot.put(key, keyHash, requiredValue, 0, change);
        return change.modified ? create(densePrefix, updatedSparseRoot, sparseSize + change.sizeDelta) : this;
    }

    private static int hash(int key) {
        return key ^ (key >>> 16);
    }

    private static int chunk(int hash, int shift) {
        return (hash >>> shift) & BIT_MASK;
    }

    private static long bitFor(int chunk) {
        return 1L << chunk;
    }

    private static int chunkCapacity(int shift) {
        return 1 << (shift + BITS_PER_LEVEL);
    }

    private static int dataIndex(long bitmap, long bit) {
        return Long.bitCount(bitmap & (bit - 1L));
    }

    @SuppressWarnings("unchecked")
    private static <T> DenseChunk<T> emptyDenseChunk() {
        return (DenseChunk<T>) EMPTY_DENSE_CHUNK;
    }

    @SuppressWarnings("unchecked")
    private static <T> DensePrefix<T> emptyDensePrefix() {
        return (DensePrefix<T>) EMPTY_DENSE_PREFIX;
    }

    private static int[] insertKey(int[] keys, int index, int key) {
        int[] updatedKeys = Arrays.copyOf(keys, keys.length + 1);
        if (index < keys.length) {
            System.arraycopy(keys, index, updatedKeys, index + 1, keys.length - index);
        }
        updatedKeys[index] = key;
        return updatedKeys;
    }

    private static Object[] insertValue(Object[] values, int index, Object value) {
        Object[] updatedValues = Arrays.copyOf(values, values.length + 1);
        if (index < values.length) {
            System.arraycopy(values, index, updatedValues, index + 1, values.length - index);
        }
        updatedValues[index] = value;
        return updatedValues;
    }

    private static SparseNode<?>[] insertChild(SparseNode<?>[] children, int index, SparseNode<?> child) {
        SparseNode<?>[] updatedChildren = Arrays.copyOf(children, children.length + 1);
        if (index < children.length) {
            System.arraycopy(children, index, updatedChildren, index + 1, children.length - index);
        }
        updatedChildren[index] = child;
        return updatedChildren;
    }

    private static int[] removeKey(int[] keys, int index) {
        int[] updatedKeys = Arrays.copyOf(keys, keys.length - 1);
        if (index < updatedKeys.length) {
            System.arraycopy(keys, index + 1, updatedKeys, index, keys.length - index - 1);
        }
        return updatedKeys;
    }

    private static Object[] removeValue(Object[] values, int index) {
        Object[] updatedValues = Arrays.copyOf(values, values.length - 1);
        if (index < updatedValues.length) {
            System.arraycopy(values, index + 1, updatedValues, index, values.length - index - 1);
        }
        return updatedValues;
    }

    private static SparseNode<?>[] removeChild(SparseNode<?>[] children, int index) {
        SparseNode<?>[] updatedChildren = Arrays.copyOf(children, children.length - 1);
        if (index < updatedChildren.length) {
            System.arraycopy(children, index + 1, updatedChildren, index, children.length - index - 1);
        }
        return updatedChildren;
    }

    private static SparseNode<?>[] replaceChild(SparseNode<?>[] children, int index, SparseNode<?> child) {
        SparseNode<?>[] updatedChildren = children.clone();
        updatedChildren[index] = child;
        return updatedChildren;
    }

    private static boolean canCollapse(SparseNode<?> node) {
        return node instanceof SparseLeafNode<?> || node instanceof SparseCollisionNode<?>;
    }

    @SuppressWarnings("unchecked")
    private static <T> SparseNode<T> castSparseNode(SparseNode<?> node) {
        return (SparseNode<T>) node;
    }

    @SuppressWarnings("unchecked")
    private static <T> DenseChunk<T> castDenseChunk(Object value) {
        return (DenseChunk<T>) value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T valueAt(Object[] values, int index) {
        return (T) values[index];
    }

    @SuppressWarnings("unchecked")
    private static <T> T valueOf(SparseLeafNode<?> leaf) {
        return (T) leaf.value;
    }

    private static int[] emptyKeys() {
        return EMPTY_KEYS;
    }

    private static Object[] emptyValues() {
        return EMPTY_VALUES;
    }

    @SuppressWarnings("unchecked")
    private static <T> SparseNode<T>[] emptyChildren() {
        return (SparseNode<T>[]) EMPTY_CHILDREN;
    }

    private static Object[] asNode(Object value) {
        return (Object[]) value;
    }

    private static <T> Object[] newPath(int level, DenseChunk<T> chunk) {
        if (level == 0) {
            return new Object[] {chunk};
        }
        return new Object[] {newPath(level - BITS_PER_LEVEL, chunk)};
    }

    private static <T> Object[] pushChunk(int level, Object[] parent, int chunkIndex, DenseChunk<T> chunk) {
        int subIndex = (chunkIndex >>> level) & BIT_MASK;
        Object[] updatedParent = subIndex < parent.length ? parent.clone() : Arrays.copyOf(parent, subIndex + 1);

        if (level == 0) {
            updatedParent[subIndex] = chunk;
            return updatedParent;
        }

        Object[] child = subIndex < parent.length ? asNode(parent[subIndex]) : EMPTY_NODE;
        updatedParent[subIndex] = child.length == 0
                ? newPath(level - BITS_PER_LEVEL, chunk)
                : pushChunk(level - BITS_PER_LEVEL, child, chunkIndex, chunk);
        return updatedParent;
    }

    private static <T> Object[] updateChunk(int level, Object[] parent, int chunkIndex, DenseChunk<T> chunk) {
        Object[] updatedParent = parent.clone();
        int subIndex = (chunkIndex >>> level) & BIT_MASK;

        if (level == 0) {
            updatedParent[subIndex] = chunk;
            return updatedParent;
        }

        updatedParent[subIndex] = updateChunk(level - BITS_PER_LEVEL, asNode(parent[subIndex]), chunkIndex, chunk);
        return updatedParent;
    }

    private static <T> SparseNode<T> mergeEntries(
            int leftKey,
            int leftHash,
            T leftValue,
            int rightKey,
            int rightHash,
            T rightValue,
            int shift) {
        if (shift > MAX_SHIFT) {
            return new SparseCollisionNode<>(
                    leftHash,
                    new int[] {leftKey, rightKey},
                    new Object[] {leftValue, rightValue});
        }

        int leftChunk = chunk(leftHash, shift);
        int rightChunk = chunk(rightHash, shift);

        if (leftChunk != rightChunk) {
            long leftBit = bitFor(leftChunk);
            long rightBit = bitFor(rightChunk);
            return leftChunk < rightChunk
                    ? new SparseBitmapNode<>(
                            leftBit | rightBit,
                            0L,
                            new int[] {leftKey, rightKey},
                            new Object[] {leftValue, rightValue},
                            emptyChildren())
                    : new SparseBitmapNode<>(
                            leftBit | rightBit,
                            0L,
                            new int[] {rightKey, leftKey},
                            new Object[] {rightValue, leftValue},
                            emptyChildren());
        }

        SparseNode<T> child =
                mergeEntries(leftKey, leftHash, leftValue, rightKey, rightHash, rightValue, shift + BITS_PER_LEVEL);
        return new SparseBitmapNode<>(0L, bitFor(leftChunk), emptyKeys(), emptyValues(), new SparseNode<?>[] {child});
    }

    private static <T> HamtIntObjectPersistentMap<T> create(
            DensePrefix<T> densePrefix,
            @Nullable SparseNode<T> sparseRoot,
            int sparseSize) {
        DensePrefix<T> normalizedDensePrefix = densePrefix.size() == 0 ? emptyDensePrefix() : densePrefix;
        return normalizedDensePrefix.size() == 0 && sparseSize == 0
                ? empty()
                : new HamtIntObjectPersistentMap<>(normalizedDensePrefix, sparseRoot, sparseSize);
    }

    private interface SparseNode<T> {

        @Nullable T find(int key, int hash, int shift);

        SparseNode<T> put(int key, int hash, T value, int shift, SparseChange change);

        @Nullable SparseNode<T> remove(int key, int hash, int shift, SparseChange change);
    }

    private static final class SparseChange {

        private boolean modified;
        private int sizeDelta;

        private void inserted() {
            modified = true;
            sizeDelta = 1;
        }

        private void removed() {
            modified = true;
            sizeDelta = -1;
        }

        private void replaced() {
            modified = true;
        }
    }

    private static final class DenseChunkUpdate<T> {

        private final DenseChunk<T> chunk;
        private final boolean modified;
        private final int sizeDelta;

        private DenseChunkUpdate(DenseChunk<T> chunk, boolean modified, int sizeDelta) {
            this.chunk = chunk;
            this.modified = modified;
            this.sizeDelta = sizeDelta;
        }
    }

    private static final class DensePrefixUpdate<T> {

        private final DensePrefix<T> prefix;
        private final boolean modified;

        private DensePrefixUpdate(DensePrefix<T> prefix, boolean modified) {
            this.prefix = prefix;
            this.modified = modified;
        }
    }

    private static final class DenseChunk<T> {

        private final int bitmap;
        private final Object[] values;

        private DenseChunk(int bitmap, Object[] values) {
            this.bitmap = bitmap;
            this.values = values;
        }

        private static <T> DenseChunk<T> single(int offset, T value) {
            Object[] values = new Object[CHUNK_SIZE];
            values[offset] = value;
            return new DenseChunk<>(1 << offset, values);
        }

        private @Nullable T find(int offset) {
            int bit = 1 << offset;
            return (bitmap & bit) == 0 ? null : valueAt(values, offset);
        }

        private DenseChunkUpdate<T> put(int offset, T value) {
            int bit = 1 << offset;
            if ((bitmap & bit) == 0) {
                Object[] updatedValues = values.clone();
                updatedValues[offset] = value;
                return new DenseChunkUpdate<>(new DenseChunk<>(bitmap | bit, updatedValues), true, 1);
            }

            if (valueAt(values, offset) == value) {
                return new DenseChunkUpdate<>(this, false, 0);
            }

            Object[] updatedValues = values.clone();
            updatedValues[offset] = value;
            return new DenseChunkUpdate<>(new DenseChunk<>(bitmap, updatedValues), true, 0);
        }

        private DenseChunkUpdate<T> remove(int offset) {
            int bit = 1 << offset;
            if ((bitmap & bit) == 0) {
                return new DenseChunkUpdate<>(this, false, 0);
            }

            Object[] updatedValues = values.clone();
            updatedValues[offset] = null;
            return new DenseChunkUpdate<>(new DenseChunk<>(bitmap & ~bit, updatedValues), true, -1);
        }
    }

    private static final class DensePrefix<T> implements Iterable<T> {

        private final int span;
        private final int presentCount;
        private final int shift;
        private final Object[] root;
        private final int rootChunkCount;
        private final DenseChunk<T> tail;

        private DensePrefix(
                int span,
                int presentCount,
                int shift,
                Object[] root,
                int rootChunkCount,
                DenseChunk<T> tail) {
            this.span = span;
            this.presentCount = presentCount;
            this.shift = shift;
            this.root = root;
            this.rootChunkCount = rootChunkCount;
            this.tail = tail;
        }

        private @Nullable T find(int key) {
            DenseChunk<T> chunk = chunkFor(key >>> BITS_PER_LEVEL);
            return chunk.find(key & BIT_MASK);
        }

        @Override
        public Iterator<T> iterator() {
            return presentCount == 0 ? new EmptyIterator<>() : new DenseValueIterator<>(this);
        }

        private DensePrefix<T> append(T value) {
            if (span == 0) {
                return new DensePrefix<>(1, 1, shift, root, rootChunkCount, DenseChunk.single(0, value));
            }

            int tailOffset = span & BIT_MASK;
            if (tailOffset != 0) {
                DenseChunkUpdate<T> update = tail.put(tailOffset, value);
                return new DensePrefix<>(span + 1, presentCount + 1, shift, root, rootChunkCount, update.chunk);
            }

            Object[] updatedRoot;
            int updatedShift = shift;
            if (rootChunkCount == chunkCapacity(shift)) {
                updatedRoot = new Object[2];
                updatedRoot[0] = root;
                updatedRoot[1] = newPath(shift, tail);
                updatedShift += BITS_PER_LEVEL;
            } else {
                updatedRoot = pushChunk(shift, root, rootChunkCount, tail);
            }

            return new DensePrefix<>(
                    span + 1,
                    presentCount + 1,
                    updatedShift,
                    updatedRoot,
                    rootChunkCount + 1,
                    DenseChunk.single(0, value));
        }

        private DensePrefixUpdate<T> put(int key, T value) {
            int chunkIndex = key >>> BITS_PER_LEVEL;
            int offset = key & BIT_MASK;
            DenseChunk<T> chunk = chunkFor(chunkIndex);
            DenseChunkUpdate<T> update = chunk.put(offset, value);
            if (!update.modified) {
                return new DensePrefixUpdate<>(this, false);
            }

            DensePrefix<T> updatedPrefix = chunkIndex == rootChunkCount
                    ? new DensePrefix<>(span, presentCount + update.sizeDelta, shift, root, rootChunkCount, update.chunk)
                    : new DensePrefix<>(
                            span,
                            presentCount + update.sizeDelta,
                            shift,
                            updateChunk(shift, root, chunkIndex, update.chunk),
                            rootChunkCount,
                            tail);
            return new DensePrefixUpdate<>(updatedPrefix, true);
        }

        private DensePrefixUpdate<T> remove(int key) {
            int chunkIndex = key >>> BITS_PER_LEVEL;
            int offset = key & BIT_MASK;
            DenseChunk<T> chunk = chunkFor(chunkIndex);
            DenseChunkUpdate<T> update = chunk.remove(offset);
            if (!update.modified) {
                return new DensePrefixUpdate<>(this, false);
            }

            DensePrefix<T> updatedPrefix = chunkIndex == rootChunkCount
                    ? new DensePrefix<>(span, presentCount - 1, shift, root, rootChunkCount, update.chunk)
                    : new DensePrefix<>(
                            span,
                            presentCount - 1,
                            shift,
                            updateChunk(shift, root, chunkIndex, update.chunk),
                            rootChunkCount,
                            tail);
            return new DensePrefixUpdate<>(updatedPrefix, true);
        }

        private int size() {
            return presentCount;
        }

        private int span() {
            return span;
        }

        private DenseChunk<T> chunkFor(int chunkIndex) {
            if (chunkIndex == rootChunkCount) {
                return tail;
            }

            Object[] node = root;
            for (int level = shift; level > 0; level -= BITS_PER_LEVEL) {
                node = asNode(node[(chunkIndex >>> level) & BIT_MASK]);
            }
            return castDenseChunk(node[chunkIndex & BIT_MASK]);
        }
    }

    private static final class SparseLeafNode<T> implements SparseNode<T> {

        private final int key;
        private final int hash;
        private final T value;

        private SparseLeafNode(int key, int hash, T value) {
            this.key = key;
            this.hash = hash;
            this.value = value;
        }

        @Override
        public @Nullable T find(int key, int hash, int shift) {
            return this.key == key ? value : null;
        }

        @Override
        public SparseNode<T> put(int key, int hash, T value, int shift, SparseChange change) {
            if (this.key == key) {
                if (this.value == value) {
                    return this;
                }

                change.replaced();
                return new SparseLeafNode<>(key, hash, value);
            }

            change.inserted();
            return mergeEntries(this.key, this.hash, this.value, key, hash, value, shift);
        }

        @Override
        public @Nullable SparseNode<T> remove(int key, int hash, int shift, SparseChange change) {
            if (this.key != key) {
                return this;
            }

            change.removed();
            return null;
        }
    }

    private static final class SparseBitmapNode<T> implements SparseNode<T> {

        private final long dataMap;
        private final long nodeMap;
        private final int[] keys;
        private final Object[] values;
        private final SparseNode<?>[] children;

        private SparseBitmapNode(
                long dataMap,
                long nodeMap,
                int[] keys,
                Object[] values,
                SparseNode<?>[] children) {
            this.dataMap = dataMap;
            this.nodeMap = nodeMap;
            this.keys = keys;
            this.values = values;
            this.children = children;
        }

        @Override
        public @Nullable T find(int key, int hash, int shift) {
            long bit = bitFor(chunk(hash, shift));
            if ((dataMap & bit) != 0L) {
                int index = dataIndex(dataMap, bit);
                return keys[index] == key ? valueAt(values, index) : null;
            }

            if ((nodeMap & bit) == 0L) {
                return null;
            }

            int childIndex = dataIndex(nodeMap, bit);
            return HamtIntObjectPersistentMap.<T>castSparseNode(children[childIndex])
                    .find(key, hash, shift + BITS_PER_LEVEL);
        }

        @Override
        public SparseNode<T> put(int key, int hash, T value, int shift, SparseChange change) {
            long bit = bitFor(chunk(hash, shift));
            if ((dataMap & bit) != 0L) {
                int entryIndex = dataIndex(dataMap, bit);
                int existingKey = keys[entryIndex];
                if (existingKey == key) {
                    if (valueAt(values, entryIndex) == value) {
                        return this;
                    }

                    Object[] updatedValues = values.clone();
                    updatedValues[entryIndex] = value;
                    change.replaced();
                    return new SparseBitmapNode<>(dataMap, nodeMap, keys, updatedValues, children);
                }

                change.inserted();
                SparseNode<T> merged =
                        mergeEntries(
                                existingKey,
                                hash(existingKey),
                                HamtIntObjectPersistentMap.<T>valueAt(values, entryIndex),
                                key,
                                hash,
                                value,
                                shift + BITS_PER_LEVEL);
                return new SparseBitmapNode<>(
                        dataMap & ~bit,
                        nodeMap | bit,
                        removeKey(keys, entryIndex),
                        removeValue(values, entryIndex),
                        insertChild(children, dataIndex(nodeMap, bit), merged));
            }

            if ((nodeMap & bit) == 0L) {
                change.inserted();
                int entryIndex = dataIndex(dataMap, bit);
                return new SparseBitmapNode<>(
                        dataMap | bit,
                        nodeMap,
                        insertKey(keys, entryIndex, key),
                        insertValue(values, entryIndex, value),
                        children);
            }

            int childIndex = dataIndex(nodeMap, bit);
            SparseNode<T> updatedChild = HamtIntObjectPersistentMap.<T>castSparseNode(children[childIndex])
                    .put(key, hash, value, shift + BITS_PER_LEVEL, change);
            if (!change.modified) {
                return this;
            }

            return new SparseBitmapNode<>(dataMap, nodeMap, keys, values, replaceChild(children, childIndex, updatedChild));
        }

        @Override
        public @Nullable SparseNode<T> remove(int key, int hash, int shift, SparseChange change) {
            long bit = bitFor(chunk(hash, shift));
            if ((dataMap & bit) != 0L) {
                int entryIndex = dataIndex(dataMap, bit);
                if (keys[entryIndex] != key) {
                    return this;
                }

                change.removed();
                int[] updatedKeys = removeKey(keys, entryIndex);
                Object[] updatedValues = removeValue(values, entryIndex);
                if (updatedKeys.length == 0 && children.length == 0) {
                    return null;
                }
                if (updatedKeys.length == 0
                        && children.length == 1
                        && canCollapse(HamtIntObjectPersistentMap.<T>castSparseNode(children[0]))) {
                    return HamtIntObjectPersistentMap.<T>castSparseNode(children[0]);
                }
                return new SparseBitmapNode<>(dataMap & ~bit, nodeMap, updatedKeys, updatedValues, children);
            }

            if ((nodeMap & bit) == 0L) {
                return this;
            }

            int childIndex = dataIndex(nodeMap, bit);
            SparseNode<T> updatedChild = HamtIntObjectPersistentMap.<T>castSparseNode(children[childIndex])
                    .remove(key, hash, shift + BITS_PER_LEVEL, change);
            if (!change.modified) {
                return this;
            }

            if (updatedChild != null) {
                SparseNode<?>[] updatedChildren = replaceChild(children, childIndex, updatedChild);
                if (keys.length == 0
                        && updatedChildren.length == 1
                        && canCollapse(HamtIntObjectPersistentMap.<T>castSparseNode(updatedChildren[0]))) {
                    return HamtIntObjectPersistentMap.<T>castSparseNode(updatedChildren[0]);
                }
                return new SparseBitmapNode<>(dataMap, nodeMap, keys, values, updatedChildren);
            }

            SparseNode<?>[] updatedChildren = removeChild(children, childIndex);
            if (keys.length == 0 && updatedChildren.length == 0) {
                return null;
            }
            if (keys.length == 0
                    && updatedChildren.length == 1
                    && canCollapse(HamtIntObjectPersistentMap.<T>castSparseNode(updatedChildren[0]))) {
                return HamtIntObjectPersistentMap.<T>castSparseNode(updatedChildren[0]);
            }
            return new SparseBitmapNode<>(dataMap, nodeMap & ~bit, keys, values, updatedChildren);
        }
    }

    private static final class SparseCollisionNode<T> implements SparseNode<T> {

        private final int hash;
        private final int[] keys;
        private final Object[] values;

        private SparseCollisionNode(int hash, int[] keys, Object[] values) {
            this.hash = hash;
            this.keys = keys;
            this.values = values;
        }

        @Override
        public @Nullable T find(int key, int hash, int shift) {
            if (this.hash != hash) {
                return null;
            }

            int index = indexOf(key);
            return index < 0 ? null : valueAt(values, index);
        }

        @Override
        public SparseNode<T> put(int key, int hash, T value, int shift, SparseChange change) {
            if (this.hash != hash) {
                throw new IllegalStateException("Unexpected hash divergence");
            }

            int index = indexOf(key);
            if (index >= 0) {
                if (valueAt(values, index) == value) {
                    return this;
                }

                Object[] updatedValues = values.clone();
                updatedValues[index] = value;
                change.replaced();
                return new SparseCollisionNode<>(hash, keys, updatedValues);
            }

            int[] updatedKeys = Arrays.copyOf(keys, keys.length + 1);
            updatedKeys[keys.length] = key;
            Object[] updatedValues = Arrays.copyOf(values, values.length + 1);
            updatedValues[values.length] = value;
            change.inserted();
            return new SparseCollisionNode<>(hash, updatedKeys, updatedValues);
        }

        @Override
        public @Nullable SparseNode<T> remove(int key, int hash, int shift, SparseChange change) {
            if (this.hash != hash) {
                return this;
            }

            int index = indexOf(key);
            if (index < 0) {
                return this;
            }

            change.removed();
            if (keys.length == 1) {
                return null;
            }

            if (keys.length == 2) {
                int remainingIndex = index ^ 1;
                return new SparseLeafNode<>(keys[remainingIndex], this.hash, valueAt(values, remainingIndex));
            }

            return new SparseCollisionNode<>(this.hash, removeKey(keys, index), removeValue(values, index));
        }

        private int indexOf(int key) {
            for (int index = 0; index < keys.length; index++) {
                if (keys[index] == key) {
                    return index;
                }
            }

            return -1;
        }
    }

    private static final class DenseValueIterator<T> implements Iterator<T> {

        private final DensePrefix<T> prefix;
        private int chunkIndex;
        private DenseChunk<T> chunk;
        private int remainingBits;
        private int offset;
        private @Nullable T nextValue;

        private DenseValueIterator(DensePrefix<T> prefix) {
            this.prefix = prefix;
            this.chunk = prefix.chunkFor(0);
            this.remainingBits = chunk.bitmap;
            advance();
        }

        @Override
        public boolean hasNext() {
            return nextValue != null;
        }

        @Override
        public T next() {
            T value = nextValue;
            if (value == null) {
                throw new NoSuchElementException();
            }

            advance();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advance() {
            while (true) {
                if (remainingBits != 0) {
                    int skip = Integer.numberOfTrailingZeros(remainingBits);
                    remainingBits >>>= skip;
                    offset += skip;
                    nextValue = valueAt(chunk.values, offset);
                    remainingBits >>>= 1;
                    offset++;
                    return;
                }

                if (chunkIndex == prefix.rootChunkCount) {
                    nextValue = null;
                    return;
                }

                chunkIndex++;
                chunk = prefix.chunkFor(chunkIndex);
                remainingBits = chunk.bitmap;
                offset = 0;
            }
        }
    }

    private static final class SparseValueIterator<T> implements Iterator<T> {

        private final SparseBitmapNode<?>[] nodeStack = new SparseBitmapNode<?>[MAX_BITMAP_DEPTH];
        private final int[] valueIndices = new int[MAX_BITMAP_DEPTH];
        private final int[] childIndices = new int[MAX_BITMAP_DEPTH];
        private int depth = -1;
        private @Nullable Object[] collisionValues;
        private int collisionIndex;
        private @Nullable T nextValue;

        private SparseValueIterator(@Nullable SparseNode<T> root) {
            if (root instanceof SparseBitmapNode<?> bitmap) {
                push(bitmap);
                advance();
            } else if (root instanceof SparseLeafNode<?> leaf) {
                nextValue = valueOf(leaf);
            } else if (root instanceof SparseCollisionNode<?> collision) {
                collisionValues = collision.values;
                collisionIndex = 1;
                nextValue = valueAt(collision.values, 0);
            }
        }

        @Override
        public boolean hasNext() {
            return nextValue != null;
        }

        @Override
        public T next() {
            T value = nextValue;
            if (value == null) {
                throw new NoSuchElementException();
            }

            advance();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advance() {
            Object[] currentCollisionValues = collisionValues;
            if (currentCollisionValues != null) {
                if (collisionIndex < currentCollisionValues.length) {
                    nextValue = valueAt(currentCollisionValues, collisionIndex++);
                    return;
                }

                collisionValues = null;
            }

            while (depth >= 0) {
                SparseBitmapNode<?> node = nodeStack[depth];
                int valueIndex = valueIndices[depth];
                if (valueIndex < node.values.length) {
                    nextValue = valueAt(node.values, valueIndex);
                    valueIndices[depth] = valueIndex + 1;
                    return;
                }

                int childIndex = childIndices[depth];
                if (childIndex < node.children.length) {
                    Object child = node.children[childIndex];
                    childIndices[depth] = childIndex + 1;
                    if (child instanceof SparseBitmapNode<?> bitmap) {
                        push(bitmap);
                        continue;
                    }

                    if (child instanceof SparseLeafNode<?> leaf) {
                        nextValue = valueOf(leaf);
                        return;
                    }

                    SparseCollisionNode<?> collision = (SparseCollisionNode<?>) child;
                    collisionValues = collision.values;
                    collisionIndex = 1;
                    nextValue = valueAt(collision.values, 0);
                    return;
                }

                depth--;
            }

            nextValue = null;
        }

        private void push(SparseBitmapNode<?> node) {
            nodeStack[++depth] = node;
            valueIndices[depth] = 0;
            childIndices[depth] = 0;
        }
    }

    private static final class CombinedIterator<T> implements Iterator<T> {

        private final Iterator<T> first;
        private final Iterator<T> second;

        private CombinedIterator(Iterator<T> first, Iterator<T> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean hasNext() {
            return first.hasNext() || second.hasNext();
        }

        @Override
        public T next() {
            if (first.hasNext()) {
                return first.next();
            }
            if (second.hasNext()) {
                return second.next();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class EmptyIterator<T> implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
