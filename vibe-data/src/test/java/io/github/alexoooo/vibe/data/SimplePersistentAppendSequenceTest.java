package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.PersistentAppendSequenceContract;

class SimplePersistentAppendSequenceTest implements PersistentAppendSequenceContract {

    @Override
    public PersistentAppendSequence<String> newSequence() {
        return SimplePersistentAppendSequence.empty();
    }
}
