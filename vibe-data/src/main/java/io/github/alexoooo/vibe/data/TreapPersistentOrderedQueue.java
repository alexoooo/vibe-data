package io.github.alexoooo.vibe.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class TreapPersistentOrderedQueue<T> implements PersistentOrderedQueue<T> {

    private static final long MIX_CONSTANT_1 = 0x9e3779b97f4a7c15L;
    private static final long MIX_CONSTANT_2 = 0xbf58476d1ce4e5b9L;
    private static final long MIX_CONSTANT_3 = 0x94d049bb133111ebL;

    private final Comparator<? super T> comparator;
    private final @Nullable Node<T> root;
    private final int size;
    private final @Nullable T firstElement;
    private final @Nullable T lastElement;

    private TreapPersistentOrderedQueue(
            Comparator<? super T> comparator,
            @Nullable Node<T> root,
            int size,
            @Nullable T firstElement,
            @Nullable T lastElement) {
        this.comparator = comparator;
        this.root = root;
        this.size = size;
        this.firstElement = firstElement;
        this.lastElement = lastElement;
    }

    public static <T extends Comparable<? super T>> TreapPersistentOrderedQueue<T> empty() {
        return new TreapPersistentOrderedQueue<>(naturalOrderComparator(), null, 0, null, null);
    }

    public static <T> TreapPersistentOrderedQueue<T> empty(Comparator<? super T> comparator) {
        return new TreapPersistentOrderedQueue<>(Objects.requireNonNull(comparator, "comparator"), null, 0, null, null);
    }

    @Override
    public TreapPersistentOrderedQueue<T> add(T value) {
        T requiredValue = Objects.requireNonNull(value, "value");
        Node<T> updatedRoot = put(root, requiredValue, priorityOf(requiredValue), comparator);
        if (updatedRoot == root) {
            return this;
        }

        T updatedFirst = size == 0 || compare(requiredValue, require(firstElement), comparator) <= 0
                ? requiredValue
                : require(firstElement);
        T updatedLast = size == 0 || compare(requiredValue, require(lastElement), comparator) >= 0
                ? requiredValue
                : require(lastElement);
        return new TreapPersistentOrderedQueue<>(
                comparator,
                updatedRoot,
                sizeOf(updatedRoot),
                updatedFirst,
                updatedLast);
    }

    @Override
    public T first() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return require(firstElement);
    }

    @Override
    public Iterator<T> iterator() {
        return new AscendingIterator<>(root);
    }

    @Override
    public T last() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return require(lastElement);
    }

    @Override
    public TreapPersistentOrderedQueue<T> remove(T value) {
        T requiredValue = Objects.requireNonNull(value, "value");
        Node<T> updatedRoot = remove(root, requiredValue, comparator);
        if (updatedRoot == root) {
            return this;
        }

        if (updatedRoot == null) {
            return new TreapPersistentOrderedQueue<>(comparator, null, 0, null, null);
        }

        boolean updatedFirstRequired = compare(requiredValue, require(firstElement), comparator) == 0;
        boolean updatedLastRequired = compare(requiredValue, require(lastElement), comparator) == 0;
        T updatedFirst = updatedFirstRequired ? leftmost(updatedRoot).value : require(firstElement);
        T updatedLast = updatedLastRequired ? rightmost(updatedRoot).value : require(lastElement);
        return new TreapPersistentOrderedQueue<>(
                comparator,
                updatedRoot,
                sizeOf(updatedRoot),
                updatedFirst,
                updatedLast);
    }

    @Override
    public TreapPersistentOrderedQueue<T> replace(T remove, T add) {
        return remove(remove).add(add);
    }

    @Override
    public int size() {
        return size;
    }

    private static <T> int compare(T left, T right, Comparator<? super T> comparator) {
        return comparator.compare(left, right);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<? super T>> Comparator<? super T> naturalOrderComparator() {
        return (Comparator<? super T>) Comparator.naturalOrder();
    }

    private static long priorityOf(Object value) {
        long mixed = Integer.toUnsignedLong(value.hashCode()) + MIX_CONSTANT_1;
        mixed = (mixed ^ (mixed >>> 30)) * MIX_CONSTANT_2;
        mixed = (mixed ^ (mixed >>> 27)) * MIX_CONSTANT_3;
        return mixed ^ (mixed >>> 31);
    }

    private static <T> T require(@Nullable T value) {
        return Objects.requireNonNull(value, "value");
    }

    private static <T> int sizeOf(@Nullable Node<T> node) {
        return node == null ? 0 : node.size;
    }

    private static <T> Node<T> put(@Nullable Node<T> node, T value, long priority, Comparator<? super T> comparator) {
        if (node == null) {
            return new Node<>(value, priority, null, null);
        }

        int comparison = compare(value, node.value, comparator);
        if (comparison == 0) {
            return node.value == value ? node : new Node<>(value, node.priority, node.left, node.right);
        }

        if (comparison < 0) {
            Node<T> updatedLeft = put(node.left, value, priority, comparator);
            if (updatedLeft == node.left) {
                return node;
            }

            Node<T> updated = new Node<>(node.value, node.priority, updatedLeft, node.right);
            return comparePriority(updatedLeft.priority, updated.priority) > 0
                    ? rotateRight(updated)
                    : updated;
        }

        Node<T> updatedRight = put(node.right, value, priority, comparator);
        if (updatedRight == node.right) {
            return node;
        }

        Node<T> updated = new Node<>(node.value, node.priority, node.left, updatedRight);
        return comparePriority(updatedRight.priority, updated.priority) > 0
                ? rotateLeft(updated)
                : updated;
    }

    private static int comparePriority(long left, long right) {
        return Long.compareUnsigned(left, right);
    }

    private static <T> @Nullable Node<T> remove(@Nullable Node<T> node, T value, Comparator<? super T> comparator) {
        if (node == null) {
            return null;
        }

        int comparison = compare(value, node.value, comparator);
        if (comparison < 0) {
            Node<T> updatedLeft = remove(node.left, value, comparator);
            if (updatedLeft == node.left) {
                return node;
            }

            return new Node<>(node.value, node.priority, updatedLeft, node.right);
        }

        if (comparison > 0) {
            Node<T> updatedRight = remove(node.right, value, comparator);
            if (updatedRight == node.right) {
                return node;
            }

            return new Node<>(node.value, node.priority, node.left, updatedRight);
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
            return new Node<>(left.value, left.priority, left.left, mergedRight);
        }

        Node<T> mergedLeft = merge(left, right.left);
        if (mergedLeft == right.left) {
            return right;
        }
        return new Node<>(right.value, right.priority, mergedLeft, right.right);
    }

    private static <T> Node<T> leftmost(Node<T> node) {
        Node<T> current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private static <T> Node<T> rightmost(Node<T> node) {
        Node<T> current = node;
        while (current.right != null) {
            current = current.right;
        }
        return current;
    }

    private static <T> Node<T> rotateLeft(Node<T> node) {
        Node<T> right = Objects.requireNonNull(node.right, "right");
        Node<T> movedLeft = new Node<>(node.value, node.priority, node.left, right.left);
        return new Node<>(right.value, right.priority, movedLeft, right.right);
    }

    private static <T> Node<T> rotateRight(Node<T> node) {
        Node<T> left = Objects.requireNonNull(node.left, "left");
        Node<T> movedRight = new Node<>(node.value, node.priority, left.right, node.right);
        return new Node<>(left.value, left.priority, left.left, movedRight);
    }

    private static <T> void pushLeftPath(@Nullable Node<T> node, NodeStack<T> stack) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    private static final class Node<T> {

        private final T value;
        private final long priority;
        private final int size;
        private final @Nullable Node<T> left;
        private final @Nullable Node<T> right;

        private Node(T value, long priority, @Nullable Node<T> left, @Nullable Node<T> right) {
            this.value = value;
            this.priority = priority;
            this.size = 1 + sizeOf(left) + sizeOf(right);
            this.left = left;
            this.right = right;
        }
    }

    private static final class AscendingIterator<T> implements Iterator<T> {

        private final NodeStack<T> stack = new NodeStack<>();

        private AscendingIterator(@Nullable Node<T> root) {
            pushLeftPath(root, stack);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }

            Node<T> next = stack.pop();
            pushLeftPath(next.right, stack);
            return next.value;
        }
    }

    @SuppressWarnings("unchecked")
    private static final class NodeStack<T> {

        private @Nullable Node<T>[] elements = new Node[16];
        private int size;

        private boolean isEmpty() {
            return size == 0;
        }

        private Node<T> pop() {
            int index = --size;
            Node<T> node = require(elements[index]);
            elements[index] = null;
            return node;
        }

        private void push(Node<T> node) {
            if (size == elements.length) {
                elements = Arrays.copyOf(elements, elements.length * 2);
            }

            elements[size++] = node;
        }
    }
}
