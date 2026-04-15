package io.github.alexoooo.vibe.data;

public interface OrderedQueue<T> extends Iterable<T> {

    T first();

    T last();

    int size();
}
