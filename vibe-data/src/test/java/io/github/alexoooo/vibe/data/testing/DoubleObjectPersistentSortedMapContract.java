package io.github.alexoooo.vibe.data.testing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

public interface DoubleObjectPersistentSortedMapContract {

    DoubleObjectPersistentSortedMap<String> newAscendingMap();

    DoubleObjectPersistentSortedMap<String> newDescendingMap();

    default DoubleObjectPersistentSortedMap<String> newMap(IterationOrder order) {
        return switch (order) {
            case ASCENDING -> newAscendingMap();
            case DESCENDING -> newDescendingMap();
        };
    }

    default ContractOptions contractOptions() {
        return ContractOptions.defaults();
    }

    @Test
    default void emptyMapMatchesReferenceModelAcrossBounds() {
        for (IterationOrder order : IterationOrder.values()) {
            assertMatchesReference(
                    "empty map " + order,
                    newMap(order),
                    ReferenceDoubleObjectPersistentSortedMap.empty(order),
                    DoubleObjectPersistentSortedMapContractSupport.specialKeys(),
                    DoubleObjectPersistentSortedMapContractSupport.specialLowerBounds());
        }
    }

    @Test
    default void specialDoubleKeysMatchReferenceModel() {
        List<Double> probeKeys = DoubleObjectPersistentSortedMapContractSupport.specialKeys();
        List<Double> lowerBounds = DoubleObjectPersistentSortedMapContractSupport.specialLowerBounds();

        for (IterationOrder order : IterationOrder.values()) {
            DoubleObjectPersistentSortedMap<String> actual = newMap(order);
            ReferenceDoubleObjectPersistentSortedMap expected = ReferenceDoubleObjectPersistentSortedMap.empty(order);
            List<ContractSnapshot> snapshots = new ArrayList<>();
            snapshots.add(new ContractSnapshot(actual, expected, "special initial " + order));

            int step = 0;
            for (double key : probeKeys) {
                actual = actual.put(key, DoubleObjectPersistentSortedMapContractSupport.valueFor("special", step, key));
                expected = expected.put(key, DoubleObjectPersistentSortedMapContractSupport.valueFor("special", step, key));

                if (step % 3 == 0) {
                    snapshots.add(new ContractSnapshot(actual, expected, "special step " + step + " " + order));
                }

                assertMatchesReference(
                        "special insert order=" + order + " key=" + DoubleObjectPersistentSortedMapContractSupport.describeKey(key),
                        actual,
                        expected,
                        probeKeys,
                        lowerBounds);
                step++;
            }

            actual = actual.put(Double.NaN, "special-nan-overwrite");
            expected = expected.put(Double.NaN, "special-nan-overwrite");
            actual = actual.put(-0.0, "special-negative-zero-overwrite");
            expected = expected.put(-0.0, "special-negative-zero-overwrite");
            actual = actual.put(0.0, "special-positive-zero-overwrite");
            expected = expected.put(0.0, "special-positive-zero-overwrite");

            assertMatchesReference("special overwrites order=" + order, actual, expected, probeKeys, lowerBounds);

            for (double key : List.of(0.0, -0.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)) {
                actual = actual.remove(key);
                expected = expected.remove(key);

                assertMatchesReference(
                        "special remove order=" + order + " key=" + DoubleObjectPersistentSortedMapContractSupport.describeKey(key),
                        actual,
                        expected,
                        probeKeys,
                        lowerBounds);
            }

            for (ContractSnapshot snapshot : snapshots) {
                assertMatchesReference(
                        "special snapshot preserved " + snapshot.context(),
                        snapshot.actual(),
                        snapshot.expected(),
                        probeKeys,
                        lowerBounds);
            }
        }
    }

    @Test
    default void sequentialLoopScenarioMatchesReferenceModel() {
        List<Double> probeKeys = DoubleObjectPersistentSortedMapContractSupport.sequentialProbeKeys(128);
        List<Double> lowerBounds = DoubleObjectPersistentSortedMapContractSupport.lowerBoundsFor(probeKeys);

        for (IterationOrder order : IterationOrder.values()) {
            DoubleObjectPersistentSortedMap<String> actual = newMap(order);
            ReferenceDoubleObjectPersistentSortedMap expected = ReferenceDoubleObjectPersistentSortedMap.empty(order);
            List<ContractSnapshot> snapshots = new ArrayList<>();

            for (int key = 0; key < 128; key++) {
                if (key % 16 == 0) {
                    snapshots.add(new ContractSnapshot(actual, expected, "sequential insert checkpoint " + key + " " + order));
                }

                actual = actual.put(key, "sequential-" + key);
                expected = expected.put(key, "sequential-" + key);

                if (key % 17 == 0 || key == 127) {
                    assertMatchesReference(
                            "sequential insert order=" + order + " key=" + key,
                            actual,
                            expected,
                            probeKeys,
                            lowerBounds);
                }
            }

            for (int key = 0; key < 128; key += 5) {
                actual = actual.put(key, "sequential-overwrite-" + key);
                expected = expected.put(key, "sequential-overwrite-" + key);
            }

            assertMatchesReference("sequential overwrites order=" + order, actual, expected, probeKeys, lowerBounds);

            for (int key = 0; key < 128; key += 3) {
                actual = actual.remove(key);
                expected = expected.remove(key);

                if (key % 15 == 0 || key >= 126) {
                    assertMatchesReference(
                            "sequential remove order=" + order + " key=" + key,
                            actual,
                            expected,
                            probeKeys,
                            lowerBounds);
                }
            }

            for (ContractSnapshot snapshot : snapshots) {
                assertMatchesReference(
                        "sequential snapshot preserved " + snapshot.context(),
                        snapshot.actual(),
                        snapshot.expected(),
                        probeKeys,
                        lowerBounds);
            }
        }
    }

    @Test
    default void randomizedOperationSequencesMatchReferenceModel() {
        for (IterationOrder order : IterationOrder.values()) {
            for (int seedIndex = 0; seedIndex < 8; seedIndex++) {
                long seed = 0x5eed_5eedL + seedIndex;
                Random random = new Random(seed);
                List<Double> keyPool = DoubleObjectPersistentSortedMapContractSupport.randomKeyPool(seed, 48);
                List<Double> lowerBounds = DoubleObjectPersistentSortedMapContractSupport.lowerBoundsFor(keyPool);
                DoubleObjectPersistentSortedMap<String> actual = newMap(order);
                ReferenceDoubleObjectPersistentSortedMap expected = ReferenceDoubleObjectPersistentSortedMap.empty(order);
                List<ContractSnapshot> snapshots = new ArrayList<>();
                snapshots.add(new ContractSnapshot(actual, expected, "random initial seed=" + seed + " " + order));

                for (int step = 0; step < 256; step++) {
                    if (step % 32 == 0) {
                        snapshots.add(new ContractSnapshot(actual, expected, "random checkpoint seed=" + seed + " step=" + step + " " + order));
                    }

                    double key = keyPool.get(random.nextInt(keyPool.size()));
                    String context = "random order=" + order + " seed=" + seed + " step=" + step;

                    switch (random.nextInt(6)) {
                        case 0, 1 -> {
                            String value = DoubleObjectPersistentSortedMapContractSupport.valueFor("random-" + seed, step, key);
                            actual = actual.put(key, value);
                            expected = expected.put(key, value);
                        }
                        case 2 -> {
                            actual = actual.remove(key);
                            expected = expected.remove(key);
                        }
                        case 3 -> assertEquals(expected.find(key), actual.find(key), context + " direct find");
                        case 4 -> {
                            double lowerBound = lowerBounds.get(random.nextInt(lowerBounds.size()));
                            assertEquals(
                                    expected.greaterOrEqualTo(lowerBound),
                                    DoubleObjectPersistentSortedMapContractSupport.toList(actual.greaterOrEqualTo(lowerBound)),
                                    context + " direct greaterOrEqualTo("
                                            + DoubleObjectPersistentSortedMapContractSupport.describeKey(lowerBound)
                                            + ")");
                        }
                        case 5 -> assertEquals(expected.size(), actual.size(), context + " direct size");
                        default -> throw new IllegalStateException("Unexpected random operation");
                    }

                    assertMatchesReference(
                            context,
                            actual,
                            expected,
                            keyPool,
                            DoubleObjectPersistentSortedMapContractSupport.verificationLowerBounds(lowerBounds, key, step));
                }

                for (ContractSnapshot snapshot : snapshots) {
                    assertMatchesReference(
                            "random snapshot preserved " + snapshot.context(),
                            snapshot.actual(),
                            snapshot.expected(),
                            keyPool,
                            lowerBounds);
                }
            }
        }
    }

    @Test
    default void iteratorsRemainBoundToOriginalSnapshot() {
        List<Double> keys = DoubleObjectPersistentSortedMapContractSupport.dedupeInOrder(
                List.of(Double.NEGATIVE_INFINITY, -1.0, -0.0, 0.0, 1.0, 2.0, Double.POSITIVE_INFINITY, Double.NaN));
        List<Double> lowerBounds = DoubleObjectPersistentSortedMapContractSupport.lowerBoundsFor(keys);

        for (IterationOrder order : IterationOrder.values()) {
            DoubleObjectPersistentSortedMap<String> actual = newMap(order);
            ReferenceDoubleObjectPersistentSortedMap expected = ReferenceDoubleObjectPersistentSortedMap.empty(order);

            int step = 0;
            for (double key : keys) {
                String value = DoubleObjectPersistentSortedMapContractSupport.valueFor("iterator-snapshot", step, key);
                actual = actual.put(key, value);
                expected = expected.put(key, value);
                step++;
            }

            double lowerBound = -0.0;
            Iterator<String> iterator = actual.greaterOrEqualTo(lowerBound);
            List<String> expectedValues = expected.greaterOrEqualTo(lowerBound);

            DoubleObjectPersistentSortedMap<String> updatedActual = actual
                    .put(3.0, "iterator-snapshot-three")
                    .remove(1.0)
                    .put(Double.NaN, "iterator-snapshot-nan");
            ReferenceDoubleObjectPersistentSortedMap updatedExpected = expected
                    .put(3.0, "iterator-snapshot-three")
                    .remove(1.0)
                    .put(Double.NaN, "iterator-snapshot-nan");

            assertEquals(expectedValues, DoubleObjectPersistentSortedMapContractSupport.toList(iterator), "iterator snapshot " + order);
            assertMatchesReference("iterator updated map " + order, updatedActual, updatedExpected, keys, lowerBounds);
            assertMatchesReference("iterator original map " + order, actual, expected, keys, lowerBounds);
        }
    }

    @Test
    default void iteratorContractMatchesImplementationOptions() {
        for (IterationOrder order : IterationOrder.values()) {
            DoubleObjectPersistentSortedMap<String> map = newMap(order)
                    .put(1.0, "one")
                    .put(2.0, "two");
            ReferenceDoubleObjectPersistentSortedMap expected = ReferenceDoubleObjectPersistentSortedMap.empty(order)
                    .put(1.0, "one")
                    .put(2.0, "two");

            Iterator<String> iterator = map.greaterOrEqualTo(1.0);

            assertTrue(iterator.hasNext(), "iterator should contain the first element for " + order);
            assertEquals(expected.greaterOrEqualTo(1.0).get(0), iterator.next(), "iterator first element for " + order);

            switch (contractOptions().iteratorRemoveBehavior()) {
                case UNSUPPORTED -> assertThrows(UnsupportedOperationException.class, iterator::remove, "iterator remove for " + order);
                case SUPPORTED -> assertDoesNotThrow(iterator::remove, "iterator remove for " + order);
            }

            while (iterator.hasNext()) {
                iterator.next();
            }

            assertFalse(iterator.hasNext(), "iterator should be exhausted for " + order);
            assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion for " + order);

            Iterator<String> emptyIterator = newMap(order).greaterOrEqualTo(Double.NEGATIVE_INFINITY);
            assertFalse(emptyIterator.hasNext(), "empty iterator hasNext for " + order);
            assertThrows(NoSuchElementException.class, emptyIterator::next, "empty iterator next for " + order);
        }
    }

    @Test
    default void nullValuesAreRejected() {
        for (IterationOrder order : IterationOrder.values()) {
            assertThrows(NullPointerException.class, () -> newMap(order).put(1.0, null), "null values must be rejected for " + order);
        }
    }

    private void assertMatchesReference(
            String context,
            DoubleObjectPersistentSortedMap<String> actual,
            ReferenceDoubleObjectPersistentSortedMap expected,
            List<Double> probeKeys,
            List<Double> lowerBounds) {
        assertEquals(expected.size(), actual.size(), context + " size");

        for (double key : probeKeys) {
            assertEquals(
                    expected.find(key),
                    actual.find(key),
                    context + " find(" + DoubleObjectPersistentSortedMapContractSupport.describeKey(key) + ")");
        }

        for (double lowerBound : lowerBounds) {
            assertEquals(
                    expected.greaterOrEqualTo(lowerBound),
                    DoubleObjectPersistentSortedMapContractSupport.toList(actual.greaterOrEqualTo(lowerBound)),
                    context + " greaterOrEqualTo(" + DoubleObjectPersistentSortedMapContractSupport.describeKey(lowerBound) + ")");
        }
    }

    record ContractOptions(IteratorRemoveBehavior iteratorRemoveBehavior) {

        public static ContractOptions defaults() {
            return new ContractOptions(IteratorRemoveBehavior.UNSUPPORTED);
        }
    }

    enum IteratorRemoveBehavior {
        UNSUPPORTED,
        SUPPORTED
    }

    enum IterationOrder {
        ASCENDING,
        DESCENDING
    }
}

record ContractSnapshot(
        DoubleObjectPersistentSortedMap<String> actual,
        ReferenceDoubleObjectPersistentSortedMap expected,
        String context) {
}

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

    private DoubleObjectPersistentSortedMapContractSupport() {
    }

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

final class ReferenceDoubleObjectPersistentSortedMap {

    private final NavigableMap<Double, String> entries;
    private final boolean descending;

    private ReferenceDoubleObjectPersistentSortedMap(NavigableMap<Double, String> entries, boolean descending) {
        this.entries = entries;
        this.descending = descending;
    }

    static ReferenceDoubleObjectPersistentSortedMap empty(DoubleObjectPersistentSortedMapContract.IterationOrder order) {
        return new ReferenceDoubleObjectPersistentSortedMap(
                new TreeMap<>(),
                order == DoubleObjectPersistentSortedMapContract.IterationOrder.DESCENDING);
    }

    @Nullable String find(double key) {
        return entries.get(key);
    }

    List<String> greaterOrEqualTo(double lowerBound) {
        NavigableMap<Double, String> filteredEntries = entries.tailMap(lowerBound, true);
        NavigableMap<Double, String> orderedEntries = descending ? filteredEntries.descendingMap() : filteredEntries;
        return new ArrayList<>(orderedEntries.values());
    }

    int size() {
        return entries.size();
    }

    ReferenceDoubleObjectPersistentSortedMap put(double key, String value) {
        TreeMap<Double, String> updatedEntries = new TreeMap<>(entries);
        updatedEntries.put(key, Objects.requireNonNull(value, "value"));
        return new ReferenceDoubleObjectPersistentSortedMap(updatedEntries, descending);
    }

    ReferenceDoubleObjectPersistentSortedMap remove(double key) {
        TreeMap<Double, String> updatedEntries = new TreeMap<>(entries);
        updatedEntries.remove(key);
        return new ReferenceDoubleObjectPersistentSortedMap(updatedEntries, descending);
    }
}
