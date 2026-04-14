package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.lacuna.bifurcan.SortedMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import org.jspecify.annotations.Nullable;

final class BifurcanSortedMapDoubleObjectPersistentSortedMap<T> implements DoubleObjectPersistentSortedMap<T> {

    private final SortedMap<Double, T> entries;
    private final boolean descending;

    private BifurcanSortedMapDoubleObjectPersistentSortedMap(SortedMap<Double, T> entries, boolean descending) {
        this.entries = entries;
        this.descending = descending;
    }

    static <T> BifurcanSortedMapDoubleObjectPersistentSortedMap<T> ascending() {
        return new BifurcanSortedMapDoubleObjectPersistentSortedMap<>(new SortedMap<>(), false);
    }

    static <T> BifurcanSortedMapDoubleObjectPersistentSortedMap<T> descending() {
        return new BifurcanSortedMapDoubleObjectPersistentSortedMap<>(new SortedMap<>(), true);
    }

    @Override
    public @Nullable T find(double key) {
        return entries.get(key, null);
    }

    @Override
    public Iterator<T> greaterOrEqualTo(double key) {
        OptionalLong startIndex = entries.ceilIndex(key);
        if (startIndex.isEmpty()) {
            return Collections.emptyIterator();
        }

        long firstIndex = startIndex.getAsLong();
        long size = entries.size();
        List<T> values = new ArrayList<>(Math.toIntExact(size - firstIndex));
        if (descending) {
            for (long index = size; index-- > firstIndex; ) {
                values.add(entries.nth(index).value());
            }
        } else {
            for (long index = firstIndex; index < size; index++) {
                values.add(entries.nth(index).value());
            }
        }

        return Collections.unmodifiableList(values).iterator();
    }

    @Override
    public int size() {
        return Math.toIntExact(entries.size());
    }

    @Override
    public BifurcanSortedMapDoubleObjectPersistentSortedMap<T> remove(double key) {
        SortedMap<Double, T> updatedEntries = entries.remove(key);
        return updatedEntries == entries
                ? this
                : new BifurcanSortedMapDoubleObjectPersistentSortedMap<>(updatedEntries, descending);
    }

    @Override
    public BifurcanSortedMapDoubleObjectPersistentSortedMap<T> put(double key, T value) {
        SortedMap<Double, T> updatedEntries = entries.put(key, Objects.requireNonNull(value, "value"));
        return updatedEntries == entries
                ? this
                : new BifurcanSortedMapDoubleObjectPersistentSortedMap<>(updatedEntries, descending);
    }
}
