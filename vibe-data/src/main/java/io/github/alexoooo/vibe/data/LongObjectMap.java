package io.github.alexoooo.vibe.data;

import org.jspecify.annotations.Nullable;

public interface LongObjectMap<T> extends Iterable<T> {

    @Nullable T find(long key);

    int size();
}
