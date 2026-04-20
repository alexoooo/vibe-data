package io.github.alexoooo.vibe.data.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import io.github.alexoooo.vibe.data.PersistentVector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class BenchmarkFixturesTest {

    @Test
    void buildsLongMapsForEveryImplementation() {
        for (String implementation : List.of("simple", "hamt", "compact", "dexx", "bifurcan", "bifurcanMap")) {
            LongObjectPersistentMap<String> map =
                    BenchmarkFixtures.buildLongObjectPersistentMap(implementation, 8, index -> "value-" + index);

            assertEquals(8, map.size(), implementation);
            assertEquals("value-3", map.find(3L), implementation);
        }
    }

    @Test
    void buildsSortedMapsForEveryImplementationAndOrder() {
        for (String implementation : List.of("simple", "treap", "compact", "dexx", "bifurcanSorted", "bifurcanFloat")) {
            assertSortedOrder(implementation, "ascending", List.of("value-6", "value-7"));
            assertSortedOrder(implementation, "descending", List.of("value-7", "value-6"));
        }
    }

    @Test
    void buildsOrderedQueuesForEveryImplementation() {
        for (String implementation : List.of("simple", "treap", "compact", "dexx", "bifurcan")) {
            PersistentOrderedQueue<TestValue> queue =
                    BenchmarkFixtures.buildPersistentOrderedQueue(implementation, 8, TestValue::new);

            assertEquals(8, queue.size(), implementation);
            assertEquals(new TestValue(0), queue.first(), implementation);
            assertEquals(new TestValue(7), queue.last(), implementation);
        }
    }

    @Test
    void buildsAppendSequencesForEveryImplementation() {
        for (String implementation : List.of("simple", "chunkedVector", "chunkedAppend", "bifurcan", "dexx")) {
            PersistentAppendSequence<String> sequence =
                    BenchmarkFixtures.buildPersistentAppendSequence(implementation, 8, index -> "value-" + index);

            assertEquals(8, sequence.size(), implementation);
            assertEquals("value-0", sequence.first(), implementation);
            assertEquals("value-7", sequence.last(), implementation);
        }
    }

    @Test
    void buildsVectorsForEveryImplementation() {
        for (String implementation : List.of("simple", "chunked", "bifurcan", "dexx")) {
            PersistentVector<String> vector =
                    BenchmarkFixtures.buildPersistentVector(implementation, 8, index -> "value-" + index);

            assertEquals(8, vector.size(), implementation);
            assertEquals("value-0", vector.first(), implementation);
            assertEquals("value-7", vector.last(), implementation);
            assertEquals("value-4", vector.get(4), implementation);
        }
    }

    private static void assertSortedOrder(String implementation, String order, List<String> expected) {
        DoubleObjectPersistentSortedMap<String> map =
                BenchmarkFixtures.buildDoubleObjectPersistentSortedMap(implementation, order, 8, index -> "value-" + index);
        assertEquals(8, map.size(), implementation + "/" + order);
        Iterator<String> iterator = map.greaterOrEqualTo(6.0);
        List<String> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }
        assertEquals(expected, actual, implementation + "/" + order);
    }

    private record TestValue(int id) implements Comparable<TestValue> {

        @Override
        public int compareTo(TestValue other) {
            return Integer.compare(id, other.id);
        }
    }
}
