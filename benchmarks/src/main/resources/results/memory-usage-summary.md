# Memory usage summary

- Generated: `2026-04-19T18:42:08.476092Z`
- Source CSV: [`2026-04-19T18-42-08Z-memory-usage.csv`](./2026-04-19T18-42-08Z-memory-usage.csv)
- Java: `25.0.1` (`OpenJDK 64-Bit Server VM`, `Eclipse Adoptium`)
- OS: `Windows 11 10.0`
- Measured sizes: `0`, `128`, `4096`
- Payload model: one small comparable payload object per logical element; `structural bytes` subtract that payload cost from the retained graph to isolate collection overhead more directly.
- Payload bytes per element: `16`

## Methodology

- Retained footprint is measured from the fully built collection instance with JOL `GraphLayout`.
- Sorted maps are measured in both `ascending` and `descending` order configurations.
- `Retained bytes` is the full reachable graph size; `structural bytes` subtracts the per-element payload objects while keeping collection nodes, arrays, and references.

## JVM layout details

```text
# VM mode: 64 bits
# Compressed references (oops): 3-bit shift
# Compressed class pointers: 3-bit shift
# WARNING | Compressed references base/shifts are guessed by the experiment!
# WARNING | Therefore, computed addresses are just guesses, and ARE NOT RELIABLE.
# WARNING | Make sure to attach Serviceability Agent to get the reliable addresses.
# Object alignment: 8 bytes
#                       ref, bool, byte, char, shrt,  int,  flt,  lng,  dbl
# Field sizes:            4,    1,    1,    2,    2,    4,    4,    8,    8
# Array element sizes:    4,    1,    1,    2,    2,    4,    4,    8,    8
# Array base offsets:    16,   16,   16,   16,   16,   16,   16,   16,   16
```

## DoubleObjectPersistentSortedMap

### ascending

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | treap | 24 | 24 | - | - | 1 |
| 2 | simple | 72 | 72 | - | - | 2 |
| 3 | dexx | 104 | 104 | - | - | 5 |
| 4 | bifurcanSorted | 240 | 240 | - | - | 9 |
| 5 | bifurcanFloat | 344 | 344 | - | - | 11 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | treap | 8216 | 6168 | 64.188 | 48.188 | 257 |
| 2 | bifurcanFloat | 8400 | 6352 | 65.625 | 49.625 | 291 |
| 3 | dexx | 9320 | 7272 | 72.813 | 56.813 | 389 |
| 4 | simple | 10312 | 8264 | 80.563 | 64.563 | 386 |
| 5 | bifurcanSorted | 10552 | 8504 | 82.438 | 66.438 | 396 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | bifurcanFloat | 217688 | 152152 | 53.146 | 37.146 | 7621 |
| 2 | treap | 262168 | 196632 | 64.006 | 48.006 | 8193 |
| 3 | dexx | 295016 | 229480 | 72.025 | 56.025 | 12293 |
| 4 | simple | 327752 | 262216 | 80.018 | 64.018 | 12290 |
| 5 | bifurcanSorted | 327992 | 262456 | 80.076 | 64.076 | 12300 |

### descending

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | treap | 24 | 24 | - | - | 1 |
| 2 | simple | 72 | 72 | - | - | 2 |
| 3 | dexx | 104 | 104 | - | - | 5 |
| 4 | bifurcanSorted | 240 | 240 | - | - | 9 |
| 5 | bifurcanFloat | 344 | 344 | - | - | 11 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | treap | 8216 | 6168 | 64.188 | 48.188 | 257 |
| 2 | bifurcanFloat | 8400 | 6352 | 65.625 | 49.625 | 291 |
| 3 | dexx | 9320 | 7272 | 72.813 | 56.813 | 389 |
| 4 | simple | 10312 | 8264 | 80.563 | 64.563 | 386 |
| 5 | bifurcanSorted | 10552 | 8504 | 82.438 | 66.438 | 396 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | bifurcanFloat | 217688 | 152152 | 53.146 | 37.146 | 7621 |
| 2 | treap | 262168 | 196632 | 64.006 | 48.006 | 8193 |
| 3 | dexx | 295016 | 229480 | 72.025 | 56.025 | 12293 |
| 4 | simple | 327752 | 262216 | 80.018 | 64.018 | 12290 |
| 5 | bifurcanSorted | 327992 | 262456 | 80.076 | 64.076 | 12300 |

## LongObjectPersistentMap

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | hamt | 24 | 24 | - | - | 1 |
| 2 | dexx | 48 | 48 | - | - | 3 |
| 3 | simple | 64 | 64 | - | - | 2 |
| 4 | bifurcanMap | 192 | 192 | - | - | 8 |
| 5 | bifurcan | 304 | 304 | - | - | 10 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | bifurcan | 4776 | 2728 | 37.313 | 21.313 | 169 |
| 2 | hamt | 8360 | 6312 | 65.313 | 49.313 | 331 |
| 3 | bifurcanMap | 10008 | 7960 | 78.188 | 62.188 | 391 |
| 4 | simple | 10320 | 8272 | 80.625 | 64.625 | 387 |
| 5 | dexx | 10536 | 8488 | 82.313 | 66.313 | 466 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | bifurcan | 146264 | 80728 | 35.709 | 19.709 | 5178 |
| 2 | hamt | 265816 | 200280 | 64.896 | 48.896 | 10543 |
| 3 | bifurcanMap | 318232 | 252696 | 77.693 | 61.693 | 12391 |
| 4 | simple | 327760 | 262224 | 80.020 | 64.020 | 12291 |
| 5 | dexx | 334280 | 268744 | 81.611 | 65.611 | 14740 |

## PersistentAppendSequence

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunkedVector | 48 | 48 | - | - | 2 |
| 2 | chunkedAppend | 56 | 56 | - | - | 2 |
| 3 | simple | 72 | 72 | - | - | 4 |
| 4 | dexx | 88 | 88 | - | - | 3 |
| 5 | bifurcan | 160 | 160 | - | - | 6 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | simple | 2632 | 584 | 20.563 | 4.563 | 132 |
| 2 | chunkedVector | 2688 | 640 | 21.000 | 5.000 | 134 |
| 3 | chunkedAppend | 2696 | 648 | 21.063 | 5.063 | 134 |
| 4 | bifurcan | 2808 | 760 | 21.938 | 5.938 | 138 |
| 5 | dexx | 2856 | 808 | 22.313 | 6.313 | 136 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | simple | 81992 | 16456 | 20.018 | 4.018 | 4100 |
| 2 | chunkedVector | 84608 | 19072 | 20.656 | 4.656 | 4230 |
| 3 | chunkedAppend | 84616 | 19080 | 20.658 | 4.658 | 4230 |
| 4 | dexx | 84776 | 19240 | 20.697 | 4.697 | 4232 |
| 5 | bifurcan | 85992 | 20456 | 20.994 | 4.994 | 4245 |

## PersistentOrderedQueue

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | simple | 80 | 80 | - | - | 3 |
| 2 | treap | 104 | 104 | - | - | 4 |
| 3 | dexx | 112 | 112 | - | - | 6 |
| 4 | bifurcan | 256 | 256 | - | - | 10 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | dexx | 5232 | 3184 | 40.875 | 24.875 | 262 |
| 2 | simple | 7264 | 5216 | 56.750 | 40.750 | 260 |
| 3 | treap | 7272 | 5224 | 56.813 | 40.813 | 260 |
| 4 | bifurcan | 7496 | 5448 | 58.563 | 42.563 | 269 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | dexx | 163952 | 98416 | 40.027 | 24.027 | 8198 |
| 2 | simple | 229472 | 163936 | 56.023 | 40.023 | 8196 |
| 3 | treap | 229480 | 163944 | 56.025 | 40.025 | 8196 |
| 4 | bifurcan | 229704 | 164168 | 56.080 | 40.080 | 8205 |

## PersistentVector

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunked | 48 | 48 | - | - | 2 |
| 2 | simple | 56 | 56 | - | - | 3 |
| 3 | dexx | 88 | 88 | - | - | 3 |
| 4 | bifurcan | 160 | 160 | - | - | 6 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | simple | 2616 | 568 | 20.438 | 4.438 | 131 |
| 2 | chunked | 2688 | 640 | 21.000 | 5.000 | 134 |
| 3 | bifurcan | 2808 | 760 | 21.938 | 5.938 | 138 |
| 4 | dexx | 2856 | 808 | 22.313 | 6.313 | 136 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | simple | 81976 | 16440 | 20.014 | 4.014 | 4099 |
| 2 | chunked | 84608 | 19072 | 20.656 | 4.656 | 4230 |
| 3 | dexx | 84776 | 19240 | 20.697 | 4.697 | 4232 |
| 4 | bifurcan | 85992 | 20456 | 20.994 | 4.994 | 4245 |
