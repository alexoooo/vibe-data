package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.ChunkedPersistentAppendSequence;
import io.github.alexoooo.vibe.data.ChunkedPersistentVector;
import io.github.alexoooo.vibe.data.CompactDoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.CompactLongObjectPersistentMap;
import io.github.alexoooo.vibe.data.CompactPersistentOrderedQueue;
import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.HamtIntObjectPersistentMap;
import io.github.alexoooo.vibe.data.HamtLongObjectPersistentMap;
import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import io.github.alexoooo.vibe.data.PersistentVector;
import io.github.alexoooo.vibe.data.SimpleIntObjectPersistentMap;
import io.github.alexoooo.vibe.data.SimpleDoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.SimpleLongObjectPersistentMap;
import io.github.alexoooo.vibe.data.SimplePersistentAppendSequence;
import io.github.alexoooo.vibe.data.SimplePersistentOrderedQueue;
import io.github.alexoooo.vibe.data.SimplePersistentVector;
import io.github.alexoooo.vibe.data.TreapDoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.TreapPersistentOrderedQueue;
import java.util.function.IntFunction;

final class BenchmarkFixtures {

    private BenchmarkFixtures() {
    }

    static <T> DoubleObjectPersistentSortedMap<T> buildDoubleObjectPersistentSortedMap(
            String implementation,
            String order,
            int size,
            IntFunction<? extends T> valueFactory) {
        DoubleObjectPersistentSortedMap<T> map = createDoubleObjectPersistentSortedMap(implementation, order);
        for (int index = 0; index < size; index++) {
            map = map.put(index, valueFactory.apply(index));
        }
        return map;
    }

    static <T> LongObjectPersistentMap<T> buildLongObjectPersistentMap(
            String implementation,
            int size,
            IntFunction<? extends T> valueFactory) {
        LongObjectPersistentMap<T> map = createLongObjectPersistentMap(implementation);
        for (int index = 0; index < size; index++) {
            map = map.put(index, valueFactory.apply(index));
        }
        return map;
    }

    static <T> IntObjectPersistentMap<T> buildIntObjectPersistentMap(
            String implementation,
            int size,
            IntFunction<? extends T> valueFactory) {
        IntObjectPersistentMap<T> map = createIntObjectPersistentMap(implementation);
        for (int index = 0; index < size; index++) {
            map = map.put(index, valueFactory.apply(index));
        }
        return map;
    }

    static <T extends Comparable<? super T>> PersistentOrderedQueue<T> buildPersistentOrderedQueue(
            String implementation,
            int size,
            IntFunction<? extends T> valueFactory) {
        PersistentOrderedQueue<T> queue = createPersistentOrderedQueue(implementation);
        for (int index = 0; index < size; index++) {
            queue = queue.add(valueFactory.apply(index));
        }
        return queue;
    }

    static <T> PersistentAppendSequence<T> buildPersistentAppendSequence(
            String implementation,
            int size,
            IntFunction<? extends T> valueFactory) {
        PersistentAppendSequence<T> sequence = createPersistentAppendSequence(implementation);
        for (int index = 0; index < size; index++) {
            sequence = sequence.append(valueFactory.apply(index));
        }
        return sequence;
    }

    static <T> PersistentVector<T> buildPersistentVector(
            String implementation,
            int size,
            IntFunction<? extends T> valueFactory) {
        PersistentVector<T> vector = createPersistentVector(implementation);
        for (int index = 0; index < size; index++) {
            vector = vector.append(valueFactory.apply(index));
        }
        return vector;
    }

    static <T> DoubleObjectPersistentSortedMap<T> createDoubleObjectPersistentSortedMap(
            String implementation,
            String order) {
        return switch (implementation) {
            case "simple" -> "descending".equals(order)
                    ? SimpleDoubleObjectPersistentSortedMap.<T>descending()
                    : SimpleDoubleObjectPersistentSortedMap.<T>ascending();
            case "treap" -> "descending".equals(order)
                    ? TreapDoubleObjectPersistentSortedMap.<T>descending()
                    : TreapDoubleObjectPersistentSortedMap.<T>ascending();
            case "dexx" -> "descending".equals(order)
                    ? DexxDoubleObjectPersistentSortedMap.<T>descending()
                    : DexxDoubleObjectPersistentSortedMap.<T>ascending();
            case "compact" -> "descending".equals(order)
                    ? CompactDoubleObjectPersistentSortedMap.<T>descending()
                    : CompactDoubleObjectPersistentSortedMap.<T>ascending();
            case "bifurcanSorted" -> "descending".equals(order)
                    ? BifurcanSortedMapDoubleObjectPersistentSortedMap.<T>descending()
                    : BifurcanSortedMapDoubleObjectPersistentSortedMap.<T>ascending();
            case "bifurcanFloat" -> "descending".equals(order)
                    ? BifurcanFloatMapDoubleObjectPersistentSortedMap.<T>descending()
                    : BifurcanFloatMapDoubleObjectPersistentSortedMap.<T>ascending();
            default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
        };
    }

    static <T> LongObjectPersistentMap<T> createLongObjectPersistentMap(String implementation) {
        return switch (implementation) {
            case "simple" -> SimpleLongObjectPersistentMap.<T>empty();
            case "hamt" -> HamtLongObjectPersistentMap.<T>empty();
            case "compact" -> CompactLongObjectPersistentMap.<T>empty();
            case "dexx" -> DexxLongObjectPersistentMap.<T>empty();
            case "bifurcan" -> BifurcanIntMapLongObjectPersistentMap.<T>empty();
            case "bifurcanMap" -> BifurcanMapLongObjectPersistentMap.<T>empty();
            default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
        };
    }

    static <T> IntObjectPersistentMap<T> createIntObjectPersistentMap(String implementation) {
        return switch (implementation) {
            case "simple" -> SimpleIntObjectPersistentMap.<T>empty();
            case "hamt" -> HamtIntObjectPersistentMap.<T>empty();
            case "dexx" -> DexxIntObjectPersistentMap.<T>empty();
            case "bifurcan" -> BifurcanIntMapIntObjectPersistentMap.<T>empty();
            case "bifurcanMap" -> BifurcanMapIntObjectPersistentMap.<T>empty();
            default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
        };
    }

    static <T extends Comparable<? super T>> PersistentOrderedQueue<T> createPersistentOrderedQueue(
            String implementation) {
        return switch (implementation) {
            case "simple" -> SimplePersistentOrderedQueue.<T>empty();
            case "treap" -> TreapPersistentOrderedQueue.<T>empty();
            case "compact" -> CompactPersistentOrderedQueue.<T>empty();
            case "dexx" -> DexxPersistentOrderedQueue.<T>empty();
            case "bifurcan" -> BifurcanPersistentOrderedQueue.<T>empty();
            default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
        };
    }

    static <T> PersistentAppendSequence<T> createPersistentAppendSequence(String implementation) {
        return switch (implementation) {
            case "simple" -> SimplePersistentAppendSequence.<T>empty();
            case "chunkedVector" -> ChunkedPersistentVector.<T>empty();
            case "chunkedAppend" -> ChunkedPersistentAppendSequence.<T>empty();
            case "bifurcan" -> BifurcanListPersistentAppendSequence.<T>empty();
            case "dexx" -> DexxPersistentAppendSequence.<T>empty();
            default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
        };
    }

    static <T> PersistentVector<T> createPersistentVector(String implementation) {
        return switch (implementation) {
            case "simple" -> SimplePersistentVector.<T>empty();
            case "chunked" -> ChunkedPersistentVector.<T>empty();
            case "bifurcan" -> BifurcanListPersistentVector.<T>empty();
            case "dexx" -> DexxPersistentVector.<T>empty();
            default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
        };
    }
}
