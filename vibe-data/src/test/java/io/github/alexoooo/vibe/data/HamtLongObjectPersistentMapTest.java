package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.LongObjectPersistentMapContract;

class HamtLongObjectPersistentMapTest implements LongObjectPersistentMapContract {

    @Override
    public LongObjectPersistentMap<String> newMap() {
        return HamtLongObjectPersistentMap.empty();
    }
}
