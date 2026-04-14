package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.PersistentVectorContract;

class SimplePersistentVectorTest implements PersistentVectorContract {

    @Override
    public PersistentVector<String> newVector() {
        return SimplePersistentVector.empty();
    }
}
