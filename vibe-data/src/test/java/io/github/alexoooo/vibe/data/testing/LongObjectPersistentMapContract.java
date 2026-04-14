package io.github.alexoooo.vibe.data.testing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alexoooo.vibe.data.LongObjectMap;
import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;

public interface LongObjectPersistentMapContract {

    LongObjectPersistentMap<String> newMap();

    default ContractOptions contractOptions() {
        return ContractOptions.defaults();
    }

    @Test
    default void emptyMapMatchesReferenceModel() {
        assertMatchesReference(
                "empty map",
                newMap(),
                ReferenceLongObjectPersistentMap.empty(),
                LongObjectPersistentMapContractSupport.specialProbeKeys());
    }

    @Test
    default void specialLongKeysAndDuplicateValuesMatchReferenceModel() {
        List<Long> insertKeys = LongObjectPersistentMapContractSupport.specialKeys();
        List<Long> probeKeys = LongObjectPersistentMapContractSupport.specialProbeKeys();
        List<String> duplicateValues = List.of(
                "duplicate-alpha",
                "duplicate-beta",
                "duplicate-alpha",
                "duplicate-gamma",
                "duplicate-beta");

        LongObjectPersistentMap<String> actual = newMap();
        ReferenceLongObjectPersistentMap expected = ReferenceLongObjectPersistentMap.empty();
        List<LongObjectContractSnapshot> snapshots = new ArrayList<>();
        snapshots.add(new LongObjectContractSnapshot(actual, expected, "special initial"));

        int step = 0;
        for (long key : insertKeys) {
            String value = duplicateValues.get(step % duplicateValues.size());
            actual = actual.put(key, value);
            expected = expected.put(key, value);

            if (step % 3 == 0) {
                snapshots.add(new LongObjectContractSnapshot(actual, expected, "special step " + step));
            }

            assertMatchesReference(
                    "special insert key=" + LongObjectPersistentMapContractSupport.describeKey(key),
                    actual,
                    expected,
                    probeKeys);
            step++;
        }

        for (long key : List.of(Long.MIN_VALUE, 0L, Long.MAX_VALUE)) {
            actual = actual.put(key, "duplicate-alpha");
            expected = expected.put(key, "duplicate-alpha");
        }
        actual = actual.put(42L, "duplicate-beta");
        expected = expected.put(42L, "duplicate-beta");

        assertMatchesReference("special overwrites", actual, expected, probeKeys);

        for (long key : List.of(0L, Long.MIN_VALUE + 2, Long.MAX_VALUE, 999_999_999L)) {
            actual = actual.remove(key);
            expected = expected.remove(key);

            assertMatchesReference(
                    "special remove key=" + LongObjectPersistentMapContractSupport.describeKey(key),
                    actual,
                    expected,
                    probeKeys);
        }

        for (LongObjectContractSnapshot snapshot : snapshots) {
            assertMatchesReference(
                    "special snapshot preserved " + snapshot.context(),
                    snapshot.actual(),
                    snapshot.expected(),
                    probeKeys);
        }
    }

    @Test
    default void sequentialLoopScenarioMatchesReferenceModel() {
        List<Long> probeKeys = LongObjectPersistentMapContractSupport.sequentialProbeKeys(128);

        LongObjectPersistentMap<String> actual = newMap();
        ReferenceLongObjectPersistentMap expected = ReferenceLongObjectPersistentMap.empty();
        List<LongObjectContractSnapshot> snapshots = new ArrayList<>();

        for (long key = 0; key < 128; key++) {
            if (key % 16 == 0) {
                snapshots.add(new LongObjectContractSnapshot(actual, expected, "sequential insert checkpoint " + key));
            }

            String value = LongObjectPersistentMapContractSupport.bucketedValue("sequential", (int) key, key, 7);
            actual = actual.put(key, value);
            expected = expected.put(key, value);

            if (key % 17 == 0 || key == 127) {
                assertMatchesReference("sequential insert key=" + key, actual, expected, probeKeys);
            }
        }

        for (long key = 0; key < 128; key += 5) {
            String value =
                    LongObjectPersistentMapContractSupport.bucketedValue("sequential-overwrite", (int) key, -key, 5);
            actual = actual.put(key, value);
            expected = expected.put(key, value);
        }

        assertMatchesReference("sequential overwrites", actual, expected, probeKeys);

        actual = actual.remove(-1L);
        expected = expected.remove(-1L);
        assertMatchesReference("sequential remove missing negative key", actual, expected, probeKeys);

        for (long key = 0; key < 128; key += 3) {
            actual = actual.remove(key);
            expected = expected.remove(key);

            if (key % 15 == 0 || key >= 126) {
                assertMatchesReference("sequential remove key=" + key, actual, expected, probeKeys);
            }
        }

        for (LongObjectContractSnapshot snapshot : snapshots) {
            assertMatchesReference(
                    "sequential snapshot preserved " + snapshot.context(),
                    snapshot.actual(),
                    snapshot.expected(),
                    probeKeys);
        }
    }

    @Test
    default void duplicateValuesAreTrackedByIterationMultiplicity() {
        List<Long> probeKeys = LongObjectPersistentMapContractSupport.dedupeInOrder(
                List.of(-1L, 10L, 20L, 30L, 40L, 50L, 60L, Long.MAX_VALUE));

        LongObjectPersistentMap<String> actual = newMap()
                .put(10L, "repeat")
                .put(20L, "repeat")
                .put(30L, "unique")
                .put(40L, "repeat")
                .put(50L, "unique");
        ReferenceLongObjectPersistentMap expected = ReferenceLongObjectPersistentMap.empty()
                .put(10L, "repeat")
                .put(20L, "repeat")
                .put(30L, "unique")
                .put(40L, "repeat")
                .put(50L, "unique");

        assertMatchesReference("duplicate values initial", actual, expected, probeKeys);

        actual = actual.put(20L, "unique").remove(30L).put(60L, "repeat");
        expected = expected.put(20L, "unique").remove(30L).put(60L, "repeat");

        assertMatchesReference("duplicate values updated", actual, expected, probeKeys);
        assertEquals(
                expected.values(),
                LongObjectPersistentMapContractSupport.valueBag(actual),
                "duplicate values should compare by multiplicity rather than sequence");
    }

    @Test
    default void randomizedOperationSequencesMatchReferenceModel() {
        for (int seedIndex = 0; seedIndex < 8; seedIndex++) {
            long seed = 0x5eed_5eedL + seedIndex;
            Random random = new Random(seed);
            List<Long> keyPool = LongObjectPersistentMapContractSupport.randomKeyPool(seed, 48);
            LongObjectPersistentMap<String> actual = newMap();
            ReferenceLongObjectPersistentMap expected = ReferenceLongObjectPersistentMap.empty();
            List<LongObjectContractSnapshot> snapshots = new ArrayList<>();
            snapshots.add(new LongObjectContractSnapshot(actual, expected, "random initial seed=" + seed));

            for (int step = 0; step < 256; step++) {
                if (step % 32 == 0) {
                    snapshots.add(new LongObjectContractSnapshot(
                            actual,
                            expected,
                            "random checkpoint seed=" + seed + " step=" + step));
                }

                long key = keyPool.get(random.nextInt(keyPool.size()));
                String context = "random seed=" + seed + " step=" + step;

                switch (random.nextInt(6)) {
                    case 0, 1 -> {
                        String value = LongObjectPersistentMapContractSupport.bucketedValue(
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
                            LongObjectPersistentMapContractSupport.valueBag(actual),
                            context + " direct iteration");
                    default -> throw new IllegalStateException("Unexpected random operation");
                }

                assertMatchesReference(context, actual, expected, keyPool);
            }

            for (LongObjectContractSnapshot snapshot : snapshots) {
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
        List<Long> probeKeys = LongObjectPersistentMapContractSupport.dedupeInOrder(
                List.of(Long.MIN_VALUE, -1L, 0L, 1L, 2L, 3L, 4L, Long.MAX_VALUE));

        LongObjectPersistentMap<String> actual = newMap();
        ReferenceLongObjectPersistentMap expected = ReferenceLongObjectPersistentMap.empty();

        List<String> values = List.of(
                "shared-alpha",
                "shared-beta",
                "shared-alpha",
                "shared-gamma",
                "shared-beta",
                "shared-alpha");
        int step = 0;
        for (long key : List.of(Long.MIN_VALUE, -1L, 0L, 1L, 2L, Long.MAX_VALUE)) {
            String value = values.get(step);
            actual = actual.put(key, value);
            expected = expected.put(key, value);
            step++;
        }

        Iterator<String> iterator = actual.iterator();
        LongObjectValueBag<String> expectedValues = expected.values();

        LongObjectPersistentMap<String> updatedActual = actual
                .put(3L, "shared-beta")
                .remove(1L)
                .put(4L, "shared-alpha");
        ReferenceLongObjectPersistentMap updatedExpected = expected
                .put(3L, "shared-beta")
                .remove(1L)
                .put(4L, "shared-alpha");

        assertEquals(expectedValues, LongObjectPersistentMapContractSupport.valueBag(iterator), "iterator snapshot");
        assertFalse(iterator.hasNext(), "iterator should be exhausted after snapshot consumption");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");
        assertMatchesReference("iterator updated map", updatedActual, updatedExpected, probeKeys);
        assertMatchesReference("iterator original map", actual, expected, probeKeys);
    }

    @Test
    default void iteratorContractMatchesImplementationOptions() {
        LongObjectPersistentMap<String> map = newMap()
                .put(1L, "one")
                .put(2L, "two")
                .put(3L, "two");
        ReferenceLongObjectPersistentMap expected = ReferenceLongObjectPersistentMap.empty()
                .put(1L, "one")
                .put(2L, "two")
                .put(3L, "two");
        LongObjectValueBag<String> expectedValues = expected.values();

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

        LongObjectValueBag<String> seenValues = LongObjectPersistentMapContractSupport.valueBag(iterator).plus(firstValue);

        assertEquals(expectedValues, seenValues, "iterator should expose all values regardless of order");
        assertFalse(iterator.hasNext(), "iterator should be exhausted");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");

        Iterator<String> emptyIterator = newMap().iterator();
        assertFalse(emptyIterator.hasNext(), "empty iterator hasNext");
        assertThrows(NoSuchElementException.class, emptyIterator::next, "empty iterator next");
    }

    @Test
    default void nullValuesAreRejected() {
        assertThrows(NullPointerException.class, () -> newMap().put(1L, null), "null values must be rejected");
    }

    private void assertMatchesReference(
            String context,
            LongObjectMap<String> actual,
            ReferenceLongObjectPersistentMap expected,
            List<Long> probeKeys) {
        assertEquals(expected.size(), actual.size(), context + " size");

        for (long key : probeKeys) {
            assertEquals(
                    expected.find(key),
                    actual.find(key),
                    context + " find(" + LongObjectPersistentMapContractSupport.describeKey(key) + ")");
        }

        LongObjectValueBag<String> actualValues = LongObjectPersistentMapContractSupport.valueBag(actual);
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
