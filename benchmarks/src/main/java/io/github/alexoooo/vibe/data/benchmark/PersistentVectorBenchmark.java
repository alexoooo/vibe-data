package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.ChunkedPersistentVector;
import io.github.alexoooo.vibe.data.PersistentVector;
import io.github.alexoooo.vibe.data.SimplePersistentVector;
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
public class PersistentVectorBenchmark {

    private static final int MIXED_OPERATION_COUNT = 64;
    private static final int REVERSE_SAMPLE_COUNT = 8;

    public static void main(String[] args) throws Exception {
        String[] benchmarkArgs = args.length == 0
                ? new String[]{PersistentVectorBenchmark.class.getName() + ".*"}
                : args;
        Main.main(benchmarkArgs);
    }

    @Benchmark
    public PersistentVector<String> append(VectorState state) {
        return state.vector.append(state.appendValue);
    }

    @Benchmark
    public String first(VectorState state) {
        return state.vector.first();
    }

    @Benchmark
    public String getMiddle(VectorState state) {
        return state.vector.get(state.middleIndex);
    }

    @Benchmark
    public String getNearEnd(VectorState state) {
        return state.vector.get(state.nearEndIndex);
    }

    @Benchmark
    public int iterateForward(VectorState state, Blackhole blackhole) {
        int count = 0;
        Iterator<String> iterator = state.vector.iterator();
        while (iterator.hasNext()) {
            blackhole.consume(iterator.next());
            count++;
        }
        return count;
    }

    @Benchmark
    public int iterateReverse(VectorState state, Blackhole blackhole) {
        int count = 0;
        Iterator<String> iterator = state.vector.reverseIterator();
        while (iterator.hasNext()) {
            blackhole.consume(iterator.next());
            count++;
        }
        return count;
    }

    @Benchmark
    public String last(VectorState state) {
        return state.vector.last();
    }

    @Benchmark
    public PersistentVector<String> mixedAppendHeavy(VectorState state, Blackhole blackhole) {
        PersistentVector<String> vector = state.vector;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            vector = vector.append(state.mixedAppendValues[index]);
            blackhole.consume(vector.last());
            blackhole.consume(vector.get((state.size / 2 + (index * 17)) % vector.size()));

            if ((index & 7) == 0) {
                Iterator<String> reverseIterator = vector.reverseIterator();
                for (int sample = 0; sample < REVERSE_SAMPLE_COUNT && reverseIterator.hasNext(); sample++) {
                    blackhole.consume(reverseIterator.next());
                }
            }
        }
        return vector;
    }

    @Benchmark
    public PersistentVector<String> mixedReadHeavy(VectorState state, Blackhole blackhole) {
        PersistentVector<String> vector = state.vector;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            blackhole.consume(vector.first());
            blackhole.consume(vector.last());
            blackhole.consume(vector.get(state.mixedReadIndexes[index]));

            if ((index & 7) == 0) {
                Iterator<String> reverseIterator = vector.reverseIterator();
                for (int sample = 0; sample < REVERSE_SAMPLE_COUNT && reverseIterator.hasNext(); sample++) {
                    blackhole.consume(reverseIterator.next());
                }
            }
        }
        return vector;
    }

    @Benchmark
    public int size(VectorState state) {
        return state.vector.size();
    }

    @State(Scope.Thread)
    public static class VectorState {

        @Param({"simple", "chunked", "bifurcan", "dexx"})
        public String implementation;

        @Param({"128", "4096"})
        public int size;

        public PersistentVector<String> vector;
        public int middleIndex;
        public int nearEndIndex;
        public String appendValue;
        public int[] mixedReadIndexes;
        public String[] mixedAppendValues;

        @Setup(Level.Trial)
        public void setUp() {
            vector = createEmptyVector();
            for (int index = 0; index < size; index++) {
                vector = vector.append("value-" + index);
            }

            middleIndex = size / 2;
            nearEndIndex = Math.max(0, size - 5);
            appendValue = "append-value";
            setUpMixedScenarioInputs();
        }

        private PersistentVector<String> createEmptyVector() {
            return switch (implementation) {
                case "simple" -> SimplePersistentVector.<String>empty();
                case "chunked" -> ChunkedPersistentVector.<String>empty();
                case "bifurcan" -> BifurcanListPersistentVector.<String>empty();
                case "dexx" -> DexxPersistentVector.<String>empty();
                default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
            };
        }

        private void setUpMixedScenarioInputs() {
            mixedReadIndexes = new int[MIXED_OPERATION_COUNT];
            mixedAppendValues = new String[MIXED_OPERATION_COUNT];

            for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
                mixedReadIndexes[index] = (int) ((index * 31L) % size);
                mixedAppendValues[index] = "mixed-append-" + index;
            }
        }
    }
}
