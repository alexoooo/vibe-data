package io.github.alexoooo.vibe.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimpleDoubleObjectPersistentSortedMapTest {

    @Test
    void emptyMapReturnsNullAndNoValues() {
        SimpleDoubleObjectPersistentSortedMap<String> map = SimpleDoubleObjectPersistentSortedMap.ascending();

        assertNull(map.find(10.0));
        assertEquals(List.of(), toList(map.greaterOrEqualTo(Double.NEGATIVE_INFINITY)));
    }

    @Test
    void putAndFindReturnExpectedValue() {
        SimpleDoubleObjectPersistentSortedMap<String> map = SimpleDoubleObjectPersistentSortedMap.<String>ascending()
                .put(2.0, "two")
                .put(1.0, "one")
                .put(3.0, "three");

        assertEquals("one", map.find(1.0));
        assertEquals("two", map.find(2.0));
        assertEquals("three", map.find(3.0));
        assertNull(map.find(4.0));
    }

    @Test
    void ascendingIterationStartsAtInclusiveLowerBound() {
        SimpleDoubleObjectPersistentSortedMap<String> map = SimpleDoubleObjectPersistentSortedMap.<String>ascending()
                .put(3.0, "three")
                .put(1.0, "one")
                .put(4.0, "four")
                .put(2.0, "two");

        assertEquals(List.of("two", "three", "four"), toList(map.greaterOrEqualTo(2.0)));
        assertEquals(List.of("three", "four"), toList(map.greaterOrEqualTo(2.5)));
    }

    @Test
    void descendingIterationUsesDescendingTraversalOrder() {
        SimpleDoubleObjectPersistentSortedMap<String> map = SimpleDoubleObjectPersistentSortedMap.<String>descending()
                .put(3.0, "three")
                .put(1.0, "one")
                .put(4.0, "four")
                .put(2.0, "two");

        assertEquals(List.of("four", "three", "two"), toList(map.greaterOrEqualTo(2.0)));
        assertEquals(List.of("four", "three"), toList(map.greaterOrEqualTo(2.5)));
    }

    @Test
    void removeReturnsNewMapWithoutChangingPreviousInstance() {
        SimpleDoubleObjectPersistentSortedMap<String> original = SimpleDoubleObjectPersistentSortedMap.<String>ascending()
                .put(1.0, "one")
                .put(2.0, "two");

        SimpleDoubleObjectPersistentSortedMap<String> removed = original.remove(1.0);

        assertEquals("one", original.find(1.0));
        assertNull(removed.find(1.0));
        assertEquals("two", original.find(2.0));
        assertEquals("two", removed.find(2.0));
    }

    @Test
    void putReturnsNewMapWithoutChangingPreviousInstance() {
        SimpleDoubleObjectPersistentSortedMap<String> original = SimpleDoubleObjectPersistentSortedMap.ascending();
        SimpleDoubleObjectPersistentSortedMap<String> updated = original.put(1.0, "one");

        assertNull(original.find(1.0));
        assertEquals("one", updated.find(1.0));
    }

    @Test
    void iteratorDoesNotPermitMutation() {
        SimpleDoubleObjectPersistentSortedMap<String> map = SimpleDoubleObjectPersistentSortedMap.<String>ascending()
                .put(1.0, "one")
                .put(2.0, "two");

        Iterator<String> iterator = map.greaterOrEqualTo(1.0);
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(List.of("one", "two"), toList(map.greaterOrEqualTo(1.0)));
    }

    private static <T> List<T> toList(Iterator<T> iterator) {
        List<T> values = new ArrayList<>();
        iterator.forEachRemaining(values::add);
        return values;
    }
}
