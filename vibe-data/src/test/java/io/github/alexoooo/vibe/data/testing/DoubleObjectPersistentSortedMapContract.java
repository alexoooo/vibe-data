package io.github.alexoooo.vibe.data.testing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface DoubleObjectPersistentSortedMapContract {

    DoubleObjectPersistentSortedMap<String> newAscendingMap();

    DoubleObjectPersistentSortedMap<String> newDescendingMap();

    default ImplementationProperties implementationProperties() {
        return ImplementationProperties.defaults();
    }

    @Test
    default void emptyMapReturnsNullAndNoValues() {
        DoubleObjectPersistentSortedMap<String> map = newAscendingMap();

        assertEquals(0, map.size());
        assertNull(map.find(10.0));
        assertEquals(List.of(), toList(map.greaterOrEqualTo(Double.NEGATIVE_INFINITY)));
    }

    @Test
    default void putAndFindReturnExpectedValue() {
        DoubleObjectPersistentSortedMap<String> map = newAscendingMap()
                .put(2.0, "two")
                .put(1.0, "one")
                .put(3.0, "three");

        assertEquals(3, map.size());
        assertEquals("one", map.find(1.0));
        assertEquals("two", map.find(2.0));
        assertEquals("three", map.find(3.0));
        assertNull(map.find(4.0));
    }

    @Test
    default void sizeTracksDistinctKeysAndRemovals() {
        DoubleObjectPersistentSortedMap<String> empty = newAscendingMap();
        DoubleObjectPersistentSortedMap<String> single = empty.put(1.0, "one");
        DoubleObjectPersistentSortedMap<String> overwritten = single.put(1.0, "uno");
        DoubleObjectPersistentSortedMap<String> pair = overwritten.put(2.0, "two");
        DoubleObjectPersistentSortedMap<String> removed = pair.remove(1.0);
        DoubleObjectPersistentSortedMap<String> unchanged = removed.remove(42.0);

        assertEquals(0, empty.size());
        assertEquals(1, single.size());
        assertEquals(1, overwritten.size());
        assertEquals(2, pair.size());
        assertEquals(1, removed.size());
        assertEquals(1, unchanged.size());
    }

    @Test
    default void ascendingIterationStartsAtInclusiveLowerBound() {
        DoubleObjectPersistentSortedMap<String> map = newAscendingMap()
                .put(3.0, "three")
                .put(1.0, "one")
                .put(4.0, "four")
                .put(2.0, "two");

        assertEquals(List.of("two", "three", "four"), toList(map.greaterOrEqualTo(2.0)));
        assertEquals(List.of("three", "four"), toList(map.greaterOrEqualTo(2.5)));
    }

    @Test
    default void descendingIterationUsesDescendingTraversalOrder() {
        DoubleObjectPersistentSortedMap<String> map = newDescendingMap()
                .put(3.0, "three")
                .put(1.0, "one")
                .put(4.0, "four")
                .put(2.0, "two");

        assertEquals(List.of("four", "three", "two"), toList(map.greaterOrEqualTo(2.0)));
        assertEquals(List.of("four", "three"), toList(map.greaterOrEqualTo(2.5)));
    }

    @Test
    default void removeReturnsNewMapWithoutChangingPreviousInstance() {
        DoubleObjectPersistentSortedMap<String> original = newAscendingMap()
                .put(1.0, "one")
                .put(2.0, "two");

        DoubleObjectPersistentSortedMap<String> removed = original.remove(1.0);

        assertEquals(2, original.size());
        assertEquals(1, removed.size());
        assertEquals("one", original.find(1.0));
        assertNull(removed.find(1.0));
        assertEquals("two", original.find(2.0));
        assertEquals("two", removed.find(2.0));
    }

    @Test
    default void putReturnsNewMapWithoutChangingPreviousInstance() {
        DoubleObjectPersistentSortedMap<String> original = newAscendingMap();
        DoubleObjectPersistentSortedMap<String> updated = original.put(1.0, "one");

        assertEquals(0, original.size());
        assertEquals(1, updated.size());
        assertNull(original.find(1.0));
        assertEquals("one", updated.find(1.0));
    }

    @Test
    default void iteratorRemoveMatchesImplementationProperties() {
        DoubleObjectPersistentSortedMap<String> map = newAscendingMap()
                .put(1.0, "one")
                .put(2.0, "two");

        Iterator<String> iterator = map.greaterOrEqualTo(1.0);
        assertEquals("one", iterator.next());

        switch (implementationProperties().iteratorRemoveBehavior()) {
            case UNSUPPORTED -> {
                assertThrows(UnsupportedOperationException.class, iterator::remove);
                assertEquals(List.of("one", "two"), toList(map.greaterOrEqualTo(1.0)));
            }
            case SUPPORTED -> assertDoesNotThrow(iterator::remove);
        }
    }

    record ImplementationProperties(IteratorRemoveBehavior iteratorRemoveBehavior) {

        public static ImplementationProperties defaults() {
            return new ImplementationProperties(IteratorRemoveBehavior.UNSUPPORTED);
        }
    }

    enum IteratorRemoveBehavior {
        UNSUPPORTED,
        SUPPORTED
    }

    private static <T> List<T> toList(Iterator<T> iterator) {
        List<T> values = new ArrayList<>();
        iterator.forEachRemaining(values::add);
        return values;
    }
}
