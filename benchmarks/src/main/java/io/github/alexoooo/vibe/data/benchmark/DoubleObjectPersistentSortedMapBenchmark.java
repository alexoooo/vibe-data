package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.SimpleDoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.TreapDoubleObjectPersistentSortedMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.Nullable;
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
public class DoubleObjectPersistentSortedMapBenchmark {

    private static final int MIXED_OPERATION_COUNT = 64;
    private static final int MIXED_ITERATION_SAMPLE = 4;

    public static void main(String[] args) throws Exception {
        String[] benchmarkArgs = args.length == 0
                ? new String[]{DoubleObjectPersistentSortedMapBenchmark.class.getName() + ".*"}
                : args;
        Main.main(benchmarkArgs);
    }

    @Benchmark
    public @Nullable String findExisting(MapState state) {
        return state.map.find(state.existingKey);
    }

    @Benchmark
    public @Nullable String findMissing(MapState state) {
        return state.map.find(state.missingKey);
    }

    @Benchmark
    public int size(MapState state) {
        return state.map.size();
    }

    @Benchmark
    public DoubleObjectPersistentSortedMap<String> putExisting(MapState state) {
        return state.map.put(state.existingKey, state.replacementValue);
    }

    @Benchmark
    public DoubleObjectPersistentSortedMap<String> putNew(MapState state) {
        return state.map.put(state.missingKey, state.replacementValue);
    }

    @Benchmark
    public DoubleObjectPersistentSortedMap<String> removeExisting(MapState state) {
        return state.map.remove(state.existingKey);
    }

    @Benchmark
    public int greaterOrEqualTo(MapState state, Blackhole blackhole) {
        int count = 0;
        Iterator<String> iterator = state.map.greaterOrEqualTo(state.existingKey);
        while (iterator.hasNext()) {
            blackhole.consume(iterator.next());
            count++;
        }
        return count;
    }

    @Benchmark
    public DoubleObjectPersistentSortedMap<String> mixedReadHeavy(MapState state, Blackhole blackhole) {
        DoubleObjectPersistentSortedMap<String> map = state.map;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            blackhole.consume(map.find(state.mixedExistingReadKeys[index]));
            blackhole.consume(map.find(state.mixedMissingReadKeys[index]));

            if ((index & 7) == 0) {
                Iterator<String> iterator = map.greaterOrEqualTo(state.mixedLowerBounds[index]);
                for (int consumed = 0; consumed < MIXED_ITERATION_SAMPLE && iterator.hasNext(); consumed++) {
                    blackhole.consume(iterator.next());
                }
            }

            if ((index & 15) == 0) {
                map = map.put(state.mixedWriteKeys[index], state.mixedWriteValues[index]);
            } else if ((index & 15) == 8) {
                map = map.remove(state.mixedRemoveKeys[index]);
            }
        }
        return map;
    }

    @Benchmark
    public DoubleObjectPersistentSortedMap<String> mixedUpdateHeavy(MapState state, Blackhole blackhole) {
        DoubleObjectPersistentSortedMap<String> map = state.map;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            map = map.put(state.mixedWriteKeys[index], state.mixedWriteValues[index]);
            blackhole.consume(map.find(state.mixedExistingReadKeys[index]));

            if ((index & 1) == 0) {
                map = map.remove(state.mixedRemoveKeys[index]);
            }

            if ((index & 3) == 0) {
                Iterator<String> iterator = map.greaterOrEqualTo(state.mixedLowerBounds[index]);
                if (iterator.hasNext()) {
                    blackhole.consume(iterator.next());
                }
            }
        }
        return map;
    }

    @State(Scope.Thread)
    public static class MapState {

        @Param({"simple", "treap", "compact", "dexx", "bifurcanSorted", "bifurcanFloat"})
        public String implementation;

        @Param({"ascending", "descending"})
        public String order;

        @Param({"128", "4096"})
        public int size;

        public DoubleObjectPersistentSortedMap<String> map;
        public double existingKey;
        public double missingKey;
        public String replacementValue;
        public double[] mixedExistingReadKeys;
        public double[] mixedMissingReadKeys;
        public double[] mixedWriteKeys;
        public double[] mixedRemoveKeys;
        public double[] mixedLowerBounds;
        public String[] mixedWriteValues;

        @Setup(Level.Trial)
        public void setUp() {
            map = BenchmarkFixtures.buildDoubleObjectPersistentSortedMap(
                    implementation,
                    order,
                    size,
                    index -> "value-" + index);
            existingKey = size / 2.0;
            missingKey = size + 1.0;
            replacementValue = "replacement";
            setUpMixedScenarioInputs();
        }

        private void setUpMixedScenarioInputs() {
            mixedExistingReadKeys = new double[MIXED_OPERATION_COUNT];
            mixedMissingReadKeys = new double[MIXED_OPERATION_COUNT];
            mixedWriteKeys = new double[MIXED_OPERATION_COUNT];
            mixedRemoveKeys = new double[MIXED_OPERATION_COUNT];
            mixedLowerBounds = new double[MIXED_OPERATION_COUNT];
            mixedWriteValues = new String[MIXED_OPERATION_COUNT];

            for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
                mixedExistingReadKeys[index] = (index * 17) % size;
                mixedMissingReadKeys[index] = size + 32.0 + index;
                mixedWriteKeys[index] = (index & 1) == 0
                        ? mixedExistingReadKeys[index]
                        : size + 1024.0 + index;
                mixedRemoveKeys[index] = (index * 31) % size;
                mixedLowerBounds[index] = mixedExistingReadKeys[index] - ((index & 3) == 0 ? 0.25 : 0.0);
                mixedWriteValues[index] = "mixed-" + index;
            }
        }

    }
}
