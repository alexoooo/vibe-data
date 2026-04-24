package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
import io.lacuna.bifurcan.IEntry;
import io.lacuna.bifurcan.IntMap;
import java.util.Iterator;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class BifurcanIntMapIntObjectPersistentMap<T> implements IntObjectPersistentMap<T> {

    private final IntMap<T> entries;

    private BifurcanIntMapIntObjectPersistentMap(IntMap<T> entries) {
        this.entries = entries;
    }

    static <T> BifurcanIntMapIntObjectPersistentMap<T> empty() {
        return new BifurcanIntMapIntObjectPersistentMap<>(new IntMap<>());
    }

    @Override
    public @Nullable T find(int key) {
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
    public BifurcanIntMapIntObjectPersistentMap<T> remove(int key) {
        IntMap<T> updatedEntries = entries.remove(key);
        return updatedEntries == entries
                ? this
                : new BifurcanIntMapIntObjectPersistentMap<>(updatedEntries);
    }

    @Override
    public BifurcanIntMapIntObjectPersistentMap<T> put(int key, T value) {
        IntMap<T> updatedEntries = entries.put(key, Objects.requireNonNull(value, "value"));
        return updatedEntries == entries
                ? this
                : new BifurcanIntMapIntObjectPersistentMap<>(updatedEntries);
    }
}
