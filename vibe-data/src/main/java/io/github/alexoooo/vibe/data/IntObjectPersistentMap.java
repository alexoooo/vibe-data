package io.github.alexoooo.vibe.data;

public interface IntObjectPersistentMap<T> extends IntObjectMap<T> {

    IntObjectPersistentMap<T> remove(int key);

    IntObjectPersistentMap<T> put(int key, T value);
}
