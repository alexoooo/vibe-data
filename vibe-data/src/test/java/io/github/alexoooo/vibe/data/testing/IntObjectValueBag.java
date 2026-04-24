package io.github.alexoooo.vibe.data.testing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

record IntObjectValueBag<T>(Map<T, Integer> multiplicities, int totalCount) {

    IntObjectValueBag<T> plus(T value) {
        LinkedHashMap<T, Integer> updated = new LinkedHashMap<>(multiplicities);
        updated.merge(Objects.requireNonNull(value, "value"), 1, Integer::sum);
        return new IntObjectValueBag<>(Map.copyOf(updated), totalCount + 1);
    }
}
