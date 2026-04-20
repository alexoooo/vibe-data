package io.github.alexoooo.vibe.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class CompactDoubleObjectPersistentSortedMap<T> implements DoubleObjectPersistentSortedMap<T> {

    private static final long MIX_CONSTANT_1 = 0x9e3779b97f4a7c15L;
    private static final long MIX_CONSTANT_2 = 0xbf58476d1ce4e5b9L;
    private static final long MIX_CONSTANT_3 = 0x94d049bb133111ebL;
    private static final CompactDoubleObjectPersistentSortedMap<?> ASCENDING_EMPTY =
            new CompactDoubleObjectPersistentSortedMap<>(null, false, 0);
    private static final CompactDoubleObjectPersistentSortedMap<?> DESCENDING_EMPTY =
            new CompactDoubleObjectPersistentSortedMap<>(null, true, 0);

    private final @Nullable Node<T> root;
    private final boolean descending;
    private final int size;

    private CompactDoubleObjectPersistentSortedMap(@Nullable Node<T> root, boolean descending, int size) {
        this.root = root;
        this.descending = descending;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <T> CompactDoubleObjectPersistentSortedMap<T> ascending() {
        return (CompactDoubleObjectPersistentSortedMap<T>) ASCENDING_EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <T> CompactDoubleObjectPersistentSortedMap<T> descending() {
        return (CompactDoubleObjectPersistentSortedMap<T>) DESCENDING_EMPTY;
    }

    @Override
    public @Nullable T find(double key) {
        Node<T> current = root;
        while (current != null) {
            int comparison = compare(key, current.key);
            if (comparison == 0) {
                return current.value;
            }

            current = comparison < 0 ? current.left : current.right;
        }

        return null;
    }

    @Override
    public Iterator<T> greaterOrEqualTo(double key) {
        return descending ? new DescendingIterator<>(root, key) : new AscendingIterator<>(root, key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public CompactDoubleObjectPersistentSortedMap<T> remove(double key) {
        Node<T> updatedRoot = remove(root, key);
        if (updatedRoot == root) {
            return this;
        }

        return new CompactDoubleObjectPersistentSortedMap<>(updatedRoot, descending, sizeOf(updatedRoot));
    }

    @Override
    public CompactDoubleObjectPersistentSortedMap<T> put(double key, T value) {
        T requiredValue = Objects.requireNonNull(value, "value");
        Node<T> updatedRoot = put(root, key, requiredValue, priorityOf(key));
        if (updatedRoot == root) {
            return this;
        }

        return new CompactDoubleObjectPersistentSortedMap<>(updatedRoot, descending, sizeOf(updatedRoot));
    }

    private static int compare(double left, double right) {
        return Double.compare(left, right);
    }

    private static int comparePriority(long left, long right) {
        return Long.compareUnsigned(left, right);
    }

    private static long priorityOf(double key) {
        long mixed = Double.doubleToLongBits(key) + MIX_CONSTANT_1;
        mixed = (mixed ^ (mixed >>> 30)) * MIX_CONSTANT_2;
        mixed = (mixed ^ (mixed >>> 27)) * MIX_CONSTANT_3;
        return mixed ^ (mixed >>> 31);
    }

    private static <T> int sizeOf(@Nullable Node<T> node) {
        return node == null ? 0 : node.size;
    }

    private static <T> Node<T> put(@Nullable Node<T> node, double key, T value, long priority) {
        if (node == null) {
            return new Node<>(key, value, priority, null, null);
        }

        int comparison = compare(key, node.key);
        if (comparison == 0) {
            if (node.value == value) {
                return node;
            }

            return new Node<>(node.key, value, node.priority, node.left, node.right);
        }

        if (comparison < 0) {
            Node<T> updatedLeft = put(node.left, key, value, priority);
            if (updatedLeft == node.left) {
                return node;
            }

            Node<T> updated = new Node<>(node.key, node.value, node.priority, updatedLeft, node.right);
            return comparePriority(updatedLeft.priority, updated.priority) > 0 ? rotateRight(updated) : updated;
        }

        Node<T> updatedRight = put(node.right, key, value, priority);
        if (updatedRight == node.right) {
            return node;
        }

        Node<T> updated = new Node<>(node.key, node.value, node.priority, node.left, updatedRight);
        return comparePriority(updatedRight.priority, updated.priority) > 0 ? rotateLeft(updated) : updated;
    }

    private static <T> @Nullable Node<T> remove(@Nullable Node<T> node, double key) {
        if (node == null) {
            return null;
        }

        int comparison = compare(key, node.key);
        if (comparison < 0) {
            Node<T> updatedLeft = remove(node.left, key);
            if (updatedLeft == node.left) {
                return node;
            }

            return new Node<>(node.key, node.value, node.priority, updatedLeft, node.right);
        }

        if (comparison > 0) {
            Node<T> updatedRight = remove(node.right, key);
            if (updatedRight == node.right) {
                return node;
            }

            return new Node<>(node.key, node.value, node.priority, node.left, updatedRight);
        }

        return merge(node.left, node.right);
    }

    private static <T> @Nullable Node<T> merge(@Nullable Node<T> left, @Nullable Node<T> right) {
        if (left == null) {
            return right;
        }

        if (right == null) {
            return left;
        }

        if (comparePriority(left.priority, right.priority) > 0) {
            Node<T> mergedRight = merge(left.right, right);
            if (mergedRight == left.right) {
                return left;
            }

            return new Node<>(left.key, left.value, left.priority, left.left, mergedRight);
        }

        Node<T> mergedLeft = merge(left, right.left);
        if (mergedLeft == right.left) {
            return right;
        }

        return new Node<>(right.key, right.value, right.priority, mergedLeft, right.right);
    }

    private static <T> Node<T> rotateLeft(Node<T> node) {
        Node<T> right = Objects.requireNonNull(node.right, "right");
        Node<T> movedLeft = new Node<>(node.key, node.value, node.priority, node.left, right.left);
        return new Node<>(right.key, right.value, right.priority, movedLeft, right.right);
    }

    private static <T> Node<T> rotateRight(Node<T> node) {
        Node<T> left = Objects.requireNonNull(node.left, "left");
        Node<T> movedRight = new Node<>(node.key, node.value, node.priority, left.right, node.right);
        return new Node<>(left.key, left.value, left.priority, left.left, movedRight);
    }

    private static <T> void pushAscendingPath(@Nullable Node<T> node, double lowerBound, NodeStack<T> stack) {
        while (node != null) {
            if (compare(lowerBound, node.key) <= 0) {
                stack.push(node);
                node = node.left;
            } else {
                node = node.right;
            }
        }
    }

    private static <T> void pushDescendingPath(@Nullable Node<T> node, double lowerBound, NodeStack<T> stack) {
        while (node != null) {
            if (compare(node.key, lowerBound) >= 0) {
                stack.push(node);
            }

            node = node.right;
        }
    }

    private static final class Node<T> {

        private final double key;
        private final T value;
        private final long priority;
        private final int size;
        private final @Nullable Node<T> left;
        private final @Nullable Node<T> right;

        private Node(double key, T value, long priority, @Nullable Node<T> left, @Nullable Node<T> right) {
            this.key = key;
            this.value = value;
            this.priority = priority;
            this.size = 1 + sizeOf(left) + sizeOf(right);
            this.left = left;
            this.right = right;
        }
    }

    private abstract static class AbstractIterator<T> implements Iterator<T> {

        private final double lowerBound;
        private final NodeStack<T> stack = new NodeStack<>();

        private AbstractIterator(@Nullable Node<T> root, double lowerBound) {
            this.lowerBound = lowerBound;
            seed(root);
        }

        @Override
        public final boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public final T next() {
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }

            Node<T> next = stack.pop();
            advance(next);
            return next.value;
        }

        protected final double lowerBound() {
            return lowerBound;
        }

        protected final NodeStack<T> stack() {
            return stack;
        }

        protected abstract void seed(@Nullable Node<T> root);

        protected abstract void advance(Node<T> node);
    }

    private static final class AscendingIterator<T> extends AbstractIterator<T> {

        private AscendingIterator(@Nullable Node<T> root, double lowerBound) {
            super(root, lowerBound);
        }

        @Override
        protected void seed(@Nullable Node<T> root) {
            pushAscendingPath(root, lowerBound(), stack());
        }

        @Override
        protected void advance(Node<T> node) {
            pushAscendingPath(node.right, lowerBound(), stack());
        }
    }

    private static final class DescendingIterator<T> extends AbstractIterator<T> {

        private DescendingIterator(@Nullable Node<T> root, double lowerBound) {
            super(root, lowerBound);
        }

        @Override
        protected void seed(@Nullable Node<T> root) {
            pushDescendingPath(root, lowerBound(), stack());
        }

        @Override
        protected void advance(Node<T> node) {
            pushDescendingPath(node.left, lowerBound(), stack());
        }
    }

    private static final class NodeStack<T> {

        private @Nullable Object[] elements = new @Nullable Object[16];
        private int size;

        private boolean isEmpty() {
            return size == 0;
        }

        private void push(Node<T> node) {
            if (size == elements.length) {
                elements = Arrays.copyOf(elements, elements.length * 2);
            }

            elements[size++] = node;
        }

        private Node<T> pop() {
            Object element = Objects.requireNonNull(elements[--size], "element");
            elements[size] = null;
            @SuppressWarnings("unchecked")
            Node<T> node = (Node<T>) element;
            return node;
        }
    }
}
