package io.github.alexoooo.vibe.data;

public interface PersistentAppendSequence<T> extends Iterable<T> {

    PersistentAppendSequence<T> append(T value);

    T first();

    T last();

    int size();
}
