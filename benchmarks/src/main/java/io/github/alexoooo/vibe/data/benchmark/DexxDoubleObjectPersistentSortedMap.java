package io.github.alexoooo.vibe.data.benchmark;

import com.github.andrewoma.dexx.collection.SortedMap;
import com.github.andrewoma.dexx.collection.TreeMap;
import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class DexxDoubleObjectPersistentSortedMap<T> implements DoubleObjectPersistentSortedMap<T> {

    private final TreeMap<Double, T> entries;
    private final boolean descending;

    private DexxDoubleObjectPersistentSortedMap(TreeMap<Double, T> entries, boolean descending) {
        this.entries = entries;
        this.descending = descending;
    }

    static <T> DexxDoubleObjectPersistentSortedMap<T> ascending() {
        return new DexxDoubleObjectPersistentSortedMap<>(new TreeMap<>(), false);
    }

    static <T> DexxDoubleObjectPersistentSortedMap<T> descending() {
        return new DexxDoubleObjectPersistentSortedMap<>(
                new TreeMap<Double, T>(Comparator.reverseOrder(), null),
                true);
    }

    boolean isDescending() {
        return descending;
    }

    @Override
    public @Nullable T find(double key) {
        return entries.get(key);
    }

    @Override
    public Iterator<T> greaterOrEqualTo(double key) {
        SortedMap<Double, T> filteredEntries = descending ? entries.to(key, true) : entries.from(key, true);
        return filteredEntries.values().iterator();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public DexxDoubleObjectPersistentSortedMap<T> remove(double key) {
        return new DexxDoubleObjectPersistentSortedMap<>(entries.remove(key), descending);
    }

    @Override
    public DexxDoubleObjectPersistentSortedMap<T> put(double key, T value) {
        return new DexxDoubleObjectPersistentSortedMap<>(
                entries.put(key, Objects.requireNonNull(value, "value")),
                descending);
    }
}
