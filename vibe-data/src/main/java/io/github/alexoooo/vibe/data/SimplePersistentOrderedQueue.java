package io.github.alexoooo.vibe.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

public final class SimplePersistentOrderedQueue<T> implements PersistentOrderedQueue<T> {

    private final NavigableSet<T> entries;

    private SimplePersistentOrderedQueue(NavigableSet<T> entries) {
        this.entries = entries;
    }

    public static <T extends Comparable<? super T>> SimplePersistentOrderedQueue<T> empty() {
        return new SimplePersistentOrderedQueue<>(new TreeSet<>());
    }

    public static <T> SimplePersistentOrderedQueue<T> empty(Comparator<? super T> comparator) {
        return new SimplePersistentOrderedQueue<>(new TreeSet<>(Objects.requireNonNull(comparator, "comparator")));
    }

    @Override
    public SimplePersistentOrderedQueue<T> add(T value) {
        TreeSet<T> updatedEntries = new TreeSet<>(entries);
        T requiredValue = Objects.requireNonNull(value, "value");
        updatedEntries.remove(requiredValue);
        updatedEntries.add(requiredValue);
        return new SimplePersistentOrderedQueue<>(updatedEntries);
    }

    @Override
    public T first() {
        if (entries.isEmpty()) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return entries.first();
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableNavigableSet(entries).iterator();
    }

    @Override
    public T last() {
        if (entries.isEmpty()) {
            throw new IndexOutOfBoundsException("empty ordered queue");
        }
        return entries.last();
    }

    @Override
    public SimplePersistentOrderedQueue<T> remove(T value) {
        TreeSet<T> updatedEntries = new TreeSet<>(entries);
        updatedEntries.remove(Objects.requireNonNull(value, "value"));
        return new SimplePersistentOrderedQueue<>(updatedEntries);
    }

    @Override
    public SimplePersistentOrderedQueue<T> replace(T remove, T add) {
        TreeSet<T> updatedEntries = new TreeSet<>(entries);
        updatedEntries.remove(Objects.requireNonNull(remove, "remove"));
        T requiredAdd = Objects.requireNonNull(add, "add");
        updatedEntries.remove(requiredAdd);
        updatedEntries.add(requiredAdd);
        return new SimplePersistentOrderedQueue<>(updatedEntries);
    }

    @Override
    public int size() {
        return entries.size();
    }
}
