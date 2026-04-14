package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentVector;
import io.github.alexoooo.vibe.data.testing.PersistentVectorContract;

class BifurcanListPersistentVectorTest implements PersistentVectorContract {

    @Override
    public PersistentVector<String> newVector() {
        return BifurcanListPersistentVector.empty();
    }
}
