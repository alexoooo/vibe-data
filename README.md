# vibe-data

`vibe-data` is a Java 25 multi-module Maven project for persistent data structures, including primitive-specialized map types and append-only generic sequence/vector types.

The main library artifact is `io.github.alexoooo:vibe-data`. The current slice adds persistent sorted maps with primitive `double` keys and object values, unordered persistent maps with primitive `long` keys and object values, and append-only generic sequences/vectors in package `io.github.alexoooo.vibe.data`, with JSpecify nullability annotations, JUnit 5 tests, and a separate JMH benchmark module.

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
- `SimpleDoubleObjectPersistentSortedMap<T>`
- `TreapDoubleObjectPersistentSortedMap<T>`
- `LongObjectMap<T>`
- `LongObjectPersistentMap<T>`
- `SimpleLongObjectPersistentMap<T>`
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

Or run `io.github.alexoooo.vibe.data.benchmark.DoubleObjectPersistentSortedMapBenchmark.main()`, `io.github.alexoooo.vibe.data.benchmark.LongObjectPersistentMapBenchmark.main()`, `io.github.alexoooo.vibe.data.benchmark.PersistentAppendSequenceBenchmark.main()`, or `io.github.alexoooo.vibe.data.benchmark.PersistentVectorBenchmark.main()` directly from an IDE to launch those benchmark classes without building a custom JMH command line.

The benchmark suite includes both single-operation microbenchmarks and mixed read/write workloads across the library implementations plus benchmark-only comparison wrappers.

Saved benchmark result snapshots live under `benchmarks/src/main/resources/results/` with timestamped filenames.
