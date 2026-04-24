package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.IntObjectPersistentMap;
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
public class IntObjectPersistentMapBenchmark {

    private static final int MIXED_OPERATION_COUNT = 64;

    public static void main(String[] args) throws Exception {
        String[] benchmarkArgs = args.length == 0
                ? new String[] {IntObjectPersistentMapBenchmark.class.getName() + ".*"}
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
    public IntObjectPersistentMap<String> putExisting(MapState state) {
        return state.map.put(state.existingKey, state.replacementValue);
    }

    @Benchmark
    public IntObjectPersistentMap<String> putNew(MapState state) {
        return state.map.put(state.missingKey, state.replacementValue);
    }

    @Benchmark
    public IntObjectPersistentMap<String> removeExisting(MapState state) {
        return state.map.remove(state.existingKey);
    }

    @Benchmark
    public int iterateAll(MapState state, Blackhole blackhole) {
        int count = 0;
        Iterator<String> iterator = state.map.iterator();
        while (iterator.hasNext()) {
            blackhole.consume(iterator.next());
            count++;
        }
        return count;
    }

    @Benchmark
    public IntObjectPersistentMap<String> mixedReadHeavy(MapState state, Blackhole blackhole) {
        IntObjectPersistentMap<String> map = state.map;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            blackhole.consume(map.find(state.mixedExistingReadKeys[index]));
            blackhole.consume(map.find(state.mixedMissingReadKeys[index]));

            if ((index & 15) == 0) {
                map = map.put(state.mixedWriteKeys[index], state.mixedWriteValues[index]);
            } else if ((index & 15) == 8) {
                map = map.remove(state.mixedRemoveKeys[index]);
            }
        }
        return map;
    }

    @Benchmark
    public IntObjectPersistentMap<String> mixedUpdateHeavy(MapState state, Blackhole blackhole) {
        IntObjectPersistentMap<String> map = state.map;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            map = map.put(state.mixedWriteKeys[index], state.mixedWriteValues[index]);
            blackhole.consume(map.find(state.mixedExistingReadKeys[index]));

            if ((index & 1) == 0) {
                map = map.remove(state.mixedRemoveKeys[index]);
            } else {
                blackhole.consume(map.find(state.mixedMissingReadKeys[index]));
            }
        }
        return map;
    }

    @State(Scope.Thread)
    public static class MapState {

        @Param({"simple", "hamt", "dexx", "bifurcan", "bifurcanMap"})
        public String implementation;

        @Param({"128", "4096"})
        public int size;

        public IntObjectPersistentMap<String> map;
        public int existingKey;
        public int missingKey;
        public String replacementValue;
        public int[] mixedExistingReadKeys;
        public int[] mixedMissingReadKeys;
        public int[] mixedWriteKeys;
        public int[] mixedRemoveKeys;
        public String[] mixedWriteValues;

        @Setup(Level.Trial)
        public void setUp() {
            map = BenchmarkFixtures.buildIntObjectPersistentMap(
                    implementation,
                    size,
                    index -> "value-" + index);
            existingKey = size / 2;
            missingKey = size + 1;
            replacementValue = "replacement";
            setUpMixedScenarioInputs();
        }

        private void setUpMixedScenarioInputs() {
            mixedExistingReadKeys = new int[MIXED_OPERATION_COUNT];
            mixedMissingReadKeys = new int[MIXED_OPERATION_COUNT];
            mixedWriteKeys = new int[MIXED_OPERATION_COUNT];
            mixedRemoveKeys = new int[MIXED_OPERATION_COUNT];
            mixedWriteValues = new String[MIXED_OPERATION_COUNT];

            for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
                mixedExistingReadKeys[index] = (index * 17) % size;
                mixedMissingReadKeys[index] = size + 32 + index;
                mixedWriteKeys[index] = (index & 1) == 0
                        ? mixedExistingReadKeys[index]
                        : size + 1024 + index;
                mixedRemoveKeys[index] = (index * 31) % size;
                mixedWriteValues[index] = "mixed-" + index;
            }
        }
    }
}
