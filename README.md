# vibe-data

`vibe-data` is a Java 25 multi-module Maven project for persistent data structures, including primitive-specialized map types, ordered-set-like queues, and append-only generic sequence/vector types.

The main library artifact is `io.github.alexoooo:vibe-data`. It currently provides persistent sorted maps with primitive `double` keys and object values, unordered persistent maps with primitive `long` keys and object values, ordered queues with comparator-aware factories, and append-only generic sequences/vectors in package `io.github.alexoooo.vibe.data`, with JSpecify nullability annotations, JUnit 5 tests, and a separate JMH benchmark module.

## Build

This project targets Java 25, so ensure `JAVA_HOME` points to a JDK 25 installation before running Maven.

```bash
./mvnw test
```

On Windows, use:

```powershell
.\mvnw.cmd test
```

## Modules

| Module | Purpose |
| --- | --- |
| `vibe-data` | Published library artifact containing the persistent map and sequence/vector APIs plus simple copy-on-write and custom optimized implementations |
| `benchmarks` | JMH benchmarks plus benchmark-only Dexx and Bifurcan comparison implementations |

## Current API

- `DoubleObjectSortedMap<T>`
- `DoubleObjectPersistentSortedMap<T>`
- `CompactDoubleObjectPersistentSortedMap<T>`
- `SimpleDoubleObjectPersistentSortedMap<T>`
- `TreapDoubleObjectPersistentSortedMap<T>`
- `LongObjectMap<T>`
- `LongObjectPersistentMap<T>`
- `CompactLongObjectPersistentMap<T>`
- `SimpleLongObjectPersistentMap<T>`
- `HamtLongObjectPersistentMap<T>`
- `OrderedQueue<T>`
- `PersistentOrderedQueue<T>`
- `CompactPersistentOrderedQueue<T>`
- `SimplePersistentOrderedQueue<T>`
- `TreapPersistentOrderedQueue<T>`
- `PersistentAppendSequence<T>`
- `PersistentVector<T>`
- `SimplePersistentAppendSequence<T>`
- `ChunkedPersistentAppendSequence<T>`
- `SimplePersistentVector<T>`
- `ChunkedPersistentVector<T>`

## Examples

### Sorted `double` keys

```java
import io.github.alexoooo.vibe.data.SimpleDoubleObjectPersistentSortedMap;

import java.util.ArrayList;
import java.util.List;

var map = SimpleDoubleObjectPersistentSortedMap.<String>descending()
        .put(1.0, "one")
        .put(3.0, "three")
        .put(2.0, "two");

String exact = map.find(2.0);
int size = map.size();

List<String> values = new ArrayList<>();
map.greaterOrEqualTo(2.0).forEachRemaining(values::add);
// values == ["three", "two"]
```

### Unordered `long` keys

```java
import io.github.alexoooo.vibe.data.SimpleLongObjectPersistentMap;

import java.util.ArrayList;
import java.util.List;

var map = SimpleLongObjectPersistentMap.<String>empty()
        .put(10L, "ten")
        .put(20L, "twenty")
        .put(30L, "thirty");

String exact = map.find(20L);
int size = map.size();

List<String> values = new ArrayList<>();
for (String value : map) {
    values.add(value);
}
// values contains "ten", "twenty", and "thirty" in unspecified order
```

`put` and `remove` return new map instances, so older versions remain unchanged.

### Ordered queues

```java
import io.github.alexoooo.vibe.data.TreapPersistentOrderedQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

var queue = TreapPersistentOrderedQueue.<String>empty(Comparator.comparingInt(String::length))
        .add("alpha")
        .add("b")
        .add("cccc")
        .replace("alpha", "zz");

String first = queue.first();
String last = queue.last();
int size = queue.size();

List<String> values = new ArrayList<>();
queue.forEach(values::add);
// values == ["b", "zz", "cccc"]
```

`add` and `replace` are comparator-aware and use last-write-wins semantics for comparator-equivalent values.

### Append-only sequences

```java
import io.github.alexoooo.vibe.data.ChunkedPersistentAppendSequence;

import java.util.ArrayList;
import java.util.List;

var sequence = ChunkedPersistentAppendSequence.<String>empty()
        .append("alpha")
        .append("beta")
        .append("gamma");

String first = sequence.first();
String last = sequence.last();
int size = sequence.size();

List<String> values = new ArrayList<>();
sequence.forEach(values::add);
// values == ["alpha", "beta", "gamma"]
```

### Append-only vectors

```java
import io.github.alexoooo.vibe.data.ChunkedPersistentVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

var vector = ChunkedPersistentVector.<String>empty()
        .append("alpha")
        .append("beta")
        .append("gamma");

String first = vector.first();
String last = vector.last();
String indexed = vector.get(1);
int size = vector.size();

List<String> reversed = new ArrayList<>();
Iterator<String> reverseIterator = vector.reverseIterator();
reverseIterator.forEachRemaining(reversed::add);
// reversed == ["gamma", "beta", "alpha"]
```

## Benchmarks

Package the benchmark module:

```bash
./mvnw -pl benchmarks -am package
```

On Windows:

```powershell
.\mvnw.cmd -pl benchmarks -am package
```

Then run the packaged JMH jar from the target directory, keeping the generated `lib/` folder beside it:

```bash
java -jar benchmarks/target/benchmarks.jar
```

Or run `io.github.alexoooo.vibe.data.benchmark.DoubleObjectPersistentSortedMapBenchmark.main()`, `io.github.alexoooo.vibe.data.benchmark.LongObjectPersistentMapBenchmark.main()`, `io.github.alexoooo.vibe.data.benchmark.PersistentOrderedQueueBenchmark.main()`, `io.github.alexoooo.vibe.data.benchmark.PersistentAppendSequenceBenchmark.main()`, or `io.github.alexoooo.vibe.data.benchmark.PersistentVectorBenchmark.main()` directly from an IDE to launch those benchmark classes without building a custom JMH command line.

The benchmark suite includes both single-operation microbenchmarks and mixed read/write workloads across the library implementations plus benchmark-only comparison wrappers.

Saved benchmark result snapshots live under `benchmarks/src/main/resources/results/` with timestamped filenames.

### Performance benchmark summary

Generate fresh JMH CSV snapshots for all five public benchmark families, then build the checked-in Markdown summary:

```powershell
$timestamp = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH-mm-ssZ')
$resultsDir = Join-Path $PWD 'benchmarks\src\main\resources\results'
$cpFile = Join-Path $env:TEMP 'vibe-data-benchmarks-classpath.txt'

.\mvnw.cmd -q -pl benchmarks -am package dependency:build-classpath "-Dmdep.outputFile=$cpFile" -Dmdep.pathSeparator=';' -Dmdep.includeScope=runtime

Push-Location benchmarks\target
java -jar benchmarks.jar io.github.alexoooo.vibe.data.benchmark.DoubleObjectPersistentSortedMapBenchmark.* -foe true -rf csv -rff (Join-Path $resultsDir "$timestamp-double-object-persistent-sorted-map.csv")
java -jar benchmarks.jar io.github.alexoooo.vibe.data.benchmark.LongObjectPersistentMapBenchmark.* -foe true -rf csv -rff (Join-Path $resultsDir "$timestamp-long-object-persistent-map.csv")
java -jar benchmarks.jar io.github.alexoooo.vibe.data.benchmark.PersistentOrderedQueueBenchmark.* -foe true -rf csv -rff (Join-Path $resultsDir "$timestamp-persistent-ordered-queue.csv")
java -jar benchmarks.jar io.github.alexoooo.vibe.data.benchmark.PersistentAppendSequenceBenchmark.* -foe true -rf csv -rff (Join-Path $resultsDir "$timestamp-persistent-append-sequence.csv")
java -jar benchmarks.jar io.github.alexoooo.vibe.data.benchmark.PersistentVectorBenchmark.* -foe true -rf csv -rff (Join-Path $resultsDir "$timestamp-persistent-vector.csv")
Pop-Location

$cp = "benchmarks\target\classes;vibe-data\target\classes;" + (Get-Content $cpFile -Raw).Trim()
java -cp $cp io.github.alexoooo.vibe.data.benchmark.PerformanceBenchmarkReport `
    (Join-Path $resultsDir "$timestamp-double-object-persistent-sorted-map.csv") `
    (Join-Path $resultsDir "$timestamp-long-object-persistent-map.csv") `
    (Join-Path $resultsDir "$timestamp-persistent-ordered-queue.csv") `
    (Join-Path $resultsDir "$timestamp-persistent-append-sequence.csv") `
    (Join-Path $resultsDir "$timestamp-persistent-vector.csv")
```

The report writes a normalized timestamped summary CSV and refreshes `benchmarks/src/main/resources/results/performance-benchmark-summary.md`.

### Memory usage report

Generate the retained-heap comparison report:

```powershell
$cpFile = Join-Path $env:TEMP 'vibe-data-benchmarks-classpath.txt'
.\mvnw.cmd -q -pl benchmarks -am compile dependency:build-classpath "-Dmdep.outputFile=$cpFile" -Dmdep.pathSeparator=';' -Dmdep.includeScope=runtime
$cp = "benchmarks\target\classes;vibe-data\target\classes;" + (Get-Content $cpFile -Raw).Trim()
java --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED -cp $cp io.github.alexoooo.vibe.data.benchmark.MemoryUsageReport
```

Or run `io.github.alexoooo.vibe.data.benchmark.MemoryUsageReport.main()` directly from an IDE with the same `--add-opens` JVM options.

The command writes a timestamped CSV snapshot and refreshes the checked-in Markdown summary at `benchmarks/src/main/resources/results/memory-usage-summary.md`. The report captures retained bytes for the full mutation history (empty version through the final version), a structural-bytes view with the shared payload cost removed, and the JVM layout details used for the run.
