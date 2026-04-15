package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import io.github.alexoooo.vibe.data.testing.PersistentOrderedQueueContract;
import java.util.Comparator;

class BifurcanPersistentOrderedQueueTest implements PersistentOrderedQueueContract {

    @Override
    public PersistentOrderedQueue<String> newQueue() {
        return BifurcanPersistentOrderedQueue.empty();
    }

    @Override
    public PersistentOrderedQueue<String> newQueue(Comparator<? super String> comparator) {
        return BifurcanPersistentOrderedQueue.empty(comparator);
    }
}
