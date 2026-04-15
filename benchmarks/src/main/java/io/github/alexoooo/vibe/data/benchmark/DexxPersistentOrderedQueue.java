package io.github.alexoooo.vibe.data.benchmark;

import com.github.andrewoma.dexx.collection.TreeSet;
import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class DexxPersistentOrderedQueue<T> implements PersistentOrderedQueue<T> {

    private final TreeSet<T> entries;

    private DexxPersistentOrderedQueue(TreeSet<T> entries) {
        this.entries = entries;
    }

    static <T extends Comparable<? super T>> DexxPersistentOrderedQueue<T> empty() {
        return new DexxPersistentOrderedQueue<>(TreeSet.empty());
    }

    static <T> DexxPersistentOrderedQueue<T> empty(Comparator<? super T> comparator) {
        return new DexxPersistentOrderedQueue<>(new TreeSet<>(Objects.requireNonNull(comparator, "comparator")));
    }

    @Override
    public DexxPersistentOrderedQueue<T> add(T value) {
        return new DexxPersistentOrderedQueue<>(entries.add(Objects.requireNonNull(value, "value")));
    }

    @Override
    public T first() {
        if (entries.isEmpty()) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return Objects.requireNonNull(entries.first(), "first");
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> delegate = entries.iterator();
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
        if (entries.isEmpty()) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return Objects.requireNonNull(entries.last(), "last");
    }

    @Override
    public DexxPersistentOrderedQueue<T> remove(T value) {
        return new DexxPersistentOrderedQueue<>(entries.remove(Objects.requireNonNull(value, "value")));
    }

    @Override
    public DexxPersistentOrderedQueue<T> replace(T remove, T add) {
        return new DexxPersistentOrderedQueue<>(
                entries.remove(Objects.requireNonNull(remove, "remove"))
                        .add(Objects.requireNonNull(add, "add")));
    }

    @Override
    public int size() {
        return entries.size();
    }
}
