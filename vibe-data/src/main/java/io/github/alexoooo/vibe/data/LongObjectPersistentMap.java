package io.github.alexoooo.vibe.data;

public interface LongObjectPersistentMap<T> extends LongObjectMap<T> {

    LongObjectPersistentMap<T> remove(long key);

    LongObjectPersistentMap<T> put(long key, T value);
}
