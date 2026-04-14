package io.github.alexoooo.vibe.data.testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

final class PersistentSequenceContractSupport {

    private PersistentSequenceContractSupport() {}

    static String bucketedValue(String scenario, int step, int bucketCount) {
        int bucket = Math.floorMod((scenario.hashCode() * 31) + (step * 17), bucketCount);
        return scenario + "-bucket-" + bucket;
    }

    static List<Integer> interestingIndexes(int size) {
        if (size <= 0) {
            return List.of();
        }

        LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
        indexes.add(0);
        indexes.add(1);
        indexes.add(2);
        indexes.add(size / 3);
        indexes.add(size / 2);
        indexes.add(Math.max(0, size - 3));
        indexes.add(Math.max(0, size - 2));
        indexes.add(size - 1);
        indexes.removeIf(index -> index < 0 || index >= size);
        return List.copyOf(indexes);
    }

    static <T> List<T> reversedCopy(List<T> values) {
        ArrayList<T> reversed = new ArrayList<>(values);
        Collections.reverse(reversed);
        return List.copyOf(reversed);
    }

    static <T> List<T> toList(Iterable<T> values) {
        return toList(values.iterator());
    }

    static <T> List<T> toList(Iterator<T> iterator) {
        ArrayList<T> values = new ArrayList<>();
        while (iterator.hasNext()) {
            values.add(Objects.requireNonNull(iterator.next(), "value"));
        }
        return List.copyOf(values);
    }
}
