package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.LongObjectPersistentMapContract;

class CompactLongObjectPersistentMapTest implements LongObjectPersistentMapContract {

    @Override
    public LongObjectPersistentMap<String> newMap() {
        return CompactLongObjectPersistentMap.empty();
    }
}
