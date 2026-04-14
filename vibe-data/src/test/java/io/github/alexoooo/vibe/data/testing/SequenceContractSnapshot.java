package io.github.alexoooo.vibe.data.testing;

import java.util.List;

record SequenceContractSnapshot<T>(T actual, List<String> expected, String context) {

    SequenceContractSnapshot {
        expected = List.copyOf(expected);
    }
}
