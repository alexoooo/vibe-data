package io.github.alexoooo.vibe.data;

import java.util.Iterator;

public interface PersistentVector<T> extends PersistentAppendSequence<T> {

    @Override
    PersistentVector<T> append(T value);

    T get(int index);

    Iterator<T> reverseIterator();
}
