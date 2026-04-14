package io.github.alexoooo.vibe.data.testing;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;

record DoubleObjectContractSnapshot(
        DoubleObjectPersistentSortedMap<String> actual,
        ReferenceDoubleObjectPersistentSortedMap expected,
        String context) {}
