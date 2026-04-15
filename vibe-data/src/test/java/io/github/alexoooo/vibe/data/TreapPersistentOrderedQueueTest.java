package io.github.alexoooo.vibe.data;

import io.github.alexoooo.vibe.data.testing.PersistentOrderedQueueContract;
import java.util.Comparator;

class TreapPersistentOrderedQueueTest implements PersistentOrderedQueueContract {

    @Override
    public PersistentOrderedQueue<String> newQueue() {
        return TreapPersistentOrderedQueue.empty();
    }

    @Override
    public PersistentOrderedQueue<String> newQueue(Comparator<? super String> comparator) {
        return TreapPersistentOrderedQueue.empty(comparator);
    }
}
