package io.github.alexoooo.vibe.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class SimpleLongObjectPersistentMap<T> implements LongObjectPersistentMap<T> {

    private final Map<Long, T> entries;

    private SimpleLongObjectPersistentMap(Map<Long, T> entries) {
        this.entries = entries;
    }

    public static <T> SimpleLongObjectPersistentMap<T> empty() {
        return new SimpleLongObjectPersistentMap<>(new HashMap<>());
    }

    @Override
    public @Nullable T find(long key) {
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
    public SimpleLongObjectPersistentMap<T> remove(long key) {
        HashMap<Long, T> updatedEntries = new HashMap<>(entries);
        updatedEntries.remove(key);
        return new SimpleLongObjectPersistentMap<>(updatedEntries);
    }

    @Override
    public SimpleLongObjectPersistentMap<T> put(long key, T value) {
        HashMap<Long, T> updatedEntries = new HashMap<>(entries);
        updatedEntries.put(key, Objects.requireNonNull(value, "value"));
        return new SimpleLongObjectPersistentMap<>(updatedEntries);
    }
}
