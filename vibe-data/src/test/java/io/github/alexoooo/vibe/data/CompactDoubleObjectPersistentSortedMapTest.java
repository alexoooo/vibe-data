package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.DoubleObjectPersistentSortedMapContract;

class CompactDoubleObjectPersistentSortedMapTest implements DoubleObjectPersistentSortedMapContract {

    @Override
    public DoubleObjectPersistentSortedMap<String> newAscendingMap() {
        return CompactDoubleObjectPersistentSortedMap.ascending();
    }

    @Override
    public DoubleObjectPersistentSortedMap<String> newDescendingMap() {
        return CompactDoubleObjectPersistentSortedMap.descending();
    }
}
