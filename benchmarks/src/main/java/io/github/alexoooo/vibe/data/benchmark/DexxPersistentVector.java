package io.github.alexoooo.vibe.data.benchmark;

import com.github.andrewoma.dexx.collection.Vector;
import io.github.alexoooo.vibe.data.PersistentVector;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class DexxPersistentVector<T> implements PersistentVector<T> {

    private final Vector<T> elements;

    private DexxPersistentVector(Vector<T> elements) {
        this.elements = elements;
    }

    static <T> DexxPersistentVector<T> empty() {
        return new DexxPersistentVector<>(Vector.empty());
    }

    @Override
    public DexxPersistentVector<T> append(T value) {
        return new DexxPersistentVector<>(elements.append(Objects.requireNonNull(value, "value")));
    }

    @Override
    public T first() {
        if (elements.isEmpty()) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return Objects.requireNonNull(elements.first(), "first");
    }

    @Override
    public T get(int index) {
        return elements.get(index);
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
        if (elements.isEmpty()) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return Objects.requireNonNull(elements.last(), "last");
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
