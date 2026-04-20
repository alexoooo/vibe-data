package io.github.alexoooo.vibe.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class CompactLongObjectPersistentMap<T> implements LongObjectPersistentMap<T> {

    private static final long MIX_CONSTANT_1 = 0x9e3779b97f4a7c15L;
    private static final long MIX_CONSTANT_2 = 0xbf58476d1ce4e5b9L;
    private static final long MIX_CONSTANT_3 = 0x94d049bb133111ebL;
    private static final int BITS_PER_LEVEL = 5;
    private static final int BIT_MASK = (1 << BITS_PER_LEVEL) - 1;
    private static final int MAX_SHIFT = 60;
    private static final int MAX_BITMAP_DEPTH = (Long.SIZE + BITS_PER_LEVEL - 1) / BITS_PER_LEVEL;
    private static final CompactLongObjectPersistentMap<?> EMPTY = new CompactLongObjectPersistentMap<>(null, 0);
    private static final long[] EMPTY_KEYS = {};
    private static final Object[] EMPTY_VALUES = {};
    private static final Node<?>[] EMPTY_CHILDREN = {};

    private final @Nullable Node<T> root;
    private final int size;

    private CompactLongObjectPersistentMap(@Nullable Node<T> root, int size) {
        this.root = root;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <T> CompactLongObjectPersistentMap<T> empty() {
        return (CompactLongObjectPersistentMap<T>) EMPTY;
    }

    @Override
    public @Nullable T find(long key) {
        Node<T> currentRoot = root;
        return currentRoot == null ? null : currentRoot.find(key, mix(key), 0);
    }

    @Override
    public Iterator<T> iterator() {
        return new ValueIterator<>(root);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public CompactLongObjectPersistentMap<T> remove(long key) {
        Node<T> currentRoot = root;
        if (currentRoot == null) {
            return this;
        }

        Change change = new Change();
        Node<T> updatedRoot = currentRoot.remove(key, mix(key), 0, change);
        if (!change.modified) {
            return this;
        }

        int updatedSize = size + change.sizeDelta;
        return updatedSize == 0 ? empty() : new CompactLongObjectPersistentMap<>(updatedRoot, updatedSize);
    }

    @Override
    public CompactLongObjectPersistentMap<T> put(long key, T value) {
        T requiredValue = Objects.requireNonNull(value, "value");
        long hash = mix(key);
        Node<T> currentRoot = root;
        if (currentRoot == null) {
            return new CompactLongObjectPersistentMap<>(new LeafNode<>(key, hash, requiredValue), 1);
        }

        Change change = new Change();
        Node<T> updatedRoot = currentRoot.put(key, hash, requiredValue, 0, change);
        if (!change.modified) {
            return this;
        }

        return new CompactLongObjectPersistentMap<>(updatedRoot, size + change.sizeDelta);
    }

    private static long mix(long key) {
        long mixed = key + MIX_CONSTANT_1;
        mixed = (mixed ^ (mixed >>> 30)) * MIX_CONSTANT_2;
        mixed = (mixed ^ (mixed >>> 27)) * MIX_CONSTANT_3;
        return mixed ^ (mixed >>> 31);
    }

    private static int chunk(long hash, int shift) {
        return (int) ((hash >>> shift) & BIT_MASK);
    }

    private static int bitFor(int chunk) {
        return 1 << chunk;
    }

    private static int entryIndex(int bitmap, int bit) {
        return Integer.bitCount(bitmap & (bit - 1));
    }

    private static long[] insertKey(long[] keys, int index, long key) {
        long[] updatedKeys = Arrays.copyOf(keys, keys.length + 1);
        System.arraycopy(keys, index, updatedKeys, index + 1, keys.length - index);
        updatedKeys[index] = key;
        return updatedKeys;
    }

    private static Object[] insertValue(Object[] values, int index, Object value) {
        Object[] updatedValues = Arrays.copyOf(values, values.length + 1);
        System.arraycopy(values, index, updatedValues, index + 1, values.length - index);
        updatedValues[index] = value;
        return updatedValues;
    }

    private static Node<?>[] insertChild(Node<?>[] children, int index, Node<?> child) {
        Node<?>[] updatedChildren = Arrays.copyOf(children, children.length + 1);
        System.arraycopy(children, index, updatedChildren, index + 1, children.length - index);
        updatedChildren[index] = child;
        return updatedChildren;
    }

    private static long[] removeKey(long[] keys, int index) {
        long[] updatedKeys = Arrays.copyOf(keys, keys.length - 1);
        System.arraycopy(keys, index + 1, updatedKeys, index, keys.length - index - 1);
        return updatedKeys;
    }

    private static Object[] removeValue(Object[] values, int index) {
        Object[] updatedValues = Arrays.copyOf(values, values.length - 1);
        System.arraycopy(values, index + 1, updatedValues, index, values.length - index - 1);
        return updatedValues;
    }

    private static Node<?>[] removeChild(Node<?>[] children, int index) {
        Node<?>[] updatedChildren = Arrays.copyOf(children, children.length - 1);
        System.arraycopy(children, index + 1, updatedChildren, index, children.length - index - 1);
        return updatedChildren;
    }

    private static Node<?>[] replaceChild(Node<?>[] children, int index, Node<?> child) {
        Node<?>[] updatedChildren = children.clone();
        updatedChildren[index] = child;
        return updatedChildren;
    }

    private static boolean canCollapse(Node<?> node) {
        return node instanceof LeafNode<?> || node instanceof CollisionNode<?>;
    }

    @SuppressWarnings("unchecked")
    private static <T> Node<T> castNode(Node<?> node) {
        return (Node<T>) node;
    }

    @SuppressWarnings("unchecked")
    private static <T> T valueAt(Object[] values, int index) {
        return (T) values[index];
    }

    @SuppressWarnings("unchecked")
    private static <T> T valueOf(LeafNode<?> leaf) {
        return (T) leaf.value;
    }

    private static long[] emptyKeys() {
        return EMPTY_KEYS;
    }

    private static Object[] emptyValues() {
        return EMPTY_VALUES;
    }

    @SuppressWarnings("unchecked")
    private static <T> Node<T>[] emptyChildren() {
        return (Node<T>[]) EMPTY_CHILDREN;
    }

    private static <T> Node<T> mergeEntries(long leftKey, long leftHash, T leftValue, long rightKey, long rightHash, T rightValue, int shift) {
        if (shift > MAX_SHIFT) {
            return new CollisionNode<>(leftHash, new long[] {leftKey, rightKey}, new Object[] {leftValue, rightValue});
        }

        int leftChunk = chunk(leftHash, shift);
        int rightChunk = chunk(rightHash, shift);

        if (leftChunk != rightChunk) {
            int leftBit = bitFor(leftChunk);
            int rightBit = bitFor(rightChunk);
            return leftChunk < rightChunk
                    ? new BitmapNode<>(leftBit | rightBit, 0, new long[] {leftKey, rightKey}, new Object[] {leftValue, rightValue}, emptyChildren())
                    : new BitmapNode<>(leftBit | rightBit, 0, new long[] {rightKey, leftKey}, new Object[] {rightValue, leftValue}, emptyChildren());
        }

        Node<T> child = mergeEntries(leftKey, leftHash, leftValue, rightKey, rightHash, rightValue, shift + BITS_PER_LEVEL);
        return new BitmapNode<>(0, bitFor(leftChunk), emptyKeys(), emptyValues(), new Node<?>[] {child});
    }

    private interface Node<T> {

        @Nullable T find(long key, long hash, int shift);

        Node<T> put(long key, long hash, T value, int shift, Change change);

        @Nullable Node<T> remove(long key, long hash, int shift, Change change);
    }

    private static final class Change {

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

    private static final class LeafNode<T> implements Node<T> {

        private final long key;
        private final long hash;
        private final T value;

        private LeafNode(long key, long hash, T value) {
            this.key = key;
            this.hash = hash;
            this.value = value;
        }

        @Override
        public @Nullable T find(long key, long hash, int shift) {
            return this.key == key ? value : null;
        }

        @Override
        public Node<T> put(long key, long hash, T value, int shift, Change change) {
            if (this.key == key) {
                if (this.value == value) {
                    return this;
                }

                change.replaced();
                return new LeafNode<>(key, hash, value);
            }

            change.inserted();
            return mergeEntries(this.key, this.hash, this.value, key, hash, value, shift);
        }

        @Override
        public @Nullable Node<T> remove(long key, long hash, int shift, Change change) {
            if (this.key != key) {
                return this;
            }

            change.removed();
            return null;
        }
    }

    private static final class BitmapNode<T> implements Node<T> {

        private final int dataMap;
        private final int nodeMap;
        private final long[] keys;
        private final Object[] values;
        private final Node<?>[] children;

        private BitmapNode(int dataMap, int nodeMap, long[] keys, Object[] values, Node<?>[] children) {
            this.dataMap = dataMap;
            this.nodeMap = nodeMap;
            this.keys = keys;
            this.values = values;
            this.children = children;
        }

        @Override
        public @Nullable T find(long key, long hash, int shift) {
            int bit = bitFor(chunk(hash, shift));
            if ((dataMap & bit) != 0) {
                int index = entryIndex(dataMap, bit);
                return keys[index] == key ? valueAt(values, index) : null;
            }

            if ((nodeMap & bit) == 0) {
                return null;
            }

            int childIndex = entryIndex(nodeMap, bit);
            return CompactLongObjectPersistentMap.<T>castNode(children[childIndex]).find(key, hash, shift + BITS_PER_LEVEL);
        }

        @Override
        public Node<T> put(long key, long hash, T value, int shift, Change change) {
            int bit = bitFor(chunk(hash, shift));
            if ((dataMap & bit) != 0) {
                int dataIndex = entryIndex(dataMap, bit);
                long existingKey = keys[dataIndex];
                if (existingKey == key) {
                    if (valueAt(values, dataIndex) == value) {
                        return this;
                    }

                    Object[] updatedValues = values.clone();
                    updatedValues[dataIndex] = value;
                    change.replaced();
                    return new BitmapNode<>(dataMap, nodeMap, keys, updatedValues, children);
                }

                change.inserted();
                Node<T> merged =
                        mergeEntries(
                                existingKey,
                                mix(existingKey),
                                CompactLongObjectPersistentMap.<T>valueAt(values, dataIndex),
                                key,
                                hash,
                                value,
                                shift + BITS_PER_LEVEL);
                return new BitmapNode<>(
                        dataMap & ~bit,
                        nodeMap | bit,
                        removeKey(keys, dataIndex),
                        removeValue(values, dataIndex),
                        insertChild(children, entryIndex(nodeMap, bit), merged));
            }

            if ((nodeMap & bit) == 0) {
                change.inserted();
                return new BitmapNode<>(
                        dataMap | bit,
                        nodeMap,
                        insertKey(keys, entryIndex(dataMap, bit), key),
                        insertValue(values, entryIndex(dataMap, bit), value),
                        children);
            }

            int childIndex = entryIndex(nodeMap, bit);
            Node<T> updatedChild = CompactLongObjectPersistentMap.<T>castNode(children[childIndex])
                    .put(key, hash, value, shift + BITS_PER_LEVEL, change);
            if (!change.modified) {
                return this;
            }

            return new BitmapNode<>(dataMap, nodeMap, keys, values, replaceChild(children, childIndex, updatedChild));
        }

        @Override
        public @Nullable Node<T> remove(long key, long hash, int shift, Change change) {
            int bit = bitFor(chunk(hash, shift));
            if ((dataMap & bit) != 0) {
                int dataIndex = entryIndex(dataMap, bit);
                if (keys[dataIndex] != key) {
                    return this;
                }

                change.removed();
                long[] updatedKeys = removeKey(keys, dataIndex);
                Object[] updatedValues = removeValue(values, dataIndex);
                if (updatedKeys.length == 0 && children.length == 0) {
                    return null;
                }
                if (updatedKeys.length == 0
                        && children.length == 1
                        && canCollapse(CompactLongObjectPersistentMap.<T>castNode(children[0]))) {
                    return CompactLongObjectPersistentMap.<T>castNode(children[0]);
                }
                return new BitmapNode<>(dataMap & ~bit, nodeMap, updatedKeys, updatedValues, children);
            }

            if ((nodeMap & bit) == 0) {
                return this;
            }

            int childIndex = entryIndex(nodeMap, bit);
            Node<T> updatedChild = CompactLongObjectPersistentMap.<T>castNode(children[childIndex])
                    .remove(key, hash, shift + BITS_PER_LEVEL, change);
            if (!change.modified) {
                return this;
            }

            if (updatedChild != null) {
                Node<?>[] updatedChildren = replaceChild(children, childIndex, updatedChild);
                if (keys.length == 0
                        && updatedChildren.length == 1
                        && canCollapse(CompactLongObjectPersistentMap.<T>castNode(updatedChildren[0]))) {
                    return CompactLongObjectPersistentMap.<T>castNode(updatedChildren[0]);
                }
                return new BitmapNode<>(dataMap, nodeMap, keys, values, updatedChildren);
            }

            Node<?>[] updatedChildren = removeChild(children, childIndex);
            if (keys.length == 0 && updatedChildren.length == 0) {
                return null;
            }
            if (keys.length == 0
                    && updatedChildren.length == 1
                    && canCollapse(CompactLongObjectPersistentMap.<T>castNode(updatedChildren[0]))) {
                return CompactLongObjectPersistentMap.<T>castNode(updatedChildren[0]);
            }
            return new BitmapNode<>(dataMap, nodeMap & ~bit, keys, values, updatedChildren);
        }
    }

    private static final class CollisionNode<T> implements Node<T> {

        private final long hash;
        private final long[] keys;
        private final Object[] values;

        private CollisionNode(long hash, long[] keys, Object[] values) {
            this.hash = hash;
            this.keys = keys;
            this.values = values;
        }

        @Override
        public @Nullable T find(long key, long hash, int shift) {
            if (this.hash != hash) {
                return null;
            }

            int index = indexOf(key);
            return index < 0 ? null : valueAt(values, index);
        }

        @Override
        public Node<T> put(long key, long hash, T value, int shift, Change change) {
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
                return new CollisionNode<>(hash, keys, updatedValues);
            }

            long[] updatedKeys = Arrays.copyOf(keys, keys.length + 1);
            updatedKeys[keys.length] = key;
            Object[] updatedValues = Arrays.copyOf(values, values.length + 1);
            updatedValues[values.length] = value;
            change.inserted();
            return new CollisionNode<>(hash, updatedKeys, updatedValues);
        }

        @Override
        public @Nullable Node<T> remove(long key, long hash, int shift, Change change) {
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
                return new LeafNode<>(keys[remainingIndex], this.hash, valueAt(values, remainingIndex));
            }

            return new CollisionNode<>(this.hash, removeKey(keys, index), removeValue(values, index));
        }

        private int indexOf(long key) {
            for (int index = 0; index < keys.length; index++) {
                if (keys[index] == key) {
                    return index;
                }
            }

            return -1;
        }
    }

    private static final class ValueIterator<T> implements Iterator<T> {

        private final BitmapNode<?>[] nodeStack = new BitmapNode<?>[MAX_BITMAP_DEPTH];
        private final int[] valueIndices = new int[MAX_BITMAP_DEPTH];
        private final int[] childIndices = new int[MAX_BITMAP_DEPTH];
        private int depth = -1;
        private @Nullable Object[] collisionValues;
        private int collisionIndex;
        private @Nullable T nextValue;

        private ValueIterator(@Nullable Node<T> root) {
            if (root instanceof BitmapNode<?> bitmap) {
                push(bitmap);
                advance();
            } else if (root instanceof LeafNode<?> leaf) {
                nextValue = valueOf(leaf);
            } else if (root instanceof CollisionNode<?> collision) {
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
                BitmapNode<?> node = nodeStack[depth];
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
                    if (child instanceof BitmapNode<?> bitmap) {
                        push(bitmap);
                        continue;
                    }

                    if (child instanceof LeafNode<?> leaf) {
                        nextValue = valueOf(leaf);
                        return;
                    }

                    CollisionNode<?> collision = (CollisionNode<?>) child;
                    collisionValues = collision.values;
                    collisionIndex = 1;
                    nextValue = valueAt(collision.values, 0);
                    return;
                }

                depth--;
            }

            nextValue = null;
        }

        private void push(BitmapNode<?> node) {
            nodeStack[++depth] = node;
            valueIndices[depth] = 0;
            childIndices[depth] = 0;
        }
    }
}
