package io.github.alexoooo.vibe.data;

public interface PersistentOrderedQueue<T> extends OrderedQueue<T> {

    PersistentOrderedQueue<T> add(T value);

    PersistentOrderedQueue<T> remove(T value);

    PersistentOrderedQueue<T> replace(T remove, T add);
}
