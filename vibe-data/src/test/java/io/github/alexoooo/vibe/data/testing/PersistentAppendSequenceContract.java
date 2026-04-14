package io.github.alexoooo.vibe.data.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;

public interface PersistentAppendSequenceContract {

    PersistentAppendSequence<String> newSequence();

    @Test
    default void emptySequenceHasExpectedShape() {
        PersistentAppendSequence<String> sequence = newSequence();

        assertAppendSequenceMatchesReference("empty sequence", sequence, List.of());

        Iterator<String> iterator = sequence.iterator();
        assertFalse(iterator.hasNext(), "empty iterator hasNext");
        assertThrows(NoSuchElementException.class, iterator::next, "empty iterator next");
        assertThrows(UnsupportedOperationException.class, iterator::remove, "empty iterator remove");
    }

    @Test
    default void sequentialAppendsMatchReferenceModel() {
        PersistentAppendSequence<String> actual = newSequence();
        ArrayList<String> expected = new ArrayList<>();
        List<SequenceContractSnapshot<PersistentAppendSequence<String>>> snapshots = new ArrayList<>();
        snapshots.add(new SequenceContractSnapshot<>(actual, expected, "sequential initial"));

        for (int step = 0; step < 256; step++) {
            if (step % 32 == 0) {
                snapshots.add(new SequenceContractSnapshot<>(actual, expected, "sequential checkpoint " + step));
            }

            String value = PersistentSequenceContractSupport.bucketedValue("sequential", step, 11);
            actual = actual.append(value);
            expected.add(value);

            if (step % 17 == 0 || step == 255) {
                assertAppendSequenceMatchesReference(
                        "sequential step=" + step,
                        actual,
                        expected);
            }
        }

        for (SequenceContractSnapshot<PersistentAppendSequence<String>> snapshot : snapshots) {
            assertAppendSequenceMatchesReference(
                    "sequential snapshot preserved " + snapshot.context(),
                    snapshot.actual(),
                    snapshot.expected());
        }
    }

    @Test
    default void randomizedAppendScenariosMatchReferenceModel() {
        for (int seedIndex = 0; seedIndex < 8; seedIndex++) {
            long seed = 0x5eed_1234L + seedIndex;
            Random random = new Random(seed);
            PersistentAppendSequence<String> actual = newSequence();
            ArrayList<String> expected = new ArrayList<>();
            List<SequenceContractSnapshot<PersistentAppendSequence<String>>> snapshots = new ArrayList<>();
            snapshots.add(new SequenceContractSnapshot<>(actual, expected, "random initial seed=" + seed));

            for (int step = 0; step < 192; step++) {
                if (step % 24 == 0) {
                    snapshots.add(new SequenceContractSnapshot<>(
                            actual,
                            expected,
                            "random checkpoint seed=" + seed + " step=" + step));
                }

                String value = PersistentSequenceContractSupport.bucketedValue(
                        "random-" + seed,
                        step + random.nextInt(19),
                        13);
                actual = actual.append(value);
                expected.add(value);

                String context = "random seed=" + seed + " step=" + step;
                assertEquals(expected.size(), actual.size(), context + " size");
                assertEquals(expected.get(0), actual.first(), context + " first");
                assertEquals(expected.get(expected.size() - 1), actual.last(), context + " last");

                if (step % 19 == 0 || random.nextInt(8) == 0) {
                    assertAppendSequenceMatchesReference(context, actual, expected);
                }
            }

            for (SequenceContractSnapshot<PersistentAppendSequence<String>> snapshot : snapshots) {
                assertAppendSequenceMatchesReference(
                        "random snapshot preserved " + snapshot.context(),
                        snapshot.actual(),
                        snapshot.expected());
            }
        }
    }

    @Test
    default void iteratorsRemainBoundToOriginalSnapshot() {
        PersistentAppendSequence<String> actual = newSequence();
        ArrayList<String> expected = new ArrayList<>();

        for (int step = 0; step < 24; step++) {
            String value = PersistentSequenceContractSupport.bucketedValue("snapshot", step, 7);
            actual = actual.append(value);
            expected.add(value);
        }

        Iterator<String> iterator = actual.iterator();

        PersistentAppendSequence<String> updatedActual = actual
                .append("snapshot-tail-a")
                .append("snapshot-tail-b");
        ArrayList<String> updatedExpected = new ArrayList<>(expected);
        updatedExpected.add("snapshot-tail-a");
        updatedExpected.add("snapshot-tail-b");

        assertEquals(List.copyOf(expected), PersistentSequenceContractSupport.toList(iterator), "iterator snapshot");
        assertFalse(iterator.hasNext(), "iterator should be exhausted");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");

        assertAppendSequenceMatchesReference("snapshot original", actual, expected);
        assertAppendSequenceMatchesReference("snapshot updated", updatedActual, updatedExpected);
    }

    @Test
    default void iteratorContractMatchesExpectedOrder() {
        PersistentAppendSequence<String> sequence = newSequence()
                .append("one")
                .append("two")
                .append("two")
                .append("three");

        Iterator<String> iterator = sequence.iterator();

        assertTrue(iterator.hasNext(), "iterator should contain the first element");
        assertEquals("one", iterator.next(), "iterator first element");
        assertThrows(UnsupportedOperationException.class, iterator::remove, "iterator remove");

        assertEquals(
                List.of("two", "two", "three"),
                PersistentSequenceContractSupport.toList(iterator),
                "iterator tail");
        assertFalse(iterator.hasNext(), "iterator should be exhausted");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");
    }

    @Test
    default void nullValuesAreRejected() {
        assertThrows(NullPointerException.class, () -> newSequence().append(null), "null values must be rejected");
    }

    default void assertAppendSequenceMatchesReference(
            String context,
            PersistentAppendSequence<String> actual,
            List<String> expected) {
        assertEquals(expected.size(), actual.size(), context + " size");
        assertEquals(List.copyOf(expected), PersistentSequenceContractSupport.toList(actual), context + " iteration");

        if (expected.isEmpty()) {
            assertThrows(IndexOutOfBoundsException.class, actual::first, context + " first");
            assertThrows(IndexOutOfBoundsException.class, actual::last, context + " last");
        } else {
            assertEquals(expected.get(0), actual.first(), context + " first");
            assertEquals(expected.get(expected.size() - 1), actual.last(), context + " last");
        }
    }
}
