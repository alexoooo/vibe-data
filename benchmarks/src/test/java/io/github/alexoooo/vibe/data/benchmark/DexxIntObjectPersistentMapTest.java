package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
import io.github.alexoooo.vibe.data.testing.IntObjectPersistentMapContract;

class DexxIntObjectPersistentMapTest implements IntObjectPersistentMapContract {

    @Override
    public IntObjectPersistentMap<String> newMap() {
        return DexxIntObjectPersistentMap.empty();
    }
}
