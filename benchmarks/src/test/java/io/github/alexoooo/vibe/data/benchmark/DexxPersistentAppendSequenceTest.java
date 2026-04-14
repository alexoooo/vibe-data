package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import io.github.alexoooo.vibe.data.testing.PersistentAppendSequenceContract;

class DexxPersistentAppendSequenceTest implements PersistentAppendSequenceContract {

    @Override
    public PersistentAppendSequence<String> newSequence() {
        return DexxPersistentAppendSequence.empty();
    }
}
