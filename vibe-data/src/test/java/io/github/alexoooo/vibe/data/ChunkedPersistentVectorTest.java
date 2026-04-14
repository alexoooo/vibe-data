package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.PersistentVectorContract;

class ChunkedPersistentVectorTest implements PersistentVectorContract {

    @Override
    public PersistentVector<String> newVector() {
        return ChunkedPersistentVector.empty();
    }
}
