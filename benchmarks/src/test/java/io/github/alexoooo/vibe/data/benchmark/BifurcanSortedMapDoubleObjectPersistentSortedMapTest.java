package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.testing.DoubleObjectPersistentSortedMapContract;

class BifurcanSortedMapDoubleObjectPersistentSortedMapTest implements DoubleObjectPersistentSortedMapContract {

    @Override
    public DoubleObjectPersistentSortedMap<String> newAscendingMap() {
        return BifurcanSortedMapDoubleObjectPersistentSortedMap.ascending();
    }

    @Override
    public DoubleObjectPersistentSortedMap<String> newDescendingMap() {
        return BifurcanSortedMapDoubleObjectPersistentSortedMap.descending();
    }
}
