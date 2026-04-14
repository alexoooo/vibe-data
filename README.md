# vibe-data

`vibe-data` is a Java 25 Maven library for primitive-specialized persistent data structures.

This initial slice adds persistent sorted maps with primitive `double` keys and object values in package `io.github.alexoooo.vibe.data`, with JSpecify nullability annotations and JUnit 5 tests.

## Build

```bash
./mvnw test
```

On Windows, use:

```powershell
.\mvnw.cmd test
```

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

List<String> values = new ArrayList<>();
map.greaterOrEqualTo(2.0).forEachRemaining(values::add);
// values == ["three", "two"]
```

`put` and `remove` return new map instances, so older versions remain unchanged.
