package io.github.alexoooo.vibe.data.testing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

final class LongObjectPersistentMapContractSupport {

    private static final List<Long> SPECIAL_KEYS = List.of(
            Long.MIN_VALUE,
            Long.MIN_VALUE + 1,
            -1_000_000_000_000L,
            -1024L,
            -1L,
            0L,
            1L,
            2L,
            17L,
            42L,
            1_000_000_000_000L,
            0x0123_4567_89ab_cdefL,
            -0x0123_4567_89ab_cdefL,
            Long.MAX_VALUE - 1,
            Long.MAX_VALUE);

    private static final List<Long> SPECIAL_PROBE_KEYS = dedupeInOrder(List.of(
            Long.MIN_VALUE,
            Long.MIN_VALUE + 1,
            Long.MIN_VALUE + 2,
            -1_000_000_000_000L,
            -1024L,
            -1L,
            0L,
            1L,
            2L,
            3L,
            17L,
            42L,
            999_999_999L,
            1_000_000_000_000L,
            0x0123_4567_89ab_cdefL,
            -0x0123_4567_89ab_cdefL,
            Long.MAX_VALUE - 2,
            Long.MAX_VALUE - 1,
            Long.MAX_VALUE));

    private LongObjectPersistentMapContractSupport() {}

    static String bucketedValue(String scenario, int step, long key, int bucketCount) {
        int bucket = Math.floorMod(Long.hashCode(key) ^ step, bucketCount);
        return scenario + "-bucket-" + bucket;
    }

    static List<Long> specialKeys() {
        return SPECIAL_KEYS;
    }

    static List<Long> specialProbeKeys() {
        return SPECIAL_PROBE_KEYS;
    }

    static List<Long> sequentialProbeKeys(int size) {
        List<Long> keys = new ArrayList<>(SPECIAL_PROBE_KEYS);
        keys.add(-1L);
        for (long key = 0; key <= size; key++) {
            keys.add(key);
        }
        keys.add(size + 1L);
        return dedupeInOrder(keys);
    }

    static List<Long> randomKeyPool(long seed, int targetSize) {
        Random random = new Random(seed);
        LinkedHashSet<Long> keys = new LinkedHashSet<>(SPECIAL_KEYS);

        while (keys.size() < targetSize) {
            keys.add(random.nextLong());
        }

        return List.copyOf(keys);
    }

    static List<Long> dedupeInOrder(List<Long> values) {
        return List.copyOf(new LinkedHashSet<>(values));
    }

    static String describeKey(long key) {
        return key + " [0x" + Long.toHexString(key) + "]";
    }

    static <T> LongObjectValueBag<T> valueBag(Iterable<T> values) {
        return valueBag(values.iterator());
    }

    static <T> LongObjectValueBag<T> valueBag(Iterator<T> iterator) {
        LinkedHashMap<T, Integer> multiplicities = new LinkedHashMap<>();
        int totalCount = 0;

        while (iterator.hasNext()) {
            T value = Objects.requireNonNull(iterator.next(), "value");
            multiplicities.merge(value, 1, Integer::sum);
            totalCount++;
        }

        return new LongObjectValueBag<>(Map.copyOf(multiplicities), totalCount);
    }
}
