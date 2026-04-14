package io.github.alexoooo.vibe.data;

import java.util.Iterator;

public final class SimplePersistentAppendSequence<T> implements PersistentAppendSequence<T> {

    private static final SimplePersistentAppendSequence<?> EMPTY =
            new SimplePersistentAppendSequence<>(SimplePersistentVector.empty());

    private final SimplePersistentVector<T> vector;

    private SimplePersistentAppendSequence(SimplePersistentVector<T> vector) {
        this.vector = vector;
    }

    @SuppressWarnings("unchecked")
    public static <T> SimplePersistentAppendSequence<T> empty() {
        return (SimplePersistentAppendSequence<T>) EMPTY;
    }

    @Override
    public SimplePersistentAppendSequence<T> append(T value) {
        return new SimplePersistentAppendSequence<>(vector.append(value));
    }

    @Override
    public T first() {
        return vector.first();
    }

    @Override
    public Iterator<T> iterator() {
        return vector.iterator();
    }

    @Override
    public T last() {
        return vector.last();
    }

    @Override
    public int size() {
        return vector.size();
    }
}
