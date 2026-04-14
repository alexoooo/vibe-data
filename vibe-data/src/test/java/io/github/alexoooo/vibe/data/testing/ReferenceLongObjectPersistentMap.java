package io.github.alexoooo.vibe.data.testing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class ReferenceLongObjectPersistentMap {

    private final Map<Long, String> entries;

    private ReferenceLongObjectPersistentMap(Map<Long, String> entries) {
        this.entries = entries;
    }

    static ReferenceLongObjectPersistentMap empty() {
        return new ReferenceLongObjectPersistentMap(new LinkedHashMap<>());
    }

    @Nullable String find(long key) {
        return entries.get(key);
    }

    int size() {
        return entries.size();
    }

    LongObjectValueBag<String> values() {
        return LongObjectPersistentMapContractSupport.valueBag(entries.values());
    }

    ReferenceLongObjectPersistentMap put(long key, String value) {
        LinkedHashMap<Long, String> updatedEntries = new LinkedHashMap<>(entries);
        updatedEntries.put(key, Objects.requireNonNull(value, "value"));
        return new ReferenceLongObjectPersistentMap(updatedEntries);
    }

    ReferenceLongObjectPersistentMap remove(long key) {
        LinkedHashMap<Long, String> updatedEntries = new LinkedHashMap<>(entries);
        updatedEntries.remove(key);
        return new ReferenceLongObjectPersistentMap(updatedEntries);
    }
}
