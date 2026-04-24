package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.IntObjectPersistentMapContract;

class HamtIntObjectPersistentMapTest implements IntObjectPersistentMapContract {

    @Override
    public IntObjectPersistentMap<String> newMap() {
        return HamtIntObjectPersistentMap.empty();
    }
}
