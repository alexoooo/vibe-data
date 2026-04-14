package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.DoubleObjectPersistentSortedMapContract;

class TreapDoubleObjectPersistentSortedMapTest implements DoubleObjectPersistentSortedMapContract {

    @Override
    public DoubleObjectPersistentSortedMap<String> newAscendingMap() {
        return TreapDoubleObjectPersistentSortedMap.ascending();
    }

    @Override
    public DoubleObjectPersistentSortedMap<String> newDescendingMap() {
        return TreapDoubleObjectPersistentSortedMap.descending();
    }
}
