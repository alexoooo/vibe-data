package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import io.github.alexoooo.vibe.data.testing.PersistentAppendSequenceContract;

class BifurcanListPersistentAppendSequenceTest implements PersistentAppendSequenceContract {

    @Override
    public PersistentAppendSequence<String> newSequence() {
        return BifurcanListPersistentAppendSequence.empty();
    }
}
