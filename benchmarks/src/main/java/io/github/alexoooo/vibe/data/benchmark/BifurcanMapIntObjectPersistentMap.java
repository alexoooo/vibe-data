package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
import io.lacuna.bifurcan.IEntry;
import java.util.Iterator;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class BifurcanMapIntObjectPersistentMap<T> implements IntObjectPersistentMap<T> {

    private final io.lacuna.bifurcan.Map<Integer, T> entries;

    private BifurcanMapIntObjectPersistentMap(io.lacuna.bifurcan.Map<Integer, T> entries) {
        this.entries = entries;
    }

    static <T> BifurcanMapIntObjectPersistentMap<T> empty() {
        return new BifurcanMapIntObjectPersistentMap<>(new io.lacuna.bifurcan.Map<>());
    }

    @Override
    public @Nullable T find(int key) {
        return entries.get(key, null);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<IEntry<Integer, T>> entryIterator = entries.iterator();
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
    public BifurcanMapIntObjectPersistentMap<T> remove(int key) {
        io.lacuna.bifurcan.Map<Integer, T> updatedEntries = entries.remove(key);
        return updatedEntries == entries
                ? this
                : new BifurcanMapIntObjectPersistentMap<>(updatedEntries);
    }

    @Override
    public BifurcanMapIntObjectPersistentMap<T> put(int key, T value) {
        T nonNullValue = Objects.requireNonNull(value, "value");
        io.lacuna.bifurcan.Map<Integer, T> updatedEntries =
                entries.put(key, nonNullValue, (existing, replacement) -> replacement);
        return updatedEntries == entries
                ? this
                : new BifurcanMapIntObjectPersistentMap<>(updatedEntries);
    }
}
