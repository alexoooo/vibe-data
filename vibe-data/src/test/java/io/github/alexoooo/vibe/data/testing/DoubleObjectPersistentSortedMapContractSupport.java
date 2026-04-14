package io.github.alexoooo.vibe.data.testing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

final class DoubleObjectPersistentSortedMapContractSupport {

    private static final List<Double> SPECIAL_KEYS = List.of(
            Double.NEGATIVE_INFINITY,
            -100.5,
            -1.0,
            -0.0,
            0.0,
            Double.MIN_VALUE,
            1.0,
            42.5,
            Double.POSITIVE_INFINITY,
            Double.NaN);

    private static final List<Double> SPECIAL_LOWER_BOUNDS = List.of(
            Double.NEGATIVE_INFINITY,
            -100.5,
            -1.0,
            -0.0,
            0.0,
            Double.MIN_VALUE,
            1.0,
            42.5,
            Double.POSITIVE_INFINITY,
            Double.NaN);

    private DoubleObjectPersistentSortedMapContractSupport() {}

    static List<Double> specialKeys() {
        return SPECIAL_KEYS;
    }

    static List<Double> specialLowerBounds() {
        return SPECIAL_LOWER_BOUNDS;
    }

    static List<Double> sequentialProbeKeys(int size) {
        List<Double> keys = new ArrayList<>(SPECIAL_KEYS);
        keys.add(-1.0);
        for (int key = 0; key <= size; key++) {
            keys.add((double) key);
        }
        return dedupeInOrder(keys);
    }

    static List<Double> randomKeyPool(long seed, int targetSize) {
        Random random = new Random(seed);
        TreeSet<Double> keys = new TreeSet<>();
        keys.addAll(SPECIAL_KEYS);

        while (keys.size() < targetSize) {
            keys.add(Double.longBitsToDouble(random.nextLong()));
        }

        return List.copyOf(keys);
    }

    static List<Double> lowerBoundsFor(List<Double> keys) {
        LinkedHashSet<Double> lowerBounds = new LinkedHashSet<>(SPECIAL_LOWER_BOUNDS);

        for (int index = 0; index < keys.size(); index += 5) {
            lowerBounds.add(keys.get(index));
        }

        if (!keys.isEmpty()) {
            lowerBounds.add(keys.get(keys.size() - 1));
        }

        return List.copyOf(lowerBounds);
    }

    static List<Double> verificationLowerBounds(List<Double> lowerBounds, double anchorKey, int step) {
        LinkedHashSet<Double> bounds = new LinkedHashSet<>();
        bounds.add(Double.NEGATIVE_INFINITY);
        bounds.add(-0.0);
        bounds.add(0.0);
        bounds.add(Double.NaN);
        bounds.add(anchorKey);

        if (!lowerBounds.isEmpty()) {
            bounds.add(lowerBounds.get(Math.floorMod(step, lowerBounds.size())));
            bounds.add(lowerBounds.get(Math.floorMod(step * 17 + 3, lowerBounds.size())));
            bounds.add(lowerBounds.get(Math.floorMod(step * 31 + 7, lowerBounds.size())));
        }

        return List.copyOf(bounds);
    }

    static List<Double> dedupeInOrder(List<Double> values) {
        return List.copyOf(new LinkedHashSet<>(values));
    }

    static String describeKey(double key) {
        return Double.toString(key) + " [0x" + Long.toHexString(Double.doubleToLongBits(key)) + "]";
    }

    static String valueFor(String scenario, int step, double key) {
        return scenario + "-" + step + "-" + Long.toHexString(Double.doubleToLongBits(key));
    }

    static <T> List<T> toList(Iterator<T> iterator) {
        List<T> values = new ArrayList<>();
        iterator.forEachRemaining(values::add);
        return values;
    }
}
