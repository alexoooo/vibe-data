package io.github.alexoooo.vibe.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class ChunkedPersistentVector<T> implements PersistentVector<T> {

    private static final int BITS = 5;
    private static final int BRANCH_FACTOR = 1 << BITS;
    private static final int MASK = BRANCH_FACTOR - 1;
    private static final Object[] EMPTY_NODE = new Object[0];
    private static final ChunkedPersistentVector<?> EMPTY =
            new ChunkedPersistentVector<>(0, BITS, EMPTY_NODE, EMPTY_NODE, null);

    private final int size;
    private final int shift;
    private final Object[] root;
    private final Object[] tail;
    private final @Nullable T firstElement;

    private ChunkedPersistentVector(int size, int shift, Object[] root, Object[] tail, @Nullable T firstElement) {
        this.size = size;
        this.shift = shift;
        this.root = root;
        this.tail = tail;
        this.firstElement = firstElement;
    }

    @SuppressWarnings("unchecked")
    public static <T> ChunkedPersistentVector<T> empty() {
        return (ChunkedPersistentVector<T>) EMPTY;
    }

    @Override
    public ChunkedPersistentVector<T> append(T value) {
        T nonNullValue = Objects.requireNonNull(value, "value");
        T updatedFirst = size == 0 ? nonNullValue : requireFirstElement();

        if (tail.length < BRANCH_FACTOR) {
            Object[] updatedTail = Arrays.copyOf(tail, tail.length + 1);
            updatedTail[tail.length] = nonNullValue;
            return new ChunkedPersistentVector<>(size + 1, shift, root, updatedTail, updatedFirst);
        }

        Object[] updatedRoot;
        int updatedShift = shift;

        if ((size >>> BITS) > (1 << shift)) {
            updatedRoot = new Object[2];
            updatedRoot[0] = root;
            updatedRoot[1] = newPath(shift, tail);
            updatedShift += BITS;
        } else {
            updatedRoot = pushTail(shift, root, size, tail);
        }

        return new ChunkedPersistentVector<>(
                size + 1,
                updatedShift,
                updatedRoot,
                new Object[]{nonNullValue},
                updatedFirst);
    }

    @Override
    public T first() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return requireFirstElement();
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        if (index >= tailOffset()) {
            return elementAt(tail, index - tailOffset());
        }
        Object[] leaf = leafFor(index);
        return elementAt(leaf, index & MASK);
    }

    @Override
    public Iterator<T> iterator() {
        return new ForwardIterator();
    }

    @Override
    public T last() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return elementAt(tail, tail.length - 1);
    }

    @Override
    public Iterator<T> reverseIterator() {
        return new ReverseIterator();
    }

    @Override
    public int size() {
        return size;
    }

    private static Object[] newPath(int level, Object[] node) {
        if (level == BITS) {
            return new Object[]{node};
        }
        return new Object[]{newPath(level - BITS, node)};
    }

    private static Object[] pushTail(int level, Object[] parent, int count, Object[] tailNode) {
        int subIndex = ((count - 1) >>> level) & MASK;
        Object[] updatedParent = subIndex < parent.length ? parent.clone() : Arrays.copyOf(parent, subIndex + 1);

        if (level == BITS) {
            updatedParent[subIndex] = tailNode;
            return updatedParent;
        }

        Object[] child = subIndex < parent.length ? asNode(parent[subIndex]) : EMPTY_NODE;
        updatedParent[subIndex] = child.length == 0
                ? newPath(level - BITS, tailNode)
                : pushTail(level - BITS, child, count, tailNode);
        return updatedParent;
    }

    private static Object[] asNode(Object value) {
        return (Object[]) value;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index + " must be within [0, " + size + ")");
        }
    }

    private Object[] chunkFor(int index) {
        return index >= tailOffset() ? tail : leafFor(index);
    }

    private int chunkStart(int index) {
        return index >= tailOffset() ? tailOffset() : index - (index & MASK);
    }

    @SuppressWarnings("unchecked")
    private T elementAt(Object[] chunk, int index) {
        return (T) chunk[index];
    }

    private Object[] leafFor(int index) {
        Object[] node = root;
        for (int level = shift; level > BITS; level -= BITS) {
            node = asNode(node[(index >>> level) & MASK]);
        }
        return asNode(node[(index >>> BITS) & MASK]);
    }

    private T requireFirstElement() {
        return Objects.requireNonNull(firstElement, "firstElement");
    }

    private int tailOffset() {
        return size - tail.length;
    }

    private final class ForwardIterator implements Iterator<T> {

        private int nextIndex;
        private Object[] chunk;
        private int chunkStart;

        private ForwardIterator() {
            chunk = EMPTY_NODE;
            if (size > 0) {
                loadChunk(0);
            }
        }

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T value = elementAt(chunk, nextIndex - chunkStart);
            nextIndex++;
            if (nextIndex < size && nextIndex == chunkStart + chunk.length) {
                loadChunk(nextIndex);
            }
            return value;
        }

        private void loadChunk(int index) {
            chunk = chunkFor(index);
            chunkStart = chunkStart(index);
        }
    }

    private final class ReverseIterator implements Iterator<T> {

        private int nextIndex = size - 1;
        private Object[] chunk;
        private int chunkStart;

        private ReverseIterator() {
            chunk = EMPTY_NODE;
            if (size > 0) {
                loadChunk(nextIndex);
            }
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T value = elementAt(chunk, nextIndex - chunkStart);
            nextIndex--;
            if (nextIndex >= 0 && nextIndex < chunkStart) {
                loadChunk(nextIndex);
            }
            return value;
        }

        private void loadChunk(int index) {
            chunk = chunkFor(index);
            chunkStart = chunkStart(index);
        }
    }
}
