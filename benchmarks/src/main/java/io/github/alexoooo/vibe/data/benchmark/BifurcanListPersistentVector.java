package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentVector;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class BifurcanListPersistentVector<T> implements PersistentVector<T> {

    private final io.lacuna.bifurcan.List<T> elements;

    private BifurcanListPersistentVector(io.lacuna.bifurcan.List<T> elements) {
        this.elements = elements;
    }

    static <T> BifurcanListPersistentVector<T> empty() {
        return new BifurcanListPersistentVector<>(io.lacuna.bifurcan.List.empty());
    }

    @Override
    public BifurcanListPersistentVector<T> append(T value) {
        io.lacuna.bifurcan.List<T> updated = elements.addLast(Objects.requireNonNull(value, "value"));
        return updated == elements ? this : new BifurcanListPersistentVector<>(updated);
    }

    @Override
    public T first() {
        return elements.first();
    }

    @Override
    public T get(int index) {
        return elements.nth(index);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> delegate = elements.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return delegate.next();
            }
        };
    }

    @Override
    public T last() {
        return elements.last();
    }

    @Override
    public Iterator<T> reverseIterator() {
        return new Iterator<>() {
            private long nextIndex = elements.size() - 1;

            @Override
            public boolean hasNext() {
                return nextIndex >= 0;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements.nth(nextIndex--);
            }
        };
    }

    @Override
    public int size() {
        return Math.toIntExact(elements.size());
    }
}
