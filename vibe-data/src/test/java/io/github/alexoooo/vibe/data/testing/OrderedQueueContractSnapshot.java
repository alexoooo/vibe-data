package io.github.alexoooo.vibe.data.testing;

import java.util.List;

record OrderedQueueContractSnapshot<T>(T actual, List<String> expected, String context) {

    OrderedQueueContractSnapshot {
        expected = List.copyOf(expected);
    }
}
