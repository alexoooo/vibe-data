package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.DoubleObjectPersistentSortedMapContract;

class SimpleDoubleObjectPersistentSortedMapTest implements DoubleObjectPersistentSortedMapContract {

    @Override
    public DoubleObjectPersistentSortedMap<String> newAscendingMap() {
        return SimpleDoubleObjectPersistentSortedMap.ascending();
    }

    @Override
    public DoubleObjectPersistentSortedMap<String> newDescendingMap() {
        return SimpleDoubleObjectPersistentSortedMap.descending();
    }
}
