package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.lacuna.bifurcan.FloatMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import org.jspecify.annotations.Nullable;

final class BifurcanFloatMapDoubleObjectPersistentSortedMap<T> implements DoubleObjectPersistentSortedMap<T> {

    private static final long NEGATIVE_ZERO_BITS = Double.doubleToLongBits(-0.0d);

    private final FloatMap<T> entries;
    private final boolean descending;
    private final boolean hasNegativeZero;
    private final @Nullable T negativeZeroValue;
    private final boolean hasNaN;
    private final @Nullable T nanValue;

    private BifurcanFloatMapDoubleObjectPersistentSortedMap(
            FloatMap<T> entries,
            boolean descending,
            boolean hasNegativeZero,
            @Nullable T negativeZeroValue,
            boolean hasNaN,
            @Nullable T nanValue) {
        this.entries = entries;
        this.descending = descending;
        this.hasNegativeZero = hasNegativeZero;
        this.negativeZeroValue = negativeZeroValue;
        this.hasNaN = hasNaN;
        this.nanValue = nanValue;
    }

    static <T> BifurcanFloatMapDoubleObjectPersistentSortedMap<T> ascending() {
        return new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(new FloatMap<>(), false, false, null, false, null);
    }

    static <T> BifurcanFloatMapDoubleObjectPersistentSortedMap<T> descending() {
        return new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(new FloatMap<>(), true, false, null, false, null);
    }

    @Override
    public @Nullable T find(double key) {
        if (Double.isNaN(key)) {
            return hasNaN ? nanValue : null;
        }
        if (isNegativeZero(key)) {
            return hasNegativeZero ? negativeZeroValue : null;
        }
        return entries.get(key, null);
    }

    @Override
    public Iterator<T> greaterOrEqualTo(double key) {
        if (Double.isNaN(key)) {
            return hasNaN
                    ? Collections.singletonList(Objects.requireNonNull(nanValue, "nanValue")).iterator()
                    : Collections.emptyIterator();
        }

        List<T> values = new ArrayList<>(size());
        if (descending) {
            appendDescending(values, key);
        } else {
            appendAscending(values, key);
        }
        return Collections.unmodifiableList(values).iterator();
    }

    @Override
    public int size() {
        return Math.toIntExact(entries.size())
                + (hasNegativeZero ? 1 : 0)
                + (hasNaN ? 1 : 0);
    }

    @Override
    public BifurcanFloatMapDoubleObjectPersistentSortedMap<T> remove(double key) {
        if (Double.isNaN(key)) {
            return hasNaN
                    ? new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(
                            entries,
                            descending,
                            hasNegativeZero,
                            negativeZeroValue,
                            false,
                            null)
                    : this;
        }

        if (isNegativeZero(key)) {
            return hasNegativeZero
                    ? new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(
                            entries,
                            descending,
                            false,
                            null,
                            hasNaN,
                            nanValue)
                    : this;
        }

        FloatMap<T> updatedEntries = entries.remove(key);
        return updatedEntries == entries
                ? this
                : new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(
                        updatedEntries,
                        descending,
                        hasNegativeZero,
                        negativeZeroValue,
                        hasNaN,
                        nanValue);
    }

    @Override
    public BifurcanFloatMapDoubleObjectPersistentSortedMap<T> put(double key, T value) {
        T requiredValue = Objects.requireNonNull(value, "value");
        if (Double.isNaN(key)) {
            return new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(
                    entries,
                    descending,
                    hasNegativeZero,
                    negativeZeroValue,
                    true,
                    requiredValue);
        }

        if (isNegativeZero(key)) {
            return new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(
                    entries,
                    descending,
                    true,
                    requiredValue,
                    hasNaN,
                    nanValue);
        }

        FloatMap<T> updatedEntries = entries.put(key, requiredValue);
        return updatedEntries == entries
                ? this
                : new BifurcanFloatMapDoubleObjectPersistentSortedMap<>(
                        updatedEntries,
                        descending,
                        hasNegativeZero,
                        negativeZeroValue,
                        hasNaN,
                        nanValue);
    }

    private void appendAscending(List<T> values, double lowerBound) {
        boolean includeNegativeZero = hasNegativeZero && Double.compare(-0.0, lowerBound) >= 0;
        boolean insertedNegativeZero = false;
        OptionalLong startIndex = entries.ceilIndex(keyForEntries(lowerBound));

        if (startIndex.isPresent()) {
            long size = entries.size();
            for (long index = startIndex.getAsLong(); index < size; index++) {
                var entry = entries.nth(index);
                if (includeNegativeZero && !insertedNegativeZero && Double.compare(entry.key(), 0.0) >= 0) {
                    values.add(negativeZeroValue());
                    insertedNegativeZero = true;
                }
                values.add(entry.value());
            }
        }

        if (includeNegativeZero && !insertedNegativeZero) {
            values.add(negativeZeroValue());
        }

        if (hasNaN) {
            values.add(nanValue());
        }
    }

    private void appendDescending(List<T> values, double lowerBound) {
        if (hasNaN) {
            values.add(nanValue());
        }

        boolean includeNegativeZero = hasNegativeZero && Double.compare(-0.0, lowerBound) >= 0;
        boolean insertedNegativeZero = false;
        OptionalLong startIndex = entries.ceilIndex(keyForEntries(lowerBound));

        if (startIndex.isPresent()) {
            long firstIndex = startIndex.getAsLong();
            long size = entries.size();
            for (long index = size; index-- > firstIndex; ) {
                var entry = entries.nth(index);
                if (includeNegativeZero && !insertedNegativeZero && Double.compare(entry.key(), 0.0) < 0) {
                    values.add(negativeZeroValue());
                    insertedNegativeZero = true;
                }
                values.add(entry.value());
            }
        }

        if (includeNegativeZero && !insertedNegativeZero) {
            values.add(negativeZeroValue());
        }
    }

    private static boolean isNegativeZero(double key) {
        return Double.doubleToLongBits(key) == NEGATIVE_ZERO_BITS;
    }

    private static double keyForEntries(double key) {
        return isNegativeZero(key) ? 0.0 : key;
    }

    private T negativeZeroValue() {
        return Objects.requireNonNull(negativeZeroValue, "negativeZeroValue");
    }

    private T nanValue() {
        return Objects.requireNonNull(nanValue, "nanValue");
    }
}
