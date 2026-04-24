package io.github.alexoooo.vibe.data.testing;

import io.github.alexoooo.vibe.data.IntObjectPersistentMap;

record IntObjectContractSnapshot(
        IntObjectPersistentMap<String> actual,
        ReferenceIntObjectPersistentMap expected,
        String context) {}
