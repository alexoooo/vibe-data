package io.github.alexoooo.vibe.data.benchmark;

import com.github.andrewoma.dexx.collection.Vector;
import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class DexxPersistentAppendSequence<T> implements PersistentAppendSequence<T> {

    private final Vector<T> elements;

    private DexxPersistentAppendSequence(Vector<T> elements) {
        this.elements = elements;
    }

    static <T> DexxPersistentAppendSequence<T> empty() {
        return new DexxPersistentAppendSequence<>(Vector.empty());
    }

    @Override
    public DexxPersistentAppendSequence<T> append(T value) {
        return new DexxPersistentAppendSequence<>(elements.append(Objects.requireNonNull(value, "value")));
    }

    @Override
    public T first() {
        if (elements.isEmpty()) {
            throw new IndexOutOfBoundsException("empty sequence");
        }
        return Objects.requireNonNull(elements.first(), "first");
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
    public int size() {
        return elements.size();
    }
}
