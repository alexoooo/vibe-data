package io.github.alexoooo.vibe.data.benchmark;

import com.github.andrewoma.dexx.collection.HashMap;
import com.github.andrewoma.dexx.collection.Pair;
import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
import java.util.Iterator;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class DexxIntObjectPersistentMap<T> implements IntObjectPersistentMap<T> {

    private final HashMap<Integer, T> entries;

    private DexxIntObjectPersistentMap(HashMap<Integer, T> entries) {
        this.entries = entries;
    }

    static <T> DexxIntObjectPersistentMap<T> empty() {
        return new DexxIntObjectPersistentMap<>(new HashMap<>());
    }

    @Override
    public @Nullable T find(int key) {
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
    public DexxIntObjectPersistentMap<T> remove(int key) {
        return new DexxIntObjectPersistentMap<>(entries.remove(key));
    }

    @Override
    public DexxIntObjectPersistentMap<T> put(int key, T value) {
        return new DexxIntObjectPersistentMap<>(entries.put(key, Objects.requireNonNull(value, "value")));
    }

    private static final class ValueIterator<T> implements Iterator<T> {

        private final Iterator<Pair<Integer, T>> pairs;

        private ValueIterator(Iterator<Pair<Integer, T>> pairs) {
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
