package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import io.github.alexoooo.vibe.data.testing.LongObjectPersistentMapContract;

class BifurcanMapLongObjectPersistentMapTest implements LongObjectPersistentMapContract {

    @Override
    public LongObjectPersistentMap<String> newMap() {
        return BifurcanMapLongObjectPersistentMap.empty();
    }
}
