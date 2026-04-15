package io.github.alexoooo.vibe.data.testing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

final class ReferenceOrderedQueue<T> {

    private final Comparator<? super T> comparator;
    private final NavigableSet<T> entries;

    private ReferenceOrderedQueue(Comparator<? super T> comparator, NavigableSet<T> entries) {
        this.comparator = comparator;
        this.entries = entries;
    }

    public static ReferenceOrderedQueue<String> naturalOrder() {
        return new ReferenceOrderedQueue<String>(Comparator.naturalOrder(), new TreeSet<>());
    }

    public static <T> ReferenceOrderedQueue<T> orderedBy(Comparator<? super T> comparator) {
        Comparator<? super T> requiredComparator = Objects.requireNonNull(comparator, "comparator");
        return new ReferenceOrderedQueue<>(requiredComparator, new TreeSet<>(requiredComparator));
    }

    ReferenceOrderedQueue<T> add(T value) {
        TreeSet<T> updatedEntries = new TreeSet<>(comparator);
        updatedEntries.addAll(entries);
        T requiredValue = Objects.requireNonNull(value, "value");
        updatedEntries.remove(requiredValue);
        updatedEntries.add(requiredValue);
        return new ReferenceOrderedQueue<>(comparator, updatedEntries);
    }

    T first() {
        return entries.first();
    }

    boolean isEmpty() {
        return entries.isEmpty();
    }

    T last() {
        return entries.last();
    }

    ReferenceOrderedQueue<T> remove(T value) {
        TreeSet<T> updatedEntries = new TreeSet<>(comparator);
        updatedEntries.addAll(entries);
        updatedEntries.remove(Objects.requireNonNull(value, "value"));
        return new ReferenceOrderedQueue<>(comparator, updatedEntries);
    }

    ReferenceOrderedQueue<T> replace(T remove, T add) {
        TreeSet<T> updatedEntries = new TreeSet<>(comparator);
        updatedEntries.addAll(entries);
        updatedEntries.remove(Objects.requireNonNull(remove, "remove"));
        T requiredAdd = Objects.requireNonNull(add, "add");
        updatedEntries.remove(requiredAdd);
        updatedEntries.add(requiredAdd);
        return new ReferenceOrderedQueue<>(comparator, updatedEntries);
    }

    int size() {
        return entries.size();
    }

    List<T> values() {
        return List.copyOf(new ArrayList<>(entries));
    }
}
