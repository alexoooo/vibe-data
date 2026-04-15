package io.github.alexoooo.vibe.data.testing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

final class OrderedQueueContractSupport {

    private OrderedQueueContractSupport() {}

    static String naturalValue(int step) {
        return String.format(java.util.Locale.ROOT, "value-%04d", step);
    }

    static Comparator<String> lengthComparator() {
        return Comparator.comparingInt(String::length);
    }

    static List<String> lengthComparatorScenarioValues() {
        return List.of(
                "a",
                "b",
                "cc",
                "dd",
                "eee",
                "fff",
                "gggg",
                "hhhh",
                "iiiii",
                "jjjjj");
    }

    static List<String> naturalScenarioValues(int count) {
        ArrayList<String> values = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            values.add(naturalValue(index));
        }
        return List.copyOf(values);
    }

    static List<String> randomLengthComparatorPool(long seed, int targetSize) {
        Random random = new Random(seed);
        ArrayList<String> values = new ArrayList<>(targetSize);
        while (values.size() < targetSize) {
            int length = 1 + random.nextInt(10);
            char c = (char) ('a' + random.nextInt(26));
            values.add(String.valueOf(c).repeat(length));
        }
        return List.copyOf(values);
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
