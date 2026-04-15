package io.github.alexoooo.vibe.data.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alexoooo.vibe.data.OrderedQueue;
import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;

public interface PersistentOrderedQueueContract {

    PersistentOrderedQueue<String> newQueue();

    PersistentOrderedQueue<String> newQueue(Comparator<? super String> comparator);

    @Test
    default void emptyQueueMatchesReferenceModel() {
        assertMatchesReference("empty queue", newQueue(), ReferenceOrderedQueue.naturalOrder());

        Iterator<String> iterator = newQueue().iterator();
        assertFalse(iterator.hasNext(), "empty iterator hasNext");
        assertThrows(NoSuchElementException.class, iterator::next, "empty iterator next");
        assertThrows(UnsupportedOperationException.class, iterator::remove, "empty iterator remove");
    }

    @Test
    default void naturalOrderSequentialScenarioMatchesReferenceModel() {
        PersistentOrderedQueue<String> actual = newQueue();
        ReferenceOrderedQueue<String> expected = ReferenceOrderedQueue.naturalOrder();
        List<OrderedQueueContractSnapshot<PersistentOrderedQueue<String>>> snapshots = new ArrayList<>();
        snapshots.add(new OrderedQueueContractSnapshot<>(actual, expected.values(), "natural initial"));

        for (int step = 0; step < 192; step++) {
            if (step % 24 == 0) {
                snapshots.add(new OrderedQueueContractSnapshot<>(actual, expected.values(), "natural checkpoint " + step));
            }

            String value = OrderedQueueContractSupport.naturalValue(step);
            actual = actual.add(value);
            expected = expected.add(value);

            if (step % 17 == 0 || step == 191) {
                assertMatchesReference("natural add step=" + step, actual, expected);
            }
        }

        for (int step = 0; step < 192; step += 5) {
            String value = OrderedQueueContractSupport.naturalValue(step);
            actual = actual.remove(value);
            expected = expected.remove(value);
        }
        assertMatchesReference("natural removes", actual, expected);

        actual = actual.replace(OrderedQueueContractSupport.naturalValue(10), "value-0500");
        expected = expected.replace(OrderedQueueContractSupport.naturalValue(10), "value-0500");
        actual = actual.replace("value-9999", "value-0750");
        expected = expected.replace("value-9999", "value-0750");
        assertMatchesReference("natural replaces", actual, expected);

        for (OrderedQueueContractSnapshot<PersistentOrderedQueue<String>> snapshot : snapshots) {
            assertMatchesReference(
                    "natural snapshot preserved " + snapshot.context(),
                    snapshot.actual(),
                    snapshot.expected());
        }
    }

    @Test
    default void customComparatorEquivalenceClassesMatchReferenceModel() {
        Comparator<String> comparator = OrderedQueueContractSupport.lengthComparator();
        PersistentOrderedQueue<String> actual = newQueue(comparator);
        ReferenceOrderedQueue<String> expected = ReferenceOrderedQueue.orderedBy(comparator);

        for (String value : OrderedQueueContractSupport.lengthComparatorScenarioValues()) {
            actual = actual.add(value);
            expected = expected.add(value);
            assertMatchesReference("length comparator add " + value, actual, expected);
        }

        actual = actual.add("zz").add("yyyy").add("mmmm");
        expected = expected.add("zz").add("yyyy").add("mmmm");
        assertMatchesReference("length comparator duplicate classes", actual, expected);

        actual = actual.replace("dd", "qq").replace("hhhh", "wwww").replace("xxxxx", "tt");
        expected = expected.replace("dd", "qq").replace("hhhh", "wwww").replace("xxxxx", "tt");
        assertMatchesReference("length comparator replace", actual, expected);
    }

    @Test
    default void randomizedNaturalOperationsMatchReferenceModel() {
        for (int seedIndex = 0; seedIndex < 8; seedIndex++) {
            long seed = 0x0dd3_1234L + seedIndex;
            Random random = new Random(seed);
            List<String> pool = OrderedQueueContractSupport.naturalScenarioValues(64);
            PersistentOrderedQueue<String> actual = newQueue();
            ReferenceOrderedQueue<String> expected = ReferenceOrderedQueue.naturalOrder();
            List<OrderedQueueContractSnapshot<PersistentOrderedQueue<String>>> snapshots = new ArrayList<>();
            snapshots.add(new OrderedQueueContractSnapshot<>(actual, expected.values(), "random natural initial seed=" + seed));

            for (int step = 0; step < 256; step++) {
                if (step % 32 == 0) {
                    snapshots.add(new OrderedQueueContractSnapshot<>(
                            actual,
                            expected.values(),
                            "random natural checkpoint seed=" + seed + " step=" + step));
                }

                String value = pool.get(random.nextInt(pool.size()));
                String replacement = pool.get(random.nextInt(pool.size()));
                String context = "random natural seed=" + seed + " step=" + step;

                switch (random.nextInt(6)) {
                    case 0, 1 -> {
                        actual = actual.add(value);
                        expected = expected.add(value);
                    }
                    case 2 -> {
                        actual = actual.remove(value);
                        expected = expected.remove(value);
                    }
                    case 3 -> {
                        actual = actual.replace(value, replacement);
                        expected = expected.replace(value, replacement);
                    }
                    case 4 -> assertEquals(expected.values(), OrderedQueueContractSupport.toList(actual), context + " direct iteration");
                    case 5 -> assertEquals(expected.size(), actual.size(), context + " direct size");
                    default -> throw new IllegalStateException("Unexpected operation");
                }

                assertMatchesReference(context, actual, expected);
            }

            for (OrderedQueueContractSnapshot<PersistentOrderedQueue<String>> snapshot : snapshots) {
                assertMatchesReference(
                        "random natural snapshot preserved " + snapshot.context(),
                        snapshot.actual(),
                        snapshot.expected());
            }
        }
    }

    @Test
    default void randomizedLengthComparatorOperationsMatchReferenceModel() {
        for (int seedIndex = 0; seedIndex < 8; seedIndex++) {
            long seed = 0x0dd3_5678L + seedIndex;
            Random random = new Random(seed);
            Comparator<String> comparator = OrderedQueueContractSupport.lengthComparator();
            List<String> pool = OrderedQueueContractSupport.randomLengthComparatorPool(seed, 64);
            PersistentOrderedQueue<String> actual = newQueue(comparator);
            ReferenceOrderedQueue<String> expected = ReferenceOrderedQueue.orderedBy(comparator);

            for (int step = 0; step < 224; step++) {
                String value = pool.get(random.nextInt(pool.size()));
                String replacement = pool.get(random.nextInt(pool.size()));
                String context = "random length seed=" + seed + " step=" + step;

                switch (random.nextInt(5)) {
                    case 0, 1 -> {
                        actual = actual.add(value);
                        expected = expected.add(value);
                    }
                    case 2 -> {
                        actual = actual.remove(value);
                        expected = expected.remove(value);
                    }
                    case 3 -> {
                        actual = actual.replace(value, replacement);
                        expected = expected.replace(value, replacement);
                    }
                    case 4 -> assertEquals(expected.values(), OrderedQueueContractSupport.toList(actual), context + " direct iteration");
                    default -> throw new IllegalStateException("Unexpected operation");
                }

                assertMatchesReference(context, actual, expected);
            }
        }
    }

    @Test
    default void iteratorsRemainBoundToOriginalSnapshot() {
        PersistentOrderedQueue<String> actual = newQueue();
        ReferenceOrderedQueue<String> expected = ReferenceOrderedQueue.naturalOrder();

        for (int step = 0; step < 24; step++) {
            String value = OrderedQueueContractSupport.naturalValue(step);
            actual = actual.add(value);
            expected = expected.add(value);
        }

        Iterator<String> iterator = actual.iterator();

        PersistentOrderedQueue<String> updatedActual = actual
                .remove(OrderedQueueContractSupport.naturalValue(5))
                .add("value-0999")
                .replace(OrderedQueueContractSupport.naturalValue(7), "value-0888");
        ReferenceOrderedQueue<String> updatedExpected = expected
                .remove(OrderedQueueContractSupport.naturalValue(5))
                .add("value-0999")
                .replace(OrderedQueueContractSupport.naturalValue(7), "value-0888");

        assertEquals(expected.values(), OrderedQueueContractSupport.toList(iterator), "iterator snapshot");
        assertFalse(iterator.hasNext(), "iterator should be exhausted");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");

        assertMatchesReference("iterator original", actual, expected);
        assertMatchesReference("iterator updated", updatedActual, updatedExpected);
    }

    @Test
    default void iteratorContractMatchesExpectedOrder() {
        PersistentOrderedQueue<String> queue = newQueue()
                .add("three")
                .add("one")
                .add("two");

        Iterator<String> iterator = queue.iterator();

        assertTrue(iterator.hasNext(), "iterator should contain the first element");
        assertEquals("one", iterator.next(), "iterator first element");
        assertThrows(UnsupportedOperationException.class, iterator::remove, "iterator remove");
        assertEquals(List.of("three", "two"), OrderedQueueContractSupport.toList(iterator), "iterator tail");
        assertFalse(iterator.hasNext(), "iterator should be exhausted");
        assertThrows(NoSuchElementException.class, iterator::next, "iterator next after exhaustion");
    }

    @Test
    default void nullValuesAreRejected() {
        assertThrows(NullPointerException.class, () -> newQueue().add(null), "null add");
        assertThrows(NullPointerException.class, () -> newQueue().remove(null), "null remove");
        assertThrows(NullPointerException.class, () -> newQueue().replace("one", null), "null replace add");
        assertThrows(NullPointerException.class, () -> newQueue().replace(null, "one"), "null replace remove");
    }

    private void assertMatchesReference(
            String context,
            OrderedQueue<String> actual,
            List<String> expectedValues) {
        assertEquals(expectedValues.size(), actual.size(), context + " size");
        assertEquals(expectedValues, OrderedQueueContractSupport.toList(actual), context + " iteration");

        if (expectedValues.isEmpty()) {
            assertThrows(IndexOutOfBoundsException.class, actual::first, context + " first");
            assertThrows(IndexOutOfBoundsException.class, actual::last, context + " last");
        } else {
            assertEquals(expectedValues.get(0), actual.first(), context + " first");
            assertEquals(expectedValues.get(expectedValues.size() - 1), actual.last(), context + " last");
        }
    }

    private void assertMatchesReference(
            String context,
            OrderedQueue<String> actual,
            ReferenceOrderedQueue<String> expected) {
        assertMatchesReference(context, actual, expected.values());
    }
}
