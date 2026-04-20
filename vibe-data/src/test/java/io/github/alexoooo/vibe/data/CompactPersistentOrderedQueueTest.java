package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.PersistentOrderedQueueContract;
import java.util.Comparator;

class CompactPersistentOrderedQueueTest implements PersistentOrderedQueueContract {

    @Override
    public PersistentOrderedQueue<String> newQueue() {
        return CompactPersistentOrderedQueue.empty();
    }

    @Override
    public PersistentOrderedQueue<String> newQueue(Comparator<? super String> comparator) {
        return CompactPersistentOrderedQueue.empty(comparator);
    }
}
