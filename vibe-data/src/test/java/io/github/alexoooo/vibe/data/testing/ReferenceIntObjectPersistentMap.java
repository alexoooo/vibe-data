package io.github.alexoooo.vibe.data.testing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class ReferenceIntObjectPersistentMap {

    private final Map<Integer, String> entries;

    private ReferenceIntObjectPersistentMap(Map<Integer, String> entries) {
        this.entries = entries;
    }

    static ReferenceIntObjectPersistentMap empty() {
        return new ReferenceIntObjectPersistentMap(new LinkedHashMap<>());
    }

    @Nullable String find(int key) {
        return entries.get(key);
    }

    int size() {
        return entries.size();
    }

    IntObjectValueBag<String> values() {
        return IntObjectPersistentMapContractSupport.valueBag(entries.values());
    }

    ReferenceIntObjectPersistentMap put(int key, String value) {
        LinkedHashMap<Integer, String> updatedEntries = new LinkedHashMap<>(entries);
        updatedEntries.put(key, Objects.requireNonNull(value, "value"));
        return new ReferenceIntObjectPersistentMap(updatedEntries);
    }

    ReferenceIntObjectPersistentMap remove(int key) {
        LinkedHashMap<Integer, String> updatedEntries = new LinkedHashMap<>(entries);
        updatedEntries.remove(key);
        return new ReferenceIntObjectPersistentMap(updatedEntries);
    }
}
