package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.IntObjectPersistentMapContract;

class SimpleIntObjectPersistentMapTest implements IntObjectPersistentMapContract {

    @Override
    public IntObjectPersistentMap<String> newMap() {
        return SimpleIntObjectPersistentMap.empty();
    }
}
