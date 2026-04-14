package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.testing.DoubleObjectPersistentSortedMapContract;

class DexxDoubleObjectPersistentSortedMapTest implements DoubleObjectPersistentSortedMapContract {

    @Override
    public DoubleObjectPersistentSortedMap<String> newAscendingMap() {
        return DexxDoubleObjectPersistentSortedMap.ascending();
    }

    @Override
    public DoubleObjectPersistentSortedMap<String> newDescendingMap() {
        return DexxDoubleObjectPersistentSortedMap.descending();
    }
}
