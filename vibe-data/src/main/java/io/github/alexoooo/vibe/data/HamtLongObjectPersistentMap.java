package io.github.alexoooo.vibe.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class HamtLongObjectPersistentMap<T> implements LongObjectPersistentMap<T> {

    private static final long MIX_CONSTANT_1 = 0x9e3779b97f4a7c15L;
    private static final long MIX_CONSTANT_2 = 0xbf58476d1ce4e5b9L;
    private static final long MIX_CONSTANT_3 = 0x94d049bb133111ebL;
    private static final int BITS_PER_LEVEL = 6;
    private static final int BIT_MASK = (1 << BITS_PER_LEVEL) - 1;
    private static final int MAX_SHIFT = 60;
    private static final int MAX_BITMAP_DEPTH = (Long.SIZE + BITS_PER_LEVEL - 1) / BITS_PER_LEVEL;
    private static final HamtLongObjectPersistentMap<?> EMPTY = new HamtLongObjectPersistentMap<>(null, 0);

    private final @Nullable Node<T> root;
    private final int size;

    private HamtLongObjectPersistentMap(@Nullable Node<T> root, int size) {
        this.root = root;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <T> HamtLongObjectPersistentMap<T> empty() {
        return (HamtLongObjectPersistentMap<T>) EMPTY;
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
    public HamtLongObjectPersistentMap<T> remove(long key) {
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
        return updatedSize == 0 ? empty() : new HamtLongObjectPersistentMap<>(updatedRoot, updatedSize);
    }

    @Override
    public HamtLongObjectPersistentMap<T> put(long key, T value) {
        T requiredValue = Objects.requireNonNull(value, "value");
        long hash = mix(key);
        Node<T> currentRoot = root;
        if (currentRoot == null) {
            return new HamtLongObjectPersistentMap<>(new LeafNode<>(key, hash, requiredValue), 1);
        }

        Change change = new Change();
        Node<T> updatedRoot = currentRoot.put(key, hash, requiredValue, 0, change);
        if (!change.modified) {
            return this;
        }

        return new HamtLongObjectPersistentMap<>(updatedRoot, size + change.sizeDelta);
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

    private static long bitFor(int chunk) {
        return 1L << chunk;
    }

    private static int entryIndex(long bitmap, long bit) {
        return Long.bitCount(bitmap & (bit - 1L));
    }

    private static Node<?>[] insertEntry(Node<?>[] entries, int index, Node<?> entry) {
        Node<?>[] updatedEntries = Arrays.copyOf(entries, entries.length + 1);
        System.arraycopy(entries, index, updatedEntries, index + 1, entries.length - index);
        updatedEntries[index] = entry;
        return updatedEntries;
    }

    private static Node<?>[] removeEntry(Node<?>[] entries, int index) {
        Node<?>[] updatedEntries = Arrays.copyOf(entries, entries.length - 1);
        System.arraycopy(entries, index + 1, updatedEntries, index, entries.length - index - 1);
        return updatedEntries;
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

    private static <T> Node<T> mergeLeaves(LeafNode<T> left, LeafNode<T> right, int shift) {
        if (shift > MAX_SHIFT) {
            return new CollisionNode<>(
                    left.hash,
                    new long[] {left.key, right.key},
                    new Object[] {left.value, right.value});
        }

        int leftChunk = chunk(left.hash, shift);
        int rightChunk = chunk(right.hash, shift);

        if (leftChunk != rightChunk) {
            long leftBit = bitFor(leftChunk);
            long rightBit = bitFor(rightChunk);
            Node<?>[] entries = leftChunk < rightChunk
                    ? new Node<?>[] {left, right}
                    : new Node<?>[] {right, left};
            return new BitmapNode<>(leftBit | rightBit, entries);
        }

        Node<T> child = mergeLeaves(left, right, shift + BITS_PER_LEVEL);
        return new BitmapNode<>(bitFor(leftChunk), new Node<?>[] {child});
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
            return mergeLeaves(this, new LeafNode<>(key, hash, value), shift);
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

        private final long bitmap;
        private final Node<?>[] entries;

        private BitmapNode(long bitmap, Node<?>[] entries) {
            this.bitmap = bitmap;
            this.entries = entries;
        }

        @Override
        public @Nullable T find(long key, long hash, int shift) {
            long bit = bitFor(chunk(hash, shift));
            if ((bitmap & bit) == 0L) {
                return null;
            }

            Node<T> entry = castNode(entries[entryIndex(bitmap, bit)]);
            return entry.find(key, hash, shift + BITS_PER_LEVEL);
        }

        @Override
        public Node<T> put(long key, long hash, T value, int shift, Change change) {
            long bit = bitFor(chunk(hash, shift));
            int index = entryIndex(bitmap, bit);

            if ((bitmap & bit) == 0L) {
                change.inserted();
                return new BitmapNode<>(bitmap | bit, insertEntry(entries, index, new LeafNode<>(key, hash, value)));
            }

            Node<T> entry = castNode(entries[index]);
            Node<T> updatedEntry = entry.put(key, hash, value, shift + BITS_PER_LEVEL, change);
            if (!change.modified) {
                return this;
            }

            Node<?>[] updatedEntries = entries.clone();
            updatedEntries[index] = updatedEntry;
            return new BitmapNode<>(bitmap, updatedEntries);
        }

        @Override
        public @Nullable Node<T> remove(long key, long hash, int shift, Change change) {
            long bit = bitFor(chunk(hash, shift));
            if ((bitmap & bit) == 0L) {
                return this;
            }

            int index = entryIndex(bitmap, bit);
            Node<T> entry = castNode(entries[index]);
            Node<T> updatedEntry = entry.remove(key, hash, shift + BITS_PER_LEVEL, change);
            if (!change.modified) {
                return this;
            }

            if (updatedEntry != null) {
                if (entries.length == 1 && canCollapse(updatedEntry)) {
                    return updatedEntry;
                }

                Node<?>[] updatedEntries = entries.clone();
                updatedEntries[index] = updatedEntry;
                return new BitmapNode<>(bitmap, updatedEntries);
            }

            if (entries.length == 1) {
                return null;
            }

            Node<?>[] updatedEntries = removeEntry(entries, index);
            if (updatedEntries.length == 1 && canCollapse(updatedEntries[0])) {
                return castNode(updatedEntries[0]);
            }

            return new BitmapNode<>(bitmap & ~bit, updatedEntries);
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
        private final int[] entryIndices = new int[MAX_BITMAP_DEPTH];
        private int depth = -1;
        private @Nullable Object[] collisionValues;
        private int collisionIndex;
        private @Nullable T nextValue;

        private ValueIterator(@Nullable Node<T> root) {
            if (root instanceof BitmapNode<?> bitmap) {
                nodeStack[++depth] = bitmap;
                entryIndices[depth] = 0;
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
                int index = entryIndices[depth];
                if (index >= node.entries.length) {
                    depth--;
                    continue;
                }

                entryIndices[depth] = index + 1;
                Node<?> entry = node.entries[index];
                if (entry instanceof BitmapNode<?> bitmap) {
                    nodeStack[++depth] = bitmap;
                    entryIndices[depth] = 0;
                    continue;
                }

                if (entry instanceof LeafNode<?> leaf) {
                    nextValue = valueOf(leaf);
                    return;
                }

                CollisionNode<?> collision = (CollisionNode<?>) entry;
                collisionValues = collision.values;
                collisionIndex = 1;
                nextValue = valueAt(collision.values, 0);
                return;
            }

            nextValue = null;
        }
    }
}
