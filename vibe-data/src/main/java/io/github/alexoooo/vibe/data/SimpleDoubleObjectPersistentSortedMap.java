package io.github.alexoooo.vibe.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import org.jspecify.annotations.Nullable;

public final class SimpleDoubleObjectPersistentSortedMap<T> implements DoubleObjectPersistentSortedMap<T> {

    private final NavigableMap<Double, T> entries;
    private final boolean descending;

    private SimpleDoubleObjectPersistentSortedMap(NavigableMap<Double, T> entries, boolean descending) {
        this.entries = entries;
        this.descending = descending;
    }

    public static <T> SimpleDoubleObjectPersistentSortedMap<T> ascending() {
        return new SimpleDoubleObjectPersistentSortedMap<>(new TreeMap<>(), false);
    }

    public static <T> SimpleDoubleObjectPersistentSortedMap<T> descending() {
        return new SimpleDoubleObjectPersistentSortedMap<>(new TreeMap<>(), true);
    }

    public boolean isDescending() {
        return descending;
    }

    @Override
    public @Nullable T find(double key) {
        return entries.get(key);
    }

    @Override
    public Iterator<T> greaterOrEqualTo(double key) {
        NavigableMap<Double, T> filteredEntries = entries.tailMap(key, true);
        NavigableMap<Double, T> orderedEntries = descending ? filteredEntries.descendingMap() : filteredEntries;
        Collection<T> values = Collections.unmodifiableCollection(orderedEntries.values());
        return values.iterator();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public SimpleDoubleObjectPersistentSortedMap<T> remove(double key) {
        TreeMap<Double, T> updatedEntries = new TreeMap<>(entries);
        updatedEntries.remove(key);
        return new SimpleDoubleObjectPersistentSortedMap<>(updatedEntries, descending);
    }

    @Override
    public SimpleDoubleObjectPersistentSortedMap<T> put(double key, T value) {
        TreeMap<Double, T> updatedEntries = new TreeMap<>(entries);
        updatedEntries.put(key, Objects.requireNonNull(value, "value"));
        return new SimpleDoubleObjectPersistentSortedMap<>(updatedEntries, descending);
    }
}
