package io.github.alexoooo.vibe.data;

public interface DoubleObjectPersistentSortedMap<T> extends DoubleObjectSortedMap<T> {

    DoubleObjectPersistentSortedMap<T> remove(double key);

    DoubleObjectPersistentSortedMap<T> put(double key, T value);
}
