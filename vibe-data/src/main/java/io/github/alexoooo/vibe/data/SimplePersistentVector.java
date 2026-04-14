package io.github.alexoooo.vibe.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class SimplePersistentVector<T> implements PersistentVector<T> {

    private static final SimplePersistentVector<?> EMPTY = new SimplePersistentVector<>(List.of());

    private final List<T> elements;

    private SimplePersistentVector(List<T> elements) {
        this.elements = elements;
    }

    @SuppressWarnings("unchecked")
    public static <T> SimplePersistentVector<T> empty() {
        return (SimplePersistentVector<T>) EMPTY;
    }

    @Override
    public SimplePersistentVector<T> append(T value) {
        ArrayList<T> updated = new ArrayList<>(elements.size() + 1);
        updated.addAll(elements);
        updated.add(Objects.requireNonNull(value, "value"));
        return new SimplePersistentVector<>(List.copyOf(updated));
    }

    @Override
    public T first() {
        if (elements.isEmpty()) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return elements.get(0);
    }

    @Override
    public T last() {
        if (elements.isEmpty()) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return elements.get(elements.size() - 1);
    }

    @Override
    public T get(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public Iterator<T> reverseIterator() {
        return new Iterator<>() {
            private int nextIndex = elements.size() - 1;

            @Override
            public boolean hasNext() {
                return nextIndex >= 0;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements.get(nextIndex--);
            }
        };
    }

    @Override
    public int size() {
        return elements.size();
    }
}
