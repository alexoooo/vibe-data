package io.github.alexoooo.vibe.data.testing;

import io.github.alexoooo.vibe.data.LongObjectPersistentMap;

record LongObjectContractSnapshot(
        LongObjectPersistentMap<String> actual,
        ReferenceLongObjectPersistentMap expected,
        String context) {}
