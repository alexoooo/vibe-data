package io.github.alexoooo.vibe.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class ChunkedPersistentAppendSequence<T> implements PersistentAppendSequence<T> {

    private static final int BITS = 5;
    private static final int CHUNK_SIZE = 1 << BITS;
    private static final int MASK = CHUNK_SIZE - 1;
    private static final Object[] EMPTY_NODE = new Object[0];
    private static final ChunkedPersistentAppendSequence<?> EMPTY =
            new ChunkedPersistentAppendSequence<>(0, 0, EMPTY_NODE, 0, EMPTY_NODE, null, null);

    private final int size;
    private final int shift;
    private final Object[] root;
    private final int chunkCount;
    private final Object[] tail;
    private final @Nullable T firstElement;
    private final @Nullable T lastElement;

    private ChunkedPersistentAppendSequence(
            int size,
            int shift,
            Object[] root,
            int chunkCount,
            Object[] tail,
            @Nullable T firstElement,
            @Nullable T lastElement) {
        this.size = size;
        this.shift = shift;
        this.root = root;
        this.chunkCount = chunkCount;
        this.tail = tail;
        this.firstElement = firstElement;
        this.lastElement = lastElement;
    }

    @SuppressWarnings("unchecked")
    public static <T> ChunkedPersistentAppendSequence<T> empty() {
        return (ChunkedPersistentAppendSequence<T>) EMPTY;
    }

    @Override
    public ChunkedPersistentAppendSequence<T> append(T value) {
        T nonNullValue = Objects.requireNonNull(value, "value");
        T updatedFirst = size == 0 ? nonNullValue : requireFirstElement();

        if (tail.length < CHUNK_SIZE) {
            Object[] updatedTail = Arrays.copyOf(tail, tail.length + 1);
            updatedTail[tail.length] = nonNullValue;
            return new ChunkedPersistentAppendSequence<>(
                    size + 1,
                    shift,
                    root,
                    chunkCount,
                    updatedTail,
                    updatedFirst,
                    nonNullValue);
        }
        
        Object[] fullTail = tail;
        Object[] updatedRoot;
        int updatedShift = shift;

        if (chunkCount == chunkCapacity(shift)) {
            updatedRoot = new Object[2];
            updatedRoot[0] = root;
            updatedRoot[1] = newPath(shift, fullTail);
            updatedShift += BITS;
        } else {
            updatedRoot = pushChunk(shift, root, chunkCount, fullTail);
        }

        return new ChunkedPersistentAppendSequence<>(
                size + 1,
                updatedShift,
                updatedRoot,
                chunkCount + 1,
                new Object[]{nonNullValue},
                updatedFirst,
                nonNullValue);
    }

    @Override
    public T first() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return requireFirstElement();
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
        return Objects.requireNonNull(lastElement, "lastElement");
    }

    @Override
    public int size() {
        return size;
    }

    private static int chunkCapacity(int shift) {
        return 1 << (shift + BITS);
    }

    private static Object[] newPath(int level, Object[] chunk) {
        if (level == 0) {
            return new Object[]{chunk};
        }
        return new Object[]{newPath(level - BITS, chunk)};
    }

    private static Object[] pushChunk(int level, Object[] parent, int chunkIndex, Object[] chunk) {
        int subIndex = (chunkIndex >>> level) & MASK;
        Object[] updatedParent = subIndex < parent.length ? parent.clone() : Arrays.copyOf(parent, subIndex + 1);

        if (level == 0) {
            updatedParent[subIndex] = chunk;
            return updatedParent;
        }

        Object[] child = subIndex < parent.length ? asNode(parent[subIndex]) : EMPTY_NODE;
        updatedParent[subIndex] = child.length == 0
                ? newPath(level - BITS, chunk)
                : pushChunk(level - BITS, child, chunkIndex, chunk);
        return updatedParent;
    }

    private static Object[] asNode(Object value) {
        return (Object[]) value;
    }

    private T requireFirstElement() {
        return Objects.requireNonNull(firstElement, "firstElement");
    }

    private Object[] chunkAt(int chunkIndex) {
        Object[] node = root;
        for (int level = shift; level > 0; level -= BITS) {
            node = asNode(node[(chunkIndex >>> level) & MASK]);
        }
        return asNode(node[chunkIndex & MASK]);
    }

    @SuppressWarnings("unchecked")
    private T elementAt(Object[] chunk, int index) {
        return (T) chunk[index];
    }

    private final class ForwardIterator implements Iterator<T> {

        private final int tailStart = size - tail.length;
        private int nextIndex;
        private Object[] chunk = EMPTY_NODE;
        private int chunkStart;

        private ForwardIterator() {
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
            if (index >= tailStart) {
                chunk = tail;
                chunkStart = tailStart;
            } else {
                int chunkIndex = index >>> BITS;
                chunk = chunkAt(chunkIndex);
                chunkStart = chunkIndex << BITS;
            }
        }
    }
}
