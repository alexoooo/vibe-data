package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.PersistentAppendSequence;
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
public class PersistentAppendSequenceBenchmark {

    private static final int ITERATION_SAMPLE_COUNT = 8;
    private static final int MIXED_OPERATION_COUNT = 64;

    public static void main(String[] args) throws Exception {
        String[] benchmarkArgs = args.length == 0
                ? new String[]{PersistentAppendSequenceBenchmark.class.getName() + ".*"}
                : args;
        Main.main(benchmarkArgs);
    }

    @Benchmark
    public PersistentAppendSequence<String> append(AppendSequenceState state) {
        return state.sequence.append(state.appendValue);
    }

    @Benchmark
    public String first(AppendSequenceState state) {
        return state.sequence.first();
    }

    @Benchmark
    public int iterateAll(AppendSequenceState state, Blackhole blackhole) {
        int count = 0;
        Iterator<String> iterator = state.sequence.iterator();
        while (iterator.hasNext()) {
            blackhole.consume(iterator.next());
            count++;
        }
        return count;
    }

    @Benchmark
    public String last(AppendSequenceState state) {
        return state.sequence.last();
    }

    @Benchmark
    public PersistentAppendSequence<String> mixedAppendHeavy(AppendSequenceState state, Blackhole blackhole) {
        PersistentAppendSequence<String> sequence = state.sequence;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            sequence = sequence.append(state.mixedAppendValues[index]);
            blackhole.consume(sequence.first());
            blackhole.consume(sequence.last());

            if ((index & 7) == 0) {
                Iterator<String> iterator = sequence.iterator();
                for (int sample = 0; sample < ITERATION_SAMPLE_COUNT && iterator.hasNext(); sample++) {
                    blackhole.consume(iterator.next());
                }
            }
        }
        return sequence;
    }

    @Benchmark
    public PersistentAppendSequence<String> mixedReadHeavy(AppendSequenceState state, Blackhole blackhole) {
        PersistentAppendSequence<String> sequence = state.sequence;
        for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
            blackhole.consume(sequence.first());
            blackhole.consume(sequence.last());

            Iterator<String> iterator = sequence.iterator();
            for (int sample = 0; sample < ITERATION_SAMPLE_COUNT && iterator.hasNext(); sample++) {
                blackhole.consume(iterator.next());
            }

            if ((index & 15) == 0) {
                sequence = sequence.append(state.mixedAppendValues[index]);
            }
        }
        return sequence;
    }

    @Benchmark
    public int size(AppendSequenceState state) {
        return state.sequence.size();
    }

    @State(Scope.Thread)
    public static class AppendSequenceState {

        @Param({"simple", "chunkedVector", "chunkedAppend", "bifurcan", "dexx"})
        public String implementation;

        @Param({"128", "4096"})
        public int size;

        public PersistentAppendSequence<String> sequence;
        public String appendValue;
        public String[] mixedAppendValues;

        @Setup(Level.Trial)
        public void setUp() {
            sequence = BenchmarkFixtures.buildPersistentAppendSequence(
                    implementation,
                    size,
                    index -> "value-" + index);
            appendValue = "append-value";
            mixedAppendValues = new String[MIXED_OPERATION_COUNT];
            for (int index = 0; index < MIXED_OPERATION_COUNT; index++) {
                mixedAppendValues[index] = "mixed-append-" + index;
            }
        }
    }
}
