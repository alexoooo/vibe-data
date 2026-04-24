package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
import io.github.alexoooo.vibe.data.testing.IntObjectPersistentMapContract;

class BifurcanIntMapIntObjectPersistentMapTest implements IntObjectPersistentMapContract {

    @Override
    public IntObjectPersistentMap<String> newMap() {
        return BifurcanIntMapIntObjectPersistentMap.empty();
    }
}
