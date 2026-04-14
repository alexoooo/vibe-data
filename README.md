# vibe-data

`vibe-data` is a Java 25 multi-module Maven project for primitive-specialized persistent data structures.

The main library artifact is `io.github.alexoooo:vibe-data`. This initial slice adds persistent sorted maps with primitive `double` keys and object values in package `io.github.alexoooo.vibe.data`, with JSpecify nullability annotations, JUnit 5 tests, and a separate JMH benchmark module.

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
| `vibe-data` | Published library artifact containing the persistent map API and the simple `TreeMap`-copy implementation |
| `benchmarks` | JMH benchmarks plus a benchmark-only Dexx `TreeMap` comparison implementation |

## Current API

- `DoubleObjectSortedMap<T>`
- `DoubleObjectPersistentSortedMap<T>`
- `SimpleDoubleObjectPersistentSortedMap<T>`

## Example

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

`put` and `remove` return new map instances, so older versions remain unchanged.

## Benchmarks

Package the benchmark module:

```bash
./mvnw -pl benchmarks -am package
```

On Windows:

```powershell
.\mvnw.cmd -pl benchmarks -am package
```

Then run the shaded JMH jar:

```bash
java -jar benchmarks/target/benchmarks.jar
```
