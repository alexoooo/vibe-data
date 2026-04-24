package io.github.alexoooo.vibe.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class SimpleIntObjectPersistentMap<T> implements IntObjectPersistentMap<T> {

    private final Map<Integer, T> entries;

    private SimpleIntObjectPersistentMap(Map<Integer, T> entries) {
        this.entries = entries;
    }

    public static <T> SimpleIntObjectPersistentMap<T> empty() {
        return new SimpleIntObjectPersistentMap<>(new HashMap<>());
    }

    @Override
    public @Nullable T find(int key) {
        return entries.get(key);
    }

    @Override
    public Iterator<T> iterator() {
        Collection<T> values = Collections.unmodifiableCollection(entries.values());
        return values.iterator();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public SimpleIntObjectPersistentMap<T> remove(int key) {
        HashMap<Integer, T> updatedEntries = new HashMap<>(entries);
        updatedEntries.remove(key);
        return new SimpleIntObjectPersistentMap<>(updatedEntries);
    }

    @Override
    public SimpleIntObjectPersistentMap<T> put(int key, T value) {
        HashMap<Integer, T> updatedEntries = new HashMap<>(entries);
        updatedEntries.put(key, Objects.requireNonNull(value, "value"));
        return new SimpleIntObjectPersistentMap<>(updatedEntries);
    }
}
