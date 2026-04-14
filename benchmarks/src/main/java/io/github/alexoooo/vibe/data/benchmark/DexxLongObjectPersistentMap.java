package io.github.alexoooo.vibe.data.benchmark;

import com.github.andrewoma.dexx.collection.HashMap;
import com.github.andrewoma.dexx.collection.Pair;
import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import java.util.Iterator;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class DexxLongObjectPersistentMap<T> implements LongObjectPersistentMap<T> {

    private final HashMap<Long, T> entries;

    private DexxLongObjectPersistentMap(HashMap<Long, T> entries) {
        this.entries = entries;
    }

    static <T> DexxLongObjectPersistentMap<T> empty() {
        return new DexxLongObjectPersistentMap<>(new HashMap<>());
    }

    @Override
    public @Nullable T find(long key) {
        return entries.get(key);
    }

    @Override
    public Iterator<T> iterator() {
        return new ValueIterator<>(entries.iterator());
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public DexxLongObjectPersistentMap<T> remove(long key) {
        return new DexxLongObjectPersistentMap<>(entries.remove(key));
    }

    @Override
    public DexxLongObjectPersistentMap<T> put(long key, T value) {
        return new DexxLongObjectPersistentMap<>(entries.put(key, Objects.requireNonNull(value, "value")));
    }

    private static final class ValueIterator<T> implements Iterator<T> {

        private final Iterator<Pair<Long, T>> pairs;

        private ValueIterator(Iterator<Pair<Long, T>> pairs) {
            this.pairs = pairs;
        }

        @Override
        public boolean hasNext() {
            return pairs.hasNext();
        }

        @Override
        public T next() {
            return pairs.next().component2();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
