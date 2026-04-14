package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import io.lacuna.bifurcan.IEntry;
import io.lacuna.bifurcan.IntMap;
import java.util.Iterator;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class BifurcanIntMapLongObjectPersistentMap<T> implements LongObjectPersistentMap<T> {

    private final IntMap<T> entries;

    private BifurcanIntMapLongObjectPersistentMap(IntMap<T> entries) {
        this.entries = entries;
    }

    static <T> BifurcanIntMapLongObjectPersistentMap<T> empty() {
        return new BifurcanIntMapLongObjectPersistentMap<>(new IntMap<>());
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
    public BifurcanIntMapLongObjectPersistentMap<T> remove(long key) {
        IntMap<T> updatedEntries = entries.remove(key);
        return updatedEntries == entries
                ? this
                : new BifurcanIntMapLongObjectPersistentMap<>(updatedEntries);
    }

    @Override
    public BifurcanIntMapLongObjectPersistentMap<T> put(long key, T value) {
        IntMap<T> updatedEntries = entries.put(key, Objects.requireNonNull(value, "value"));
        return updatedEntries == entries
                ? this
                : new BifurcanIntMapLongObjectPersistentMap<>(updatedEntries);
    }
}
