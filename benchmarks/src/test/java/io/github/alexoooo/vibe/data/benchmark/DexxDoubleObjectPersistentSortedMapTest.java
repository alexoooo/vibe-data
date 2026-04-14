package io.github.alexoooo.vibe.data.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class DexxDoubleObjectPersistentSortedMapTest {

    @Test
    void emptyMapReturnsNullAndNoValues() {
        DexxDoubleObjectPersistentSortedMap<String> map = DexxDoubleObjectPersistentSortedMap.ascending();

        assertEquals(0, map.size());
        assertNull(map.find(10.0));
        assertEquals(List.of(), toList(map.greaterOrEqualTo(Double.NEGATIVE_INFINITY)));
    }

    @Test
    void sizeTracksDistinctKeysAndRemovals() {
        DexxDoubleObjectPersistentSortedMap<String> empty = DexxDoubleObjectPersistentSortedMap.ascending();
        DexxDoubleObjectPersistentSortedMap<String> single = empty.put(1.0, "one");
        DexxDoubleObjectPersistentSortedMap<String> overwritten = single.put(1.0, "uno");
        DexxDoubleObjectPersistentSortedMap<String> pair = overwritten.put(2.0, "two");
        DexxDoubleObjectPersistentSortedMap<String> removed = pair.remove(1.0);
        DexxDoubleObjectPersistentSortedMap<String> unchanged = removed.remove(42.0);

        assertEquals(0, empty.size());
        assertEquals(1, single.size());
        assertEquals(1, overwritten.size());
        assertEquals(2, pair.size());
        assertEquals(1, removed.size());
        assertEquals(1, unchanged.size());
    }

    @Test
    void ascendingIterationStartsAtInclusiveLowerBound() {
        DexxDoubleObjectPersistentSortedMap<String> map = DexxDoubleObjectPersistentSortedMap.<String>ascending()
                .put(3.0, "three")
                .put(1.0, "one")
                .put(4.0, "four")
                .put(2.0, "two");

        assertEquals(List.of("two", "three", "four"), toList(map.greaterOrEqualTo(2.0)));
        assertEquals(List.of("three", "four"), toList(map.greaterOrEqualTo(2.5)));
    }

    @Test
    void descendingIterationUsesDescendingTraversalOrder() {
        DexxDoubleObjectPersistentSortedMap<String> map = DexxDoubleObjectPersistentSortedMap.<String>descending()
                .put(3.0, "three")
                .put(1.0, "one")
                .put(4.0, "four")
                .put(2.0, "two");

        assertEquals(List.of("four", "three", "two"), toList(map.greaterOrEqualTo(2.0)));
        assertEquals(List.of("four", "three"), toList(map.greaterOrEqualTo(2.5)));
    }

    @Test
    void mutationsReturnPersistentCopies() {
        DexxDoubleObjectPersistentSortedMap<String> original = DexxDoubleObjectPersistentSortedMap.<String>ascending()
                .put(1.0, "one")
                .put(2.0, "two");

        DexxDoubleObjectPersistentSortedMap<String> updated = original.put(1.0, "uno");
        DexxDoubleObjectPersistentSortedMap<String> removed = original.remove(1.0);

        assertEquals("one", original.find(1.0));
        assertEquals("uno", updated.find(1.0));
        assertNull(removed.find(1.0));
        assertEquals(2, original.size());
        assertEquals(2, updated.size());
        assertEquals(1, removed.size());
    }

    private static <T> List<T> toList(Iterator<T> iterator) {
        List<T> values = new ArrayList<>();
        iterator.forEachRemaining(values::add);
        return values;
    }
}
