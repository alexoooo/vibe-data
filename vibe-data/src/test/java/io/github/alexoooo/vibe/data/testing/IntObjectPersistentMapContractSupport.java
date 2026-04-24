package io.github.alexoooo.vibe.data.testing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

final class IntObjectPersistentMapContractSupport {

    private static final List<Integer> SPECIAL_KEYS = List.of(
            Integer.MIN_VALUE,
            Integer.MIN_VALUE + 1,
            -1_000_000_000,
            -1024,
            -1,
            0,
            1,
            2,
            17,
            42,
            1_000_000_000,
            0x0123_4567,
            -0x0123_4567,
            Integer.MAX_VALUE - 1,
            Integer.MAX_VALUE);

    private static final List<Integer> SPECIAL_PROBE_KEYS = dedupeInOrder(List.of(
            Integer.MIN_VALUE,
            Integer.MIN_VALUE + 1,
            Integer.MIN_VALUE + 2,
            -1_000_000_000,
            -1024,
            -1,
            0,
            1,
            2,
            3,
            17,
            42,
            999_999_999,
            1_000_000_000,
            0x0123_4567,
            -0x0123_4567,
            Integer.MAX_VALUE - 2,
            Integer.MAX_VALUE - 1,
            Integer.MAX_VALUE));

    private IntObjectPersistentMapContractSupport() {}

    static String bucketedValue(String scenario, int step, int key, int bucketCount) {
        int bucket = Math.floorMod(Integer.hashCode(key) ^ step, bucketCount);
        return scenario + "-bucket-" + bucket;
    }

    static List<Integer> specialKeys() {
        return SPECIAL_KEYS;
    }

    static List<Integer> specialProbeKeys() {
        return SPECIAL_PROBE_KEYS;
    }

    static List<Integer> sequentialProbeKeys(int size) {
        List<Integer> keys = new ArrayList<>(SPECIAL_PROBE_KEYS);
        keys.add(-1);
        for (int key = 0; key <= size; key++) {
            keys.add(key);
        }
        keys.add(size + 1);
        return dedupeInOrder(keys);
    }

    static List<Integer> randomKeyPool(long seed, int targetSize) {
        Random random = new Random(seed);
        LinkedHashSet<Integer> keys = new LinkedHashSet<>(SPECIAL_KEYS);

        while (keys.size() < targetSize) {
            keys.add(random.nextInt());
        }

        return List.copyOf(keys);
    }

    static List<Integer> dedupeInOrder(List<Integer> values) {
        return List.copyOf(new LinkedHashSet<>(values));
    }

    static String describeKey(int key) {
        return key + " [0x" + Integer.toHexString(key) + "]";
    }

    static <T> IntObjectValueBag<T> valueBag(Iterable<T> values) {
        return valueBag(values.iterator());
    }

    static <T> IntObjectValueBag<T> valueBag(Iterator<T> iterator) {
        LinkedHashMap<T, Integer> multiplicities = new LinkedHashMap<>();
        int totalCount = 0;

        while (iterator.hasNext()) {
            T value = Objects.requireNonNull(iterator.next(), "value");
            multiplicities.merge(value, 1, Integer::sum);
            totalCount++;
        }

        return new IntObjectValueBag<>(Map.copyOf(multiplicities), totalCount);
    }
}
