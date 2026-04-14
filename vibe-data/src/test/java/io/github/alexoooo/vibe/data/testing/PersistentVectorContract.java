package io.github.alexoooo.vibe.data.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import io.github.alexoooo.vibe.data.PersistentVector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;

public interface PersistentVectorContract extends PersistentAppendSequenceContract {

    PersistentVector<String> newVector();

    @Override
    default PersistentAppendSequence<String> newSequence() {
        return newVector();
    }

    @Test
    default void indexedReadsMatchReferenceModelAcrossSequentialAppends() {
        PersistentVector<String> actual = newVector();
        ArrayList<String> expected = new ArrayList<>();
        List<SequenceContractSnapshot<PersistentVector<String>>> snapshots = new ArrayList<>();
        snapshots.add(new SequenceContractSnapshot<>(actual, expected, "vector initial"));

        for (int step = 0; step < 384; step++) {
            if (step % 48 == 0) {
                snapshots.add(new SequenceContractSnapshot<>(actual, expected, "vector checkpoint " + step));
            }

            String value = PersistentSequenceContractSupport.bucketedValue("vector-sequential", step, 17);
            actual = actual.append(value);
            expected.add(value);

            if (step % 23 == 0 || step == 383) {
                assertVectorMatchesReference("vector sequential step=" + step, actual, expected);
            }
        }

        for (SequenceContractSnapshot<PersistentVector<String>> snapshot : snapshots) {
            assertVectorMatchesReference(
                    "vector snapshot preserved " + snapshot.context(),
                    snapshot.actual(),
                    snapshot.expected());
        }
    }

    @Test
    default void reverseIteratorMatchesReferenceModel() {
        PersistentVector<String> actual = newVector();
        ArrayList<String> expected = new ArrayList<>();

        for (int step = 0; step < 96; step++) {
            String value = PersistentSequenceContractSupport.bucketedValue("reverse", step, 9);
            actual = actual.append(value);
            expected.add(value);
        }

        Iterator<String> reverseIterator = actual.reverseIterator();

        assertTrue(reverseIterator.hasNext(), "reverse iterator should contain the last element");
        assertEquals(expected.get(expected.size() - 1), reverseIterator.next(), "reverse iterator first element");
        assertThrows(UnsupportedOperationException.class, reverseIterator::remove, "reverse iterator remove");

        ArrayList<String> remaining = new ArrayList<>();
        remaining.add(expected.get(expected.size() - 1));
        remaining.addAll(PersistentSequenceContractSupport.toList(reverseIterator));

        assertEquals(
                PersistentSequenceContractSupport.reversedCopy(expected),
                List.copyOf(remaining),
                "reverse iteration");
        assertFalse(reverseIterator.hasNext(), "reverse iterator should be exhausted");
        assertThrows(NoSuchElementException.class, reverseIterator::next, "reverse iterator next after exhaustion");
    }

    @Test
    default void randomizedVectorChecksMatchReferenceModel() {
        for (int seedIndex = 0; seedIndex < 8; seedIndex++) {
            long seed = 0x7eed_5678L + seedIndex;
            Random random = new Random(seed);
            PersistentVector<String> actual = newVector();
            ArrayList<String> expected = new ArrayList<>();
            List<SequenceContractSnapshot<PersistentVector<String>>> snapshots = new ArrayList<>();
            snapshots.add(new SequenceContractSnapshot<>(actual, expected, "vector random initial seed=" + seed));

            for (int step = 0; step < 224; step++) {
                if (step % 32 == 0) {
                    snapshots.add(new SequenceContractSnapshot<>(
                            actual,
                            expected,
                            "vector random checkpoint seed=" + seed + " step=" + step));
                }

                String value = PersistentSequenceContractSupport.bucketedValue(
                        "vector-random-" + seed,
                        step + random.nextInt(23),
                        19);
                actual = actual.append(value);
                expected.add(value);

                int probeIndex = random.nextInt(expected.size());
                String context = "vector random seed=" + seed + " step=" + step;
                assertEquals(expected.get(probeIndex), actual.get(probeIndex), context + " get(" + probeIndex + ")");

                if (step % 21 == 0 || random.nextInt(8) == 0) {
                    assertVectorMatchesReference(context, actual, expected);
                }
            }

            for (SequenceContractSnapshot<PersistentVector<String>> snapshot : snapshots) {
                assertVectorMatchesReference(
                        "vector random snapshot preserved " + snapshot.context(),
                        snapshot.actual(),
                        snapshot.expected());
            }
        }
    }

    @Test
    default void getRejectsInvalidIndexes() {
        PersistentVector<String> empty = newVector();
        assertThrows(IndexOutOfBoundsException.class, () -> empty.get(-1), "empty get negative");
        assertThrows(IndexOutOfBoundsException.class, () -> empty.get(0), "empty get zero");

        PersistentVector<String> vector = empty.append("one").append("two").append("three");
        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(-1), "get negative");
        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(3), "get size");
        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(99), "get large positive");
    }

    @Test
    default void reverseIteratorsRemainBoundToOriginalSnapshot() {
        PersistentVector<String> actual = newVector();
        ArrayList<String> expected = new ArrayList<>();

        for (int step = 0; step < 40; step++) {
            String value = PersistentSequenceContractSupport.bucketedValue("reverse-snapshot", step, 11);
            actual = actual.append(value);
            expected.add(value);
        }

        Iterator<String> reverseIterator = actual.reverseIterator();

        PersistentVector<String> updatedActual = actual
                .append("reverse-snapshot-tail-a")
                .append("reverse-snapshot-tail-b");
        ArrayList<String> updatedExpected = new ArrayList<>(expected);
        updatedExpected.add("reverse-snapshot-tail-a");
        updatedExpected.add("reverse-snapshot-tail-b");

        assertEquals(
                PersistentSequenceContractSupport.reversedCopy(expected),
                PersistentSequenceContractSupport.toList(reverseIterator),
                "reverse iterator snapshot");
        assertFalse(reverseIterator.hasNext(), "reverse iterator should be exhausted");
        assertThrows(NoSuchElementException.class, reverseIterator::next, "reverse iterator next after exhaustion");

        assertVectorMatchesReference("reverse snapshot original", actual, expected);
        assertVectorMatchesReference("reverse snapshot updated", updatedActual, updatedExpected);
    }

    default void assertVectorMatchesReference(
            String context,
            PersistentVector<String> actual,
            List<String> expected) {
        assertAppendSequenceMatchesReference(context, actual, expected);
        assertEquals(
                PersistentSequenceContractSupport.reversedCopy(expected),
                PersistentSequenceContractSupport.toList(actual.reverseIterator()),
                context + " reverse iteration");

        for (int index : PersistentSequenceContractSupport.interestingIndexes(expected.size())) {
            assertEquals(expected.get(index), actual.get(index), context + " get(" + index + ")");
        }
    }
}
