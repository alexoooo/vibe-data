package io.github.alexoooo.vibe.data.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import org.jspecify.annotations.Nullable;

final class ReferenceDoubleObjectPersistentSortedMap {

    private final NavigableMap<Double, String> entries;
    private final boolean descending;

    private ReferenceDoubleObjectPersistentSortedMap(NavigableMap<Double, String> entries, boolean descending) {
        this.entries = entries;
        this.descending = descending;
    }

    static ReferenceDoubleObjectPersistentSortedMap empty(DoubleObjectPersistentSortedMapContract.IterationOrder order) {
        return new ReferenceDoubleObjectPersistentSortedMap(
                new TreeMap<>(),
                order == DoubleObjectPersistentSortedMapContract.IterationOrder.DESCENDING);
    }

    @Nullable String find(double key) {
        return entries.get(key);
    }

    List<String> greaterOrEqualTo(double lowerBound) {
        NavigableMap<Double, String> filteredEntries = entries.tailMap(lowerBound, true);
        NavigableMap<Double, String> orderedEntries = descending ? filteredEntries.descendingMap() : filteredEntries;
        return new ArrayList<>(orderedEntries.values());
    }

    int size() {
        return entries.size();
    }

    ReferenceDoubleObjectPersistentSortedMap put(double key, String value) {
        TreeMap<Double, String> updatedEntries = new TreeMap<>(entries);
        updatedEntries.put(key, Objects.requireNonNull(value, "value"));
        return new ReferenceDoubleObjectPersistentSortedMap(updatedEntries, descending);
    }

    ReferenceDoubleObjectPersistentSortedMap remove(double key) {
        TreeMap<Double, String> updatedEntries = new TreeMap<>(entries);
        updatedEntries.remove(key);
        return new ReferenceDoubleObjectPersistentSortedMap(updatedEntries, descending);
    }
}
