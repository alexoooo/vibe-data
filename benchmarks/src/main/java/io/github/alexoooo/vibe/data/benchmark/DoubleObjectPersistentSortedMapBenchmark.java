package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.SimpleDoubleObjectPersistentSortedMap;
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

    @State(Scope.Thread)
    public static class MapState {

        @Param({"simple", "dexx"})
        public String implementation;

        @Param({"ascending", "descending"})
        public String order;

        @Param({"128", "4096"})
        public int size;

        public DoubleObjectPersistentSortedMap<String> map;
        public double existingKey;
        public double missingKey;
        public String replacementValue;

        @Setup(Level.Trial)
        public void setUp() {
            map = createEmptyMap();
            for (int index = 0; index < size; index++) {
                map = map.put(index, "value-" + index);
            }

            existingKey = size / 2.0;
            missingKey = size + 1.0;
            replacementValue = "replacement";
        }

        private DoubleObjectPersistentSortedMap<String> createEmptyMap() {
            return switch (implementation) {
                case "simple" -> "descending".equals(order)
                        ? SimpleDoubleObjectPersistentSortedMap.<String>descending()
                        : SimpleDoubleObjectPersistentSortedMap.<String>ascending();
                case "dexx" -> "descending".equals(order)
                        ? DexxDoubleObjectPersistentSortedMap.<String>descending()
                        : DexxDoubleObjectPersistentSortedMap.<String>ascending();
                default -> throw new IllegalArgumentException("Unknown implementation: " + implementation);
            };
        }
    }
}
