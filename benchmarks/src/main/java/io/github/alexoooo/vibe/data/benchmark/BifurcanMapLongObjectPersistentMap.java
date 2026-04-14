package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import io.lacuna.bifurcan.IEntry;
import java.util.Iterator;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class BifurcanMapLongObjectPersistentMap<T> implements LongObjectPersistentMap<T> {

    private final io.lacuna.bifurcan.Map<Long, T> entries;

    private BifurcanMapLongObjectPersistentMap(io.lacuna.bifurcan.Map<Long, T> entries) {
        this.entries = entries;
    }

    static <T> BifurcanMapLongObjectPersistentMap<T> empty() {
        return new BifurcanMapLongObjectPersistentMap<>(new io.lacuna.bifurcan.Map<>());
    }

    @Override
    public @Nullable T find(long key) {
        return entries.get(key, null);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<IEntry<Long, T>> entryIterator = entries.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public T next() {
                return entryIterator.next().value();
            }
        };
    }

    @Override
    public int size() {
        return Math.toIntExact(entries.size());
    }

    @Override
    public BifurcanMapLongObjectPersistentMap<T> remove(long key) {
        io.lacuna.bifurcan.Map<Long, T> updatedEntries = entries.remove(key);
        return updatedEntries == entries
                ? this
                : new BifurcanMapLongObjectPersistentMap<>(updatedEntries);
    }

    @Override
    public BifurcanMapLongObjectPersistentMap<T> put(long key, T value) {
        T nonNullValue = Objects.requireNonNull(value, "value");
        io.lacuna.bifurcan.Map<Long, T> updatedEntries =
                entries.put(key, nonNullValue, (existing, replacement) -> replacement);
        return updatedEntries == entries
                ? this
                : new BifurcanMapLongObjectPersistentMap<>(updatedEntries);
    }
}
