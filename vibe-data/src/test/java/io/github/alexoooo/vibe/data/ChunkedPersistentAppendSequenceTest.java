package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.PersistentAppendSequenceContract;

class ChunkedPersistentAppendSequenceTest implements PersistentAppendSequenceContract {

    @Override
    public PersistentAppendSequence<String> newSequence() {
        return ChunkedPersistentAppendSequence.empty();
    }
}
