package io.github.alexoooo.vibe.data;

import java.util.Iterator;
import org.jspecify.annotations.Nullable;

public interface DoubleObjectSortedMap<T> {

    @Nullable T find(double key);

    Iterator<T> greaterOrEqualTo(double key);

    int size();
}
