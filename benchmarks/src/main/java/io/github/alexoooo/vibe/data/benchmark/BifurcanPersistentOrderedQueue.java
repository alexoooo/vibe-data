package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import io.lacuna.bifurcan.SortedSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class BifurcanPersistentOrderedQueue<T> implements PersistentOrderedQueue<T> {

    private final SortedSet<T> entries;

    private BifurcanPersistentOrderedQueue(SortedSet<T> entries) {
        this.entries = entries;
    }

    static <T extends Comparable<? super T>> BifurcanPersistentOrderedQueue<T> empty() {
        return new BifurcanPersistentOrderedQueue<>(new SortedSet<>());
    }

    @SuppressWarnings("unchecked")
    static <T> BifurcanPersistentOrderedQueue<T> empty(Comparator<? super T> comparator) {
        return new BifurcanPersistentOrderedQueue<>(
                new SortedSet<>((Comparator<T>) Objects.requireNonNull(comparator, "comparator")));
    }

    @Override
    public BifurcanPersistentOrderedQueue<T> add(T value) {
        SortedSet<T> updatedEntries = entries.add(Objects.requireNonNull(value, "value"));
        return updatedEntries == entries ? this : new BifurcanPersistentOrderedQueue<>(updatedEntries);
    }

    @Override
    public T first() {
        if (entries.size() == 0) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return entries.nth(0);
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> delegate = entries.elements().iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return delegate.next();
            }
        };
    }

    @Override
    public T last() {
        if (entries.size() == 0) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return entries.nth(entries.size() - 1);
    }

    @Override
    public BifurcanPersistentOrderedQueue<T> remove(T value) {
        SortedSet<T> updatedEntries = entries.remove(Objects.requireNonNull(value, "value"));
        return updatedEntries == entries ? this : new BifurcanPersistentOrderedQueue<>(updatedEntries);
    }

    @Override
    public BifurcanPersistentOrderedQueue<T> replace(T remove, T add) {
        return remove(remove).add(add);
    }

    @Override
    public int size() {
        return Math.toIntExact(entries.size());
    }
}
