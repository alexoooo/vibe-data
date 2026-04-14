package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class BifurcanListPersistentAppendSequence<T> implements PersistentAppendSequence<T> {

    private final io.lacuna.bifurcan.List<T> elements;

    private BifurcanListPersistentAppendSequence(io.lacuna.bifurcan.List<T> elements) {
        this.elements = elements;
    }

    static <T> BifurcanListPersistentAppendSequence<T> empty() {
        return new BifurcanListPersistentAppendSequence<>(io.lacuna.bifurcan.List.empty());
    }

    @Override
    public BifurcanListPersistentAppendSequence<T> append(T value) {
        io.lacuna.bifurcan.List<T> updated = elements.addLast(Objects.requireNonNull(value, "value"));
        return updated == elements ? this : new BifurcanListPersistentAppendSequence<>(updated);
    }

    @Override
    public T first() {
        return elements.first();
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
    public int size() {
        return Math.toIntExact(elements.size());
    }
}
