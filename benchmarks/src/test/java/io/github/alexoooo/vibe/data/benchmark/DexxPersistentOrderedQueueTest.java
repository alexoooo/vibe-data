package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import io.github.alexoooo.vibe.data.testing.PersistentOrderedQueueContract;
import java.util.Comparator;

class DexxPersistentOrderedQueueTest implements PersistentOrderedQueueContract {

    @Override
    public PersistentOrderedQueue<String> newQueue() {
        return DexxPersistentOrderedQueue.empty();
    }

    @Override
    public PersistentOrderedQueue<String> newQueue(Comparator<? super String> comparator) {
        return DexxPersistentOrderedQueue.empty(comparator);
    }
}
