package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentVector;
import io.github.alexoooo.vibe.data.testing.PersistentVectorContract;

class DexxPersistentVectorTest implements PersistentVectorContract {

    @Override
    public PersistentVector<String> newVector() {
        return DexxPersistentVector.empty();
    }
}
