package io.github.alexoooo.vibe.data.testing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alexoooo.vibe.data.IntObjectMap;
import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;

public interface IntObjectPersistentMapContract {

    IntObjectPersistentMap<String> newMap();

    default ContractOptions contractOptions() {
        return ContractOptions.defaults();
    }

    @Test
    default void emptyMapMatchesReferenceModel() {
        assertMatchesReference(
                "empty map",
                newMap(),
                ReferenceIntObjectPersistentMap.empty(),
                IntObjectPersistentMapContractSupport.specialProbeKeys());
    }

    @Test
    default void specialIntKeysAndDuplicateValuesMatchReferenceModel() {
        List<Integer> insertKeys = IntObjectPersistentMapContractSupport.specialKeys();
        List<Integer> probeKeys = IntObjectPersistentMapContractSupport.specialProbeKeys();
        List<String> duplicateValues = List.of(
                "duplicate-alpha",
                "duplicate-beta",
                "duplicate-alpha",
                "duplicate-gamma",
                "duplicate-beta");

        IntObjectPersistentMap<String> actual = newMap();
        ReferenceIntObjectPersistentMap expected = ReferenceIntObjectPersistentMap.empty();
        List<IntObjectContractSnapshot> snapshots = new ArrayList<>();
        snapshots.add(new IntObjectContractSnapshot(actual, expected, "special initial"));

        int step = 0;
        for (int key : insertKeys) {
            String value = duplicateValues.get(step % duplicateValues.size());
            actual = actual.put(key, value);
            expected = expected.put(key, value);

            if (step % 3 == 0) {
                snapshots.add(new IntObjectContractSnapshot(actual, expected, "special step " + step));
            }

            assertMatchesReference(
                    "special insert key=" + IntObjectPersistentMapContractSupport.describeKey(key),
                    actual,
                    expected,
                    probeKeys);
            step++;
        }

        for (int key : List.of(Integer.MIN_VALUE, 0, Integer.MAX_VALUE)) {
            actual = actual.put(key, "duplicate-alpha");
            expected = expected.put(key, "duplicate-alpha");
        }
        actual = actual.put(42, "duplicate-beta");
        expected = expected.put(42, "duplicate-beta");

        assertMatchesReference("special overwrites", actual, expected, probeKeys);

        for (int key : List.of(0, Integer.MIN_VALUE + 2, Integer.MAX_VALUE, 999_999_999)) {
            actual = actual.remove(key);
            expected = expected.remove(key);

            assertMatchesReference(
                    "special remove key=" + IntObjectPersistentMapContractSupport.describeKey(key),
                    actual,
                    expected,
                    probeKeys);
        }

        for (IntObjectContractSnapshot snapshot : snapshots) {
            assertMatchesReference(
                    "special snapshot preserved " + snapshot.context(),
                    snapshot.actual(),
                    snapshot.expected(),
                    probeKeys);
        }
    }

    @Test
    default void sequentialLoopScenarioMatchesReferenceModel() {
        List<Integer> probeKeys = IntObjectPersistentMapContractSupport.sequentialProbeKeys(128);

        IntObjectPersistentMap<String> actual = newMap();
        ReferenceIntObjectPersistentMap expected = ReferenceIntObjectPersistentMap.empty();
        List<IntObjectContractSnapshot> snapshots = new ArrayList<>();

        for (int key = 0; key < 128; key++) {
            if (key % 16 == 0) {
                snapshots.add(new IntObjectContractSnapshot(actual, expected, "sequential insert checkpoint " + key));
            }

            String value = IntObjectPersistentMapContractSupport.bucketedValue("sequential", key, key, 7);
            actual = actual.put(key, value);
            expected = expected.put(key, value);

            if (key % 17 == 0 || key == 127) {
                assertMatchesReference("sequential insert key=" + key, actual, expected, probeKeys);
            }
        }

        for (int key = 0; key < 128; key += 5) {
            String value =
                    IntObjectPersistentMapContractSupport.bucketedValue("sequential-overwrite", key, -key, 5);
            actual = actual.put(key, value);
            expected = expected.put(key, value);
        }

        assertMatchesReference("sequential overwrites", actual, expected, probeKeys);

        actual = actual.remove(-1);
        expected = expected.remove(-1);
        assertMatchesReference("sequential remove missing negative key", actual, expected, probeKeys);

        for (int key = 0; key < 128; key += 3) {
            actual = actual.remove(key);
            expected = expected.remove(key);

            if (key % 15 == 0 || key >= 126) {
                assertMatchesReference("sequential remove key=" + key, actual, expected, probeKeys);
            }
        }

        for (IntObjectContractSnapshot snapshot : snapshots) {
            assertMatchesReference(
                    "sequential snapshot preserved " + snapshot.context(),
                    snapshot.actual(),
                    snapshot.expected(),
                    probeKeys);
        }
    }

    @Test
    default void duplicateValuesAreTrackedByIterationMultiplicity() {
        List<Integer> probeKeys =
                IntObjectPersistentMapContractSupport.dedupeInOrder(List.of(-1, 10, 20, 30, 40, 50, 60, Integer.MAX_VALUE));

        IntObjectPersistentMap<String> actual = newMap()
                .put(10, "repeat")
                .put(20, "repeat")
                .put(30, "unique")
                .put(40, "repeat")
                .put(50, "unique");
        ReferenceIntObjectPersistentMap expected = ReferenceIntObjectPersistentMap.empty()
                .put(10, "repeat")
                .put(20, "repeat")
                .put(30, "unique")
                .put(40, "repeat")
                .put(50, "unique");

        assertMatchesReference("duplicate values initial", actual, expected, probeKeys);

        actual = actual.put(20, "unique").remove(30).put(60, "repeat");
        expected = expected.put(20, "unique").remove(30).put(60, "repeat");

        assertMatchesReference("duplicate values updated", actual, expected, probeKeys);
        assertEquals(
                expected.values(),
                IntObjectPersistentMapContractSupport.valueBag(actual),
                "duplicate values should compare by multiplicity rather than sequence");
    }

    @Test
    default void randomizedOperationSequencesMatchReferenceModel() {
        for (int seedIndex = 0; seedIndex < 8; seedIndex++) {
            long seed = 0x5eed_5eedL + seedIndex;
            Random random = new Random(seed);
            List<Integer> keyPool = IntObjectPersistentMapContractSupport.randomKeyPool(seed, 48);
            IntObjectPersistentMap<String> actual = newMap();
            ReferenceIntObjectPersistentMap expected = ReferenceIntObjectPersistentMap.empty();
            List<IntObjectContractSnapshot> snapshots = new ArrayList<>();
            snapshots.add(new IntObjectContractSnapshot(actual, expected, "random initial seed=" + seed));

            for (int step = 0; step < 256; step++) {
                if (step % 32 == 0) {
                    snapshots.add(new IntObjectContractSnapshot(
                            actual,
                            expected,
                            "random checkpoint seed=" + seed + " step=" + step));
                }

                int key = keyPool.get(random.nextInt(keyPool.size()));
                String context = "random seed=" + seed + " step=" + step;

                switch (random.nextInt(6)) {
                    case 0, 1 -> {
                        String value = IntObjectPersistentMapContractSupport.bucketedValue(
                                "random-" + seed,
                                step,
                                key,
                                11);
                        actual = actual.put(key, value);
                        expected = expected.put(key, value);
                    }
                    case 2 -> {
                        actual = actual.remove(key);
                        expected = expected.remove(key);
                    }
                    case 3 -> assertEquals(expected.find(key), actual.find(key), context + " direct find");
                    case 4 -> assertEquals(expected.size(), actual.size(), context + " direct size");
                    case 5 -> assertEquals(
                            expected.values(),
                            IntObjectPersistentMapContractSupport.valueBag(actual),
                            context + " direct iteration");
                    default -> throw new IllegalStateException("Unexpected random operation");
                }

                assertMatchesReference(context, actual, expected, keyPool);
            }

            for (IntObjectContractSnapshot snapshot : snapshots) {
                assertMatchesReference(
                        "random snapshot preserved " + snapshot.context(),
                        snapshot.actual(),
                        snapshot.expected(),
                        keyPool);
            }
        }
    }

    @Test
    default void iteratorsRemainBoundToOriginalSnapshot() {
        List<Integer> probeKeys =
                IntObjectPersistentMapContractSupport.dedupeInOrder(List.of(Integer.MIN_VALUE, -1, 0, 1, 2, 3, 4, Integer.MAX_VALUE));

        IntObjectPersistentMap<String> actual = newMap();
        ReferenceIntObjectPersistentMap expected = ReferenceIntObjectPersistentMap.empty();

        List<String> values = List.of(
                "shared-alpha",
                "shared-beta",
                "shared-alpha",
                "shared-gamma",
                "shared-beta",
                "shared-alpha");
        int step = 0;
        for (int key : List.of(Integer.MIN_VALUE, -1, 0, 1, 2, Integer.MAX_VALUE)) {
            String value = values.get(step);
            actual = actual.put(key, value);
            expected = expected.put(key, value);
            step++;
        }

        Iterator<String> iterator = actual.iterator();
        IntObjectValueBag<String> expectedValues = expected.values();

        IntObjectPersistentMap<String> updatedActual = actual
                .put(3, "shared-beta")
                .remove(1)
                .put(4, "shared-alpha");
        ReferenceIntObjectPersistentMap updatedExpected = expected
                .put(3, "shared-beta")
                .remove(1)
                .put(4, "shared-alpha");

        assertEquals(expectedValues, IntObjectPersistentMapContractSupport.valueBag(iterator), "iterator snapshot");
        assertFalse(iterator.hasNext(), "iterator should be exhausted after snapshot consumption");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");
        assertMatchesReference("iterator updated map", updatedActual, updatedExpected, probeKeys);
        assertMatchesReference("iterator original map", actual, expected, probeKeys);
    }

    @Test
    default void iteratorContractMatchesImplementationOptions() {
        IntObjectPersistentMap<String> map = newMap()
                .put(1, "one")
                .put(2, "two")
                .put(3, "two");
        ReferenceIntObjectPersistentMap expected = ReferenceIntObjectPersistentMap.empty()
                .put(1, "one")
                .put(2, "two")
                .put(3, "two");
        IntObjectValueBag<String> expectedValues = expected.values();

        Iterator<String> iterator = map.iterator();

        assertTrue(iterator.hasNext(), "iterator should contain the first element");
        String firstValue = iterator.next();
        assertTrue(
                expectedValues.multiplicities().containsKey(firstValue),
                "iterator first element should come from the map");

        switch (contractOptions().iteratorRemoveBehavior()) {
            case UNSUPPORTED -> assertThrows(UnsupportedOperationException.class, iterator::remove, "iterator remove");
            case SUPPORTED -> assertDoesNotThrow(iterator::remove, "iterator remove");
        }

        IntObjectValueBag<String> seenValues = IntObjectPersistentMapContractSupport.valueBag(iterator).plus(firstValue);

        assertEquals(expectedValues, seenValues, "iterator should expose all values regardless of order");
        assertFalse(iterator.hasNext(), "iterator should be exhausted");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");

        Iterator<String> emptyIterator = newMap().iterator();
        assertFalse(emptyIterator.hasNext(), "empty iterator hasNext");
        assertThrows(NoSuchElementException.class, emptyIterator::next, "empty iterator next");
    }

    @Test
    default void nullValuesAreRejected() {
        assertThrows(NullPointerException.class, () -> newMap().put(1, null), "null values must be rejected");
    }

    private void assertMatchesReference(
            String context,
            IntObjectMap<String> actual,
            ReferenceIntObjectPersistentMap expected,
            List<Integer> probeKeys) {
        assertEquals(expected.size(), actual.size(), context + " size");

        for (int key : probeKeys) {
            assertEquals(
                    expected.find(key),
                    actual.find(key),
                    context + " find(" + IntObjectPersistentMapContractSupport.describeKey(key) + ")");
        }

        IntObjectValueBag<String> actualValues = IntObjectPersistentMapContractSupport.valueBag(actual);
        assertEquals(expected.size(), actualValues.totalCount(), context + " iterated count");
        assertEquals(expected.values(), actualValues, context + " iterated values");
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
}
