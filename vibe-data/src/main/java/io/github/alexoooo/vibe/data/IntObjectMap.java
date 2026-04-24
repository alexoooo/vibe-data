package io.github.alexoooo.vibe.data;

import org.jspecify.annotations.Nullable;

public interface IntObjectMap<T> extends Iterable<T> {

    @Nullable T find(int key);

    int size();
}
