package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 4, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class PersistentOrderedQueueBenchmark {

    private static final int MIXED_OPERATION_COUNT = 64;
    private static final int ITERATION_SAMPLE_COUNT = 8;

    public static void main(String[] args) throws Exception {
        String[] benchmarkArgs = args.length == 0
                ? new String[]{PersistentOrderedQueueBenchmark.class.getName() + ".*"}
                : args;
        Main.main(benchmarkArgs);
    }

    @Benchmark
    public PersistentOrderedQueue<Integer> addExisting(QueueState state) {
        return state.queue.add(state.existingValue);
    }

    @Benchmark
    public PersistentOrderedQueue<Integer> addNew(QueueState state) {
        return state.queue.add(state.missingValue);
    }

    @Benchmark
    public Integer first(QueueState state) {
        return state.queue.first();
    }

    @Benchmark
    public int iterateAll(QueueState state, Blackhole blackhole) {
        int count = 0;
        Iterator<Integer> iterator = state.queue.iterator();
        while (iterator.hasNext()) {
            blackhole.consume(iterator.next());
            count++;
        }
        return count;
    }

    @Benchmark
    public Integer last(QueueState state) {
        return state.queue.last();
    }

    @Benchmark
    public PersistentOrderedQueue<Integer> mixedReadHeavy(QueueState state, Blackhole blackhole) {
        PersistentOrderedQueue<Integer> queue = state.queue;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            blackhole.consume(queue.first());
            blackhole.consume(queue.last());
            blackhole.consume(queue.size());

            Iterator<Integer> iterator = queue.iterator();
            for (int sample = 0; sample < ITERATION_SAMPLE_COUNT && iterator.hasNext(); sample++) {
                blackhole.consume(iterator.next());
            }

            if ((index & 15) == 0) {
                queue = queue.replace(state.mixedRemoveValues[index], state.mixedAddValues[index]);
            }
        }
        return queue;
    }

    @Benchmark
    public PersistentOrderedQueue<Integer> mixedUpdateHeavy(QueueState state, Blackhole blackhole) {
        PersistentOrderedQueue<Integer> queue = state.queue;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            queue = queue.add(state.mixedAddValues[index]);
            blackhole.consume(queue.first());
            blackhole.consume(queue.last());

            if ((index & 1) == 0) {
                queue = queue.remove(state.mixedRemoveValues[index]);
            } else {
                queue = queue.replace(state.mixedRemoveValues[index], state.mixedAddValues[index]);
            }
        }
        return queue;
    }

    @Benchmark
    public PersistentOrderedQueue<Integer> removeExisting(QueueState state) {
        return state.queue.remove(state.existingValue);
    }

    @Benchmark
    public PersistentOrderedQueue<Integer> replaceExistingWithNew(QueueState state) {
        return state.queue.replace(state.existingValue, state.replacementValue);
    }

    @Benchmark
    public int size(QueueState state) {
        return state.queue.size();
    }

    @State(Scope.Thread)
    public static class QueueState {

        @Param({"simple", "treap", "dexx", "bifurcan"})
        public String implementation;

        @Param({"128", "4096"})
        public int size;

        public PersistentOrderedQueue<Integer> queue;
        public int existingValue;
        public int missingValue;
        public int replacementValue;
        public int[] mixedAddValues;
        public int[] mixedRemoveValues;

        @Setup(Level.Trial)
        public void setUp() {
            queue = BenchmarkFixtures.buildPersistentOrderedQueue(
                    implementation,
                    size,
                    Integer::valueOf);
            existingValue = size / 2;
            missingValue = size + 1;
            replacementValue = size + 10_000;
            mixedAddValues = new int[MIXED_OPERATION_COUNT];
            mixedRemoveValues = new int[MIXED_OPERATION_COUNT];
            for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
                mixedAddValues[index] = size + 1_000 + index;
                mixedRemoveValues[index] = (int) ((index * 31L) % size);
            }
        }

    }
}
