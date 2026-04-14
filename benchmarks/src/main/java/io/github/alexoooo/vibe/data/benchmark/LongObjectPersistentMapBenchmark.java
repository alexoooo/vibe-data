package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.HamtLongObjectPersistentMap;
import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import io.github.alexoooo.vibe.data.SimpleLongObjectPersistentMap;
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
public class LongObjectPersistentMapBenchmark {

    private static final int MIXED_OPERATION_COUNT = 64;

    public static void main(String[] args) throws Exception {
        String[] benchmarkArgs = args.length == 0
                ? new String[]{LongObjectPersistentMapBenchmark.class.getName() + ".*"}
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
    public LongObjectPersistentMap<String> putExisting(MapState state) {
        return state.map.put(state.existingKey, state.replacementValue);
    }

    @Benchmark
    public LongObjectPersistentMap<String> putNew(MapState state) {
        return state.map.put(state.missingKey, state.replacementValue);
    }

    @Benchmark
    public LongObjectPersistentMap<String> removeExisting(MapState state) {
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
    public LongObjectPersistentMap<String> mixedReadHeavy(MapState state, Blackhole blackhole) {
        LongObjectPersistentMap<String> map = state.map;
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
    public LongObjectPersistentMap<String> mixedUpdateHeavy(MapState state, Blackhole blackhole) {
        LongObjectPersistentMap<String> map = state.map;
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

        @Param({"simple", "hamt", "dexx", "bifurcan"})
        public String implementation;

        @Param({"128", "4096"})
        public int size;

        public LongObjectPersistentMap<String> map;
        public long existingKey;
        public long missingKey;
        public String replacementValue;
        public long[] mixedExistingReadKeys;
        public long[] mixedMissingReadKeys;
        public long[] mixedWriteKeys;
        public long[] mixedRemoveKeys;
        public String[] mixedWriteValues;

        @Setup(Level.Trial)
        public void setUp() {
            map = createEmptyMap();
            for (int index = 0; index < size; index++) {
                map = map.put(index, "value-" + index);
            }

            existingKey = size / 2L;
            missingKey = size + 1L;
            replacementValue = "replacement";
            setUpMixedScenarioInputs();
        }

        private void setUpMixedScenarioInputs() {
            mixedExistingReadKeys = new long[MIXED_OPERATION_COUNT];
            mixedMissingReadKeys = new long[MIXED_OPERATION_COUNT];
            mixedWriteKeys = new long[MIXED_OPERATION_COUNT];
            mixedRemoveKeys = new long[MIXED_OPERATION_COUNT];
            mixedWriteValues = new String[MIXED_OPERATION_COUNT];

            for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
                mixedExistingReadKeys[index] = (index * 17L) % size;
                mixedMissingReadKeys[index] = size + 32L + index;
                mixedWriteKeys[index] = (index & 1) == 0
                        ? mixedExistingReadKeys[index]
                        : size + 1024L + index;
                mixedRemoveKeys[index] = (index * 31L) % size;
                mixedWriteValues[index] = "mixed-" + index;
            }
        }

        private LongObjectPersistentMap<String> createEmptyMap() {
            return switch (implementation) {
                case "simple" -> SimpleLongObjectPersistentMap.<String>empty();
                case "hamt" -> HamtLongObjectPersistentMap.<String>empty();
                case "dexx" -> DexxLongObjectPersistentMap.<String>empty();
                case "bifurcan" -> BifurcanIntMapLongObjectPersistentMap.<String>empty();
                default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
            };
        }
    }
}
