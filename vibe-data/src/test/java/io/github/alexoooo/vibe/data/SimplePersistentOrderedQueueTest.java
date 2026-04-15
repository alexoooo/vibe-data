package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.PersistentOrderedQueueContract;
import java.util.Comparator;

class SimplePersistentOrderedQueueTest implements PersistentOrderedQueueContract {

    @Override
    public PersistentOrderedQueue<String> newQueue() {
        return SimplePersistentOrderedQueue.empty();
    }

    @Override
    public PersistentOrderedQueue<String> newQueue(Comparator<? super String> comparator) {
        return SimplePersistentOrderedQueue.empty(comparator);
    }
}
