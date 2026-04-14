package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.LongObjectPersistentMapContract;

class SimpleLongObjectPersistentMapTest implements LongObjectPersistentMapContract {

    @Override
    public LongObjectPersistentMap<String> newMap() {
        return SimpleLongObjectPersistentMap.empty();
    }
}
