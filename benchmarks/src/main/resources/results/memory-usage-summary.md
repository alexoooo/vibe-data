# Memory usage summary

- Generated: `2026-04-19T23:13:40.0295145Z`
- Source CSV: [`2026-04-19T23-13-40Z-memory-usage.csv`](./2026-04-19T23-13-40Z-memory-usage.csv)
- Java: `25.0.1` (`OpenJDK 64-Bit Server VM`, `Eclipse Adoptium`)
- OS: `Windows 11 10.0`
- Measured sizes: `0`, `128`, `4096`
- Retention model: all versions from the build sequence are retained (`empty`, then every mutation through the final size)
- Payload model: one small comparable payload object per logical element; `structural bytes` subtract that payload cost from the retained graph to isolate collection overhead more directly.
- Payload bytes per element: `16`

## Methodology

- Retained footprint is measured from a retained-history container holding the empty version and every subsequent mutation snapshot with JOL `GraphLayout`.
- Sorted maps are measured in both `ascending` and `descending` order configurations.
- `Retained bytes` is the full reachable graph size of the retained history; `structural bytes` subtract the unique per-element payload objects while keeping collection nodes, arrays, roots, and references.

## JVM layout details

```text
# VM mode: 64 bits
# Compressed references (oops): 3-bit shift
# Compressed class pointers: 0-bit shift and 0x27000000 base
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
| 1 | compact | 64 | 64 | - | - | 3 |
| 2 | treap | 64 | 64 | - | - | 3 |
| 3 | simple | 112 | 112 | - | - | 4 |
| 4 | dexx | 144 | 144 | - | - | 7 |
| 5 | bifurcanSorted | 280 | 280 | - | - | 11 |
| 6 | bifurcanFloat | 384 | 384 | - | - | 13 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 36656 | 34608 | 286.375 | 270.375 | 904 |
| 2 | treap | 36656 | 34608 | 286.375 | 270.375 | 904 |
| 3 | dexx | 47056 | 45008 | 367.625 | 351.625 | 1617 |
| 4 | bifurcanSorted | 57072 | 55024 | 445.875 | 429.875 | 1624 |
| 5 | bifurcanFloat | 88424 | 86376 | 690.813 | 674.813 | 1958 |
| 6 | simple | 347248 | 345200 | 2712.875 | 2696.875 | 8900 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 1947168 | 1881632 | 475.383 | 459.383 | 45005 |
| 2 | treap | 1947168 | 1881632 | 475.383 | 459.383 | 45005 |
| 3 | dexx | 2474608 | 2409072 | 604.152 | 588.152 | 81942 |
| 4 | bifurcanSorted | 3031992 | 2966456 | 740.232 | 724.232 | 81949 |
| 5 | bifurcanFloat | 4273792 | 4208256 | 1043.406 | 1027.406 | 76509 |
| 6 | simple | 336167024 | 336101488 | 82072.027 | 82056.027 | 8411140 |

### descending

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 64 | 64 | - | - | 3 |
| 2 | treap | 64 | 64 | - | - | 3 |
| 3 | simple | 112 | 112 | - | - | 4 |
| 4 | dexx | 144 | 144 | - | - | 7 |
| 5 | bifurcanSorted | 280 | 280 | - | - | 11 |
| 6 | bifurcanFloat | 384 | 384 | - | - | 13 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 36656 | 34608 | 286.375 | 270.375 | 904 |
| 2 | treap | 36656 | 34608 | 286.375 | 270.375 | 904 |
| 3 | dexx | 47056 | 45008 | 367.625 | 351.625 | 1617 |
| 4 | bifurcanSorted | 57072 | 55024 | 445.875 | 429.875 | 1624 |
| 5 | bifurcanFloat | 88424 | 86376 | 690.813 | 674.813 | 1958 |
| 6 | simple | 347248 | 345200 | 2712.875 | 2696.875 | 8900 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 1947168 | 1881632 | 475.383 | 459.383 | 45005 |
| 2 | treap | 1947168 | 1881632 | 475.383 | 459.383 | 45005 |
| 3 | dexx | 2474608 | 2409072 | 604.152 | 588.152 | 81942 |
| 4 | bifurcanSorted | 3031992 | 2966456 | 740.232 | 724.232 | 81949 |
| 5 | bifurcanFloat | 4273792 | 4208256 | 1043.406 | 1027.406 | 76509 |
| 6 | simple | 336167024 | 336101488 | 82072.027 | 82056.027 | 8411140 |

## LongObjectPersistentMap

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 64 | 64 | - | - | 3 |
| 2 | hamt | 64 | 64 | - | - | 3 |
| 3 | dexx | 88 | 88 | - | - | 5 |
| 4 | simple | 104 | 104 | - | - | 4 |
| 5 | bifurcanMap | 232 | 232 | - | - | 10 |
| 6 | bifurcan | 344 | 344 | - | - | 12 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | dexx | 36752 | 34704 | 287.125 | 271.125 | 1106 |
| 2 | hamt | 36840 | 34792 | 287.813 | 271.813 | 789 |
| 3 | compact | 39104 | 37056 | 305.500 | 289.500 | 922 |
| 4 | bifurcan | 56288 | 54240 | 439.750 | 423.750 | 1226 |
| 5 | bifurcanMap | 79096 | 77048 | 617.938 | 601.938 | 1322 |
| 6 | simple | 347800 | 345752 | 2717.188 | 2701.188 | 9027 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | dexx | 1852952 | 1787416 | 452.381 | 436.381 | 43482 |
| 2 | compact | 1947064 | 1881528 | 475.357 | 459.357 | 37580 |
| 3 | hamt | 2153320 | 2087784 | 525.713 | 509.713 | 31555 |
| 4 | bifurcan | 2929760 | 2864224 | 715.273 | 699.273 | 51010 |
| 5 | bifurcanMap | 4384504 | 4318968 | 1070.436 | 1054.436 | 54186 |
| 6 | simple | 336183448 | 336117912 | 82076.037 | 82060.037 | 8415235 |

## PersistentAppendSequence

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunkedVector | 88 | 88 | - | - | 4 |
| 2 | chunkedAppend | 96 | 96 | - | - | 4 |
| 3 | simple | 112 | 112 | - | - | 6 |
| 4 | dexx | 128 | 128 | - | - | 5 |
| 5 | bifurcan | 200 | 200 | - | - | 8 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunkedVector | 17576 | 15528 | 137.313 | 121.313 | 391 |
| 2 | chunkedAppend | 18608 | 16560 | 145.375 | 129.375 | 391 |
| 3 | bifurcan | 23384 | 21336 | 182.688 | 166.688 | 536 |
| 4 | dexx | 32816 | 30768 | 256.375 | 240.375 | 648 |
| 5 | simple | 45136 | 43088 | 352.625 | 336.625 | 645 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunkedVector | 570536 | 505000 | 139.291 | 123.291 | 12514 |
| 2 | chunkedAppend | 603312 | 537776 | 147.293 | 131.293 | 12514 |
| 3 | bifurcan | 780296 | 714760 | 190.502 | 174.502 | 17192 |
| 4 | dexx | 1064864 | 999328 | 259.977 | 243.977 | 20711 |
| 5 | simple | 33947728 | 33882192 | 8288.020 | 8272.020 | 20485 |

## PersistentOrderedQueue

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | simple | 120 | 120 | - | - | 5 |
| 2 | compact | 144 | 144 | - | - | 6 |
| 3 | treap | 144 | 144 | - | - | 6 |
| 4 | dexx | 152 | 152 | - | - | 8 |
| 5 | bifurcan | 296 | 296 | - | - | 12 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 30960 | 28912 | 241.875 | 225.875 | 866 |
| 2 | treap | 30960 | 28912 | 241.875 | 225.875 | 866 |
| 3 | dexx | 34184 | 32136 | 267.063 | 251.063 | 1490 |
| 4 | bifurcan | 56064 | 54016 | 438.000 | 422.000 | 1625 |
| 5 | simple | 345224 | 343176 | 2697.063 | 2681.063 | 8902 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | compact | 1742856 | 1677320 | 425.502 | 409.502 | 46441 |
| 2 | treap | 1742856 | 1677320 | 425.502 | 409.502 | 46441 |
| 3 | dexx | 1819136 | 1753600 | 444.125 | 428.125 | 77847 |
| 4 | bifurcan | 2999240 | 2933704 | 732.236 | 716.236 | 81950 |
| 5 | simple | 336101512 | 336035976 | 82056.033 | 82040.033 | 8411142 |

## PersistentVector

#### size = 0

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunked | 88 | 88 | - | - | 4 |
| 2 | simple | 96 | 96 | - | - | 5 |
| 3 | dexx | 128 | 128 | - | - | 5 |
| 4 | bifurcan | 200 | 200 | - | - | 8 |

#### size = 128

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunked | 17576 | 15528 | 137.313 | 121.313 | 391 |
| 2 | bifurcan | 23384 | 21336 | 182.688 | 166.688 | 536 |
| 3 | dexx | 32816 | 30768 | 256.375 | 240.375 | 648 |
| 4 | simple | 43072 | 41024 | 336.500 | 320.500 | 516 |

#### size = 4096

| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| 1 | chunked | 570536 | 505000 | 139.291 | 123.291 | 12514 |
| 2 | bifurcan | 780296 | 714760 | 190.502 | 174.502 | 17192 |
| 3 | dexx | 1064864 | 999328 | 259.977 | 243.977 | 20711 |
| 4 | simple | 33882176 | 33816640 | 8272.016 | 8256.016 | 16388 |
