# Performance benchmark summary

- Generated: `2026-04-20T03:10:47.9528648Z`
- Summary CSV: [`2026-04-20T03-10-47Z-performance-benchmark-summary.csv`](./2026-04-20T03-10-47Z-performance-benchmark-summary.csv)
- Source JMH CSVs:
  - [`2026-04-19T23-36-09Z-double-object-persistent-sorted-map.csv`](./2026-04-19T23-36-09Z-double-object-persistent-sorted-map.csv)
  - [`2026-04-20T03-05-35Z-int-object-persistent-map.csv`](./2026-04-20T03-05-35Z-int-object-persistent-map.csv)
  - [`2026-04-19T23-36-09Z-long-object-persistent-map.csv`](./2026-04-19T23-36-09Z-long-object-persistent-map.csv)
  - [`2026-04-19T23-36-09Z-persistent-ordered-queue.csv`](./2026-04-19T23-36-09Z-persistent-ordered-queue.csv)
  - [`2026-04-19T23-36-09Z-persistent-append-sequence.csv`](./2026-04-19T23-36-09Z-persistent-append-sequence.csv)
  - [`2026-04-19T23-36-09Z-persistent-vector.csv`](./2026-04-19T23-36-09Z-persistent-vector.csv)
- Java: `25.0.1` (`OpenJDK 64-Bit Server VM`, `Eclipse Adoptium`)
- OS: `Windows 11 10.0`
- Measured sizes: `128`, `4096`

## Methodology

- Results are parsed from raw JMH CSV output; the CSV snapshots remain the source of truth.
- Lower scores are better because all benchmark classes use `Mode.AverageTime` with `ns/op` output.
- Shared benchmark settings across the five families: `@Warmup(iterations = 2, time = 200ms)`, `@Measurement(iterations = 4, time = 200ms)`, and `@Fork(1)`.

## DoubleObjectPersistentSortedMap

### ascending

#### findExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 1.662 | 0.536 | 4 | ns/op |
| 2 | treap | 1.707 | 0.227 | 4 | ns/op |
| 3 | bifurcanSorted | 9.354 | 1.081 | 4 | ns/op |
| 4 | simple | 9.962 | 8.367 | 4 | ns/op |
| 5 | bifurcanFloat | 12.095 | 2.719 | 4 | ns/op |
| 6 | dexx | 13.754 | 2.328 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcanFloat | 14.384 | 2.397 | 4 | ns/op |
| 2 | bifurcanSorted | 14.748 | 4.016 | 4 | ns/op |
| 3 | simple | 15.054 | 1.970 | 4 | ns/op |
| 4 | treap | 22.149 | 4.743 | 4 | ns/op |
| 5 | compact | 23.613 | 5.453 | 4 | ns/op |
| 6 | dexx | 23.759 | 7.407 | 4 | ns/op |

#### findMissing

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcanFloat | 4.217 | 0.697 | 4 | ns/op |
| 2 | compact | 5.290 | 0.572 | 4 | ns/op |
| 3 | treap | 5.343 | 0.880 | 4 | ns/op |
| 4 | bifurcanSorted | 9.458 | 1.371 | 4 | ns/op |
| 5 | simple | 9.462 | 1.001 | 4 | ns/op |
| 6 | dexx | 15.894 | 4.277 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcanFloat | 4.068 | 0.478 | 4 | ns/op |
| 2 | treap | 10.827 | 3.199 | 4 | ns/op |
| 3 | compact | 10.865 | 3.233 | 4 | ns/op |
| 4 | bifurcanSorted | 15.546 | 2.492 | 4 | ns/op |
| 5 | simple | 15.574 | 2.160 | 4 | ns/op |
| 6 | dexx | 25.541 | 6.475 | 4 | ns/op |

#### greaterOrEqualTo

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 188.446 | 43.577 | 4 | ns/op |
| 2 | compact | 192.978 | 23.838 | 4 | ns/op |
| 3 | simple | 275.684 | 34.407 | 4 | ns/op |
| 4 | bifurcanSorted | 414.962 | 183.468 | 4 | ns/op |
| 5 | dexx | 888.774 | 214.752 | 4 | ns/op |
| 6 | bifurcanFloat | 2400.136 | 484.988 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 8898.041 | 2935.584 | 4 | ns/op |
| 2 | compact | 10208.481 | 749.015 | 4 | ns/op |
| 3 | treap | 10487.434 | 2521.516 | 4 | ns/op |
| 4 | dexx | 16568.391 | 2561.885 | 4 | ns/op |
| 5 | bifurcanSorted | 56166.835 | 7889.829 | 4 | ns/op |
| 6 | bifurcanFloat | 160398.837 | 50260.003 | 4 | ns/op |

#### mixedReadHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 1620.839 | 288.920 | 4 | ns/op |
| 2 | compact | 1783.767 | 190.951 | 4 | ns/op |
| 3 | bifurcanSorted | 7639.263 | 3048.839 | 4 | ns/op |
| 4 | simple | 9569.629 | 3799.399 | 4 | ns/op |
| 5 | dexx | 10580.944 | 4968.296 | 4 | ns/op |
| 6 | bifurcanFloat | 30802.708 | 8051.132 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 3120.445 | 877.435 | 4 | ns/op |
| 2 | compact | 3166.484 | 1071.117 | 4 | ns/op |
| 3 | dexx | 78976.436 | 24029.904 | 4 | ns/op |
| 4 | simple | 234760.511 | 100349.647 | 4 | ns/op |
| 5 | bifurcanSorted | 1144978.971 | 968363.537 | 4 | ns/op |
| 6 | bifurcanFloat | 2347440.187 | 250938.273 | 4 | ns/op |

#### mixedUpdateHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 5081.899 | 2135.311 | 4 | ns/op |
| 2 | treap | 5159.842 | 2963.396 | 4 | ns/op |
| 3 | bifurcanSorted | 17789.458 | 1402.469 | 4 | ns/op |
| 4 | dexx | 21834.544 | 6011.189 | 4 | ns/op |
| 5 | bifurcanFloat | 58044.539 | 25699.208 | 4 | ns/op |
| 6 | simple | 95477.846 | 40843.975 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 8763.114 | 1567.837 | 4 | ns/op |
| 2 | compact | 9178.084 | 2443.851 | 4 | ns/op |
| 3 | dexx | 162745.458 | 49179.058 | 4 | ns/op |
| 4 | bifurcanSorted | 2009831.636 | 378667.116 | 4 | ns/op |
| 5 | simple | 2761703.582 | 1205753.904 | 4 | ns/op |
| 6 | bifurcanFloat | 4675627.246 | 273191.935 | 4 | ns/op |

#### putExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 7.169 | 2.312 | 4 | ns/op |
| 2 | compact | 7.204 | 3.030 | 4 | ns/op |
| 3 | bifurcanSorted | 40.511 | 26.841 | 4 | ns/op |
| 4 | dexx | 40.697 | 18.325 | 4 | ns/op |
| 5 | bifurcanFloat | 117.543 | 75.306 | 4 | ns/op |
| 6 | simple | 982.314 | 464.549 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 64.051 | 31.345 | 4 | ns/op |
| 2 | bifurcanSorted | 75.844 | 32.483 | 4 | ns/op |
| 3 | compact | 82.682 | 52.047 | 4 | ns/op |
| 4 | treap | 85.160 | 54.656 | 4 | ns/op |
| 5 | bifurcanFloat | 148.047 | 73.515 | 4 | ns/op |
| 6 | simple | 31380.290 | 11090.918 | 4 | ns/op |

#### putNew

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 36.418 | 12.494 | 4 | ns/op |
| 2 | compact | 37.128 | 20.239 | 4 | ns/op |
| 3 | dexx | 47.930 | 34.839 | 4 | ns/op |
| 4 | bifurcanFloat | 61.307 | 33.840 | 4 | ns/op |
| 5 | bifurcanSorted | 61.806 | 46.713 | 4 | ns/op |
| 6 | simple | 985.118 | 451.465 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 59.950 | 29.769 | 4 | ns/op |
| 2 | compact | 61.189 | 39.583 | 4 | ns/op |
| 3 | bifurcanFloat | 63.373 | 31.951 | 4 | ns/op |
| 4 | dexx | 68.522 | 60.438 | 4 | ns/op |
| 5 | bifurcanSorted | 91.337 | 60.317 | 4 | ns/op |
| 6 | simple | 32248.509 | 17210.455 | 4 | ns/op |

#### removeExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 32.454 | 18.425 | 4 | ns/op |
| 2 | treap | 32.619 | 14.179 | 4 | ns/op |
| 3 | dexx | 53.042 | 35.213 | 4 | ns/op |
| 4 | bifurcanSorted | 91.867 | 48.518 | 4 | ns/op |
| 5 | bifurcanFloat | 138.157 | 64.198 | 4 | ns/op |
| 6 | simple | 985.688 | 228.624 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 78.203 | 53.398 | 4 | ns/op |
| 2 | treap | 79.352 | 39.644 | 4 | ns/op |
| 3 | dexx | 93.131 | 23.408 | 4 | ns/op |
| 4 | bifurcanFloat | 168.323 | 77.543 | 4 | ns/op |
| 5 | bifurcanSorted | 171.226 | 96.759 | 4 | ns/op |
| 6 | simple | 32459.022 | 23911.670 | 4 | ns/op |

#### size

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 0.496 | 0.084 | 4 | ns/op |
| 2 | compact | 0.497 | 0.111 | 4 | ns/op |
| 3 | bifurcanSorted | 0.727 | 0.105 | 4 | ns/op |
| 4 | simple | 0.727 | 0.124 | 4 | ns/op |
| 5 | bifurcanFloat | 1.307 | 0.084 | 4 | ns/op |
| 6 | dexx | 239.273 | 20.308 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 0.492 | 0.112 | 4 | ns/op |
| 2 | compact | 0.492 | 0.046 | 4 | ns/op |
| 3 | simple | 0.730 | 0.106 | 4 | ns/op |
| 4 | bifurcanSorted | 0.739 | 0.109 | 4 | ns/op |
| 5 | bifurcanFloat | 1.294 | 0.213 | 4 | ns/op |
| 6 | dexx | 8039.853 | 8033.579 | 4 | ns/op |

### descending

#### findExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 1.696 | 0.156 | 4 | ns/op |
| 2 | compact | 1.784 | 0.455 | 4 | ns/op |
| 3 | bifurcanSorted | 9.100 | 1.670 | 4 | ns/op |
| 4 | simple | 9.279 | 1.235 | 4 | ns/op |
| 5 | bifurcanFloat | 12.229 | 1.980 | 4 | ns/op |
| 6 | dexx | 15.149 | 4.540 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 15.252 | 1.149 | 4 | ns/op |
| 2 | bifurcanSorted | 15.404 | 0.978 | 4 | ns/op |
| 3 | bifurcanFloat | 15.598 | 2.823 | 4 | ns/op |
| 4 | treap | 22.078 | 6.462 | 4 | ns/op |
| 5 | compact | 22.522 | 1.300 | 4 | ns/op |
| 6 | dexx | 26.582 | 11.851 | 4 | ns/op |

#### findMissing

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcanFloat | 4.359 | 0.532 | 4 | ns/op |
| 2 | treap | 5.348 | 1.284 | 4 | ns/op |
| 3 | compact | 5.385 | 0.608 | 4 | ns/op |
| 4 | simple | 9.366 | 1.430 | 4 | ns/op |
| 5 | bifurcanSorted | 9.479 | 1.206 | 4 | ns/op |
| 6 | dexx | 14.559 | 2.812 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcanFloat | 4.357 | 0.392 | 4 | ns/op |
| 2 | treap | 10.443 | 1.812 | 4 | ns/op |
| 3 | compact | 10.662 | 0.982 | 4 | ns/op |
| 4 | simple | 15.626 | 1.180 | 4 | ns/op |
| 5 | bifurcanSorted | 16.079 | 3.257 | 4 | ns/op |
| 6 | dexx | 22.851 | 3.975 | 4 | ns/op |

#### greaterOrEqualTo

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 222.661 | 46.099 | 4 | ns/op |
| 2 | treap | 226.514 | 50.572 | 4 | ns/op |
| 3 | simple | 260.144 | 65.009 | 4 | ns/op |
| 4 | bifurcanSorted | 412.504 | 188.260 | 4 | ns/op |
| 5 | dexx | 913.105 | 267.541 | 4 | ns/op |
| 6 | bifurcanFloat | 2445.567 | 296.122 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 9072.240 | 2535.409 | 4 | ns/op |
| 2 | treap | 9488.911 | 929.195 | 4 | ns/op |
| 3 | compact | 9514.287 | 1081.186 | 4 | ns/op |
| 4 | dexx | 16480.436 | 5985.475 | 4 | ns/op |
| 5 | bifurcanSorted | 57730.744 | 7086.712 | 4 | ns/op |
| 6 | bifurcanFloat | 156947.838 | 11682.125 | 4 | ns/op |

#### mixedReadHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 1458.761 | 332.010 | 4 | ns/op |
| 2 | compact | 1541.697 | 296.817 | 4 | ns/op |
| 3 | bifurcanSorted | 8541.711 | 4049.894 | 4 | ns/op |
| 4 | simple | 9480.301 | 2129.932 | 4 | ns/op |
| 5 | dexx | 10870.008 | 2863.246 | 4 | ns/op |
| 6 | bifurcanFloat | 31282.998 | 9639.249 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 3087.908 | 811.881 | 4 | ns/op |
| 2 | treap | 3181.496 | 1189.985 | 4 | ns/op |
| 3 | dexx | 79526.242 | 23074.735 | 4 | ns/op |
| 4 | simple | 235314.174 | 117822.365 | 4 | ns/op |
| 5 | bifurcanSorted | 1086767.403 | 237914.150 | 4 | ns/op |
| 6 | bifurcanFloat | 2399805.961 | 784546.536 | 4 | ns/op |

#### mixedUpdateHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 5165.185 | 3658.240 | 4 | ns/op |
| 2 | treap | 5175.915 | 4533.278 | 4 | ns/op |
| 3 | bifurcanSorted | 17172.324 | 7760.096 | 4 | ns/op |
| 4 | dexx | 22141.349 | 9423.952 | 4 | ns/op |
| 5 | bifurcanFloat | 58379.869 | 19712.305 | 4 | ns/op |
| 6 | simple | 96577.343 | 40211.376 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 8700.689 | 1705.500 | 4 | ns/op |
| 2 | treap | 8776.686 | 745.612 | 4 | ns/op |
| 3 | dexx | 160444.441 | 66493.005 | 4 | ns/op |
| 4 | bifurcanSorted | 2296898.117 | 131799.106 | 4 | ns/op |
| 5 | simple | 2803312.528 | 1034878.876 | 4 | ns/op |
| 6 | bifurcanFloat | 4614536.879 | 1860924.322 | 4 | ns/op |

#### putExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 6.781 | 3.323 | 4 | ns/op |
| 2 | treap | 7.134 | 2.653 | 4 | ns/op |
| 3 | dexx | 40.371 | 20.136 | 4 | ns/op |
| 4 | bifurcanSorted | 41.407 | 18.968 | 4 | ns/op |
| 5 | bifurcanFloat | 120.198 | 56.730 | 4 | ns/op |
| 6 | simple | 984.281 | 406.098 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 63.428 | 35.253 | 4 | ns/op |
| 2 | bifurcanSorted | 72.396 | 20.766 | 4 | ns/op |
| 3 | compact | 82.707 | 57.440 | 4 | ns/op |
| 4 | treap | 83.815 | 48.146 | 4 | ns/op |
| 5 | bifurcanFloat | 151.159 | 89.554 | 4 | ns/op |
| 6 | simple | 32024.603 | 16812.196 | 4 | ns/op |

#### putNew

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 36.665 | 22.102 | 4 | ns/op |
| 2 | compact | 37.888 | 21.385 | 4 | ns/op |
| 3 | dexx | 46.931 | 29.848 | 4 | ns/op |
| 4 | bifurcanSorted | 60.619 | 40.479 | 4 | ns/op |
| 5 | bifurcanFloat | 61.509 | 27.451 | 4 | ns/op |
| 6 | simple | 1022.309 | 442.346 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 61.556 | 46.035 | 4 | ns/op |
| 2 | treap | 61.884 | 36.998 | 4 | ns/op |
| 3 | bifurcanFloat | 62.396 | 27.401 | 4 | ns/op |
| 4 | dexx | 69.465 | 49.351 | 4 | ns/op |
| 5 | bifurcanSorted | 85.444 | 52.313 | 4 | ns/op |
| 6 | simple | 32017.584 | 8876.968 | 4 | ns/op |

#### removeExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 32.238 | 15.978 | 4 | ns/op |
| 2 | treap | 33.345 | 15.122 | 4 | ns/op |
| 3 | dexx | 54.921 | 41.456 | 4 | ns/op |
| 4 | bifurcanSorted | 93.020 | 50.616 | 4 | ns/op |
| 5 | bifurcanFloat | 141.408 | 83.785 | 4 | ns/op |
| 6 | simple | 1024.018 | 581.301 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 78.360 | 37.672 | 4 | ns/op |
| 2 | compact | 79.242 | 36.583 | 4 | ns/op |
| 3 | dexx | 99.061 | 32.019 | 4 | ns/op |
| 4 | bifurcanFloat | 169.191 | 64.073 | 4 | ns/op |
| 5 | bifurcanSorted | 176.566 | 71.612 | 4 | ns/op |
| 6 | simple | 32302.498 | 9771.031 | 4 | ns/op |

#### size

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 0.490 | 0.029 | 4 | ns/op |
| 2 | treap | 0.493 | 0.216 | 4 | ns/op |
| 3 | simple | 0.713 | 0.069 | 4 | ns/op |
| 4 | bifurcanSorted | 0.716 | 0.121 | 4 | ns/op |
| 5 | bifurcanFloat | 1.304 | 0.317 | 4 | ns/op |
| 6 | dexx | 179.428 | 41.980 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 0.498 | 0.140 | 4 | ns/op |
| 2 | compact | 0.514 | 0.126 | 4 | ns/op |
| 3 | simple | 0.683 | 0.121 | 4 | ns/op |
| 4 | bifurcanSorted | 0.740 | 0.157 | 4 | ns/op |
| 5 | bifurcanFloat | 1.316 | 0.117 | 4 | ns/op |
| 6 | dexx | 7492.717 | 3316.229 | 4 | ns/op |

## IntObjectPersistentMap

#### findExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 5.406 | 2.387 | 4 | ns/op |
| 2 | simple | 7.476 | 5.577 | 4 | ns/op |
| 3 | bifurcan | 11.610 | 3.291 | 4 | ns/op |
| 4 | bifurcanMap | 13.803 | 3.622 | 4 | ns/op |
| 5 | dexx | 28.522 | 18.827 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 6.689 | 1.250 | 4 | ns/op |
| 2 | hamt | 9.466 | 1.694 | 4 | ns/op |
| 3 | bifurcan | 18.083 | 5.121 | 4 | ns/op |
| 4 | bifurcanMap | 21.406 | 12.944 | 4 | ns/op |
| 5 | dexx | 33.836 | 39.553 | 4 | ns/op |

#### findMissing

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 1.831 | 0.642 | 4 | ns/op |
| 2 | bifurcan | 2.848 | 0.414 | 4 | ns/op |
| 3 | simple | 4.749 | 5.217 | 4 | ns/op |
| 4 | dexx | 7.947 | 7.235 | 4 | ns/op |
| 5 | bifurcanMap | 9.611 | 3.525 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 1.668 | 0.311 | 4 | ns/op |
| 2 | simple | 5.037 | 6.891 | 4 | ns/op |
| 3 | bifurcanMap | 14.876 | 4.355 | 4 | ns/op |
| 4 | bifurcan | 17.845 | 3.955 | 4 | ns/op |
| 5 | dexx | 30.791 | 36.746 | 4 | ns/op |

#### iterateAll

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 541.455 | 112.336 | 4 | ns/op |
| 2 | simple | 604.673 | 181.081 | 4 | ns/op |
| 3 | bifurcanMap | 693.106 | 130.231 | 4 | ns/op |
| 4 | bifurcan | 3493.973 | 1080.349 | 4 | ns/op |
| 5 | dexx | 6396.420 | 3465.623 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 14956.290 | 2770.180 | 4 | ns/op |
| 2 | bifurcanMap | 22564.005 | 5384.220 | 4 | ns/op |
| 3 | simple | 39676.373 | 6589.016 | 4 | ns/op |
| 4 | bifurcan | 93543.264 | 29314.940 | 4 | ns/op |
| 5 | dexx | 201312.375 | 116531.715 | 4 | ns/op |

#### mixedReadHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 1852.444 | 2183.467 | 4 | ns/op |
| 2 | bifurcan | 3368.297 | 4476.743 | 4 | ns/op |
| 3 | bifurcanMap | 4361.459 | 7343.608 | 4 | ns/op |
| 4 | dexx | 5038.395 | 7949.975 | 4 | ns/op |
| 5 | simple | 28377.165 | 54067.715 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 2151.017 | 3064.596 | 4 | ns/op |
| 2 | bifurcan | 5504.477 | 8071.433 | 4 | ns/op |
| 3 | dexx | 8547.018 | 15485.301 | 4 | ns/op |
| 4 | bifurcanMap | 9070.213 | 11926.663 | 4 | ns/op |
| 5 | simple | 674495.329 | 366531.952 | 4 | ns/op |

#### mixedUpdateHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 13504.748 | 22646.413 | 4 | ns/op |
| 2 | dexx | 23660.756 | 33069.887 | 4 | ns/op |
| 3 | bifurcanMap | 37064.883 | 69117.497 | 4 | ns/op |
| 4 | bifurcan | 41982.776 | 50854.074 | 4 | ns/op |
| 5 | simple | 384885.523 | 371744.899 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 18676.185 | 36189.181 | 4 | ns/op |
| 2 | dexx | 30779.498 | 70192.537 | 4 | ns/op |
| 3 | bifurcan | 43939.146 | 68050.701 | 4 | ns/op |
| 4 | bifurcanMap | 62850.013 | 126468.058 | 4 | ns/op |
| 5 | simple | 9101740.561 | 8575001.743 | 4 | ns/op |

#### putExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 93.571 | 127.432 | 4 | ns/op |
| 2 | dexx | 105.058 | 117.423 | 4 | ns/op |
| 3 | bifurcan | 177.031 | 193.785 | 4 | ns/op |
| 4 | bifurcanMap | 197.043 | 286.896 | 4 | ns/op |
| 5 | simple | 3248.949 | 6318.578 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 135.802 | 207.622 | 4 | ns/op |
| 2 | dexx | 181.124 | 345.997 | 4 | ns/op |
| 3 | bifurcanMap | 288.991 | 466.695 | 4 | ns/op |
| 4 | bifurcan | 470.871 | 1549.166 | 4 | ns/op |
| 5 | simple | 85988.005 | 9302.961 | 4 | ns/op |

#### putNew

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 11.982 | 16.336 | 4 | ns/op |
| 2 | dexx | 165.881 | 274.089 | 4 | ns/op |
| 3 | bifurcan | 508.675 | 358.068 | 4 | ns/op |
| 4 | bifurcanMap | 685.484 | 701.237 | 4 | ns/op |
| 5 | simple | 3074.549 | 7066.690 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 13.369 | 16.788 | 4 | ns/op |
| 2 | bifurcan | 119.957 | 149.480 | 4 | ns/op |
| 3 | dexx | 230.606 | 457.403 | 4 | ns/op |
| 4 | bifurcanMap | 703.309 | 1000.328 | 4 | ns/op |
| 5 | simple | 79756.632 | 24495.539 | 4 | ns/op |

#### removeExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 94.024 | 115.152 | 4 | ns/op |
| 2 | dexx | 156.191 | 333.505 | 4 | ns/op |
| 3 | bifurcanMap | 328.806 | 286.832 | 4 | ns/op |
| 4 | bifurcan | 688.641 | 597.087 | 4 | ns/op |
| 5 | simple | 2987.382 | 4506.110 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 163.776 | 194.584 | 4 | ns/op |
| 2 | dexx | 206.916 | 486.060 | 4 | ns/op |
| 3 | bifurcanMap | 456.158 | 1476.836 | 4 | ns/op |
| 4 | bifurcan | 1010.450 | 1227.718 | 4 | ns/op |
| 5 | simple | 81791.077 | 24763.827 | 4 | ns/op |

#### size

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 1.727 | 0.354 | 4 | ns/op |
| 2 | bifurcanMap | 1.930 | 0.782 | 4 | ns/op |
| 3 | simple | 2.040 | 1.278 | 4 | ns/op |
| 4 | bifurcan | 2.569 | 0.681 | 4 | ns/op |
| 5 | dexx | 4.141 | 4.776 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 1.770 | 0.177 | 4 | ns/op |
| 2 | simple | 1.843 | 0.965 | 4 | ns/op |
| 3 | bifurcanMap | 2.008 | 0.396 | 4 | ns/op |
| 4 | bifurcan | 2.644 | 0.497 | 4 | ns/op |
| 5 | dexx | 2.893 | 1.042 | 4 | ns/op |

## LongObjectPersistentMap

#### findExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 2.433 | 0.461 | 4 | ns/op |
| 2 | compact | 3.994 | 0.516 | 4 | ns/op |
| 3 | hamt | 4.224 | 0.542 | 4 | ns/op |
| 4 | bifurcan | 4.536 | 0.847 | 4 | ns/op |
| 5 | bifurcanMap | 5.575 | 1.270 | 4 | ns/op |
| 6 | dexx | 9.981 | 0.982 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 2.999 | 1.530 | 4 | ns/op |
| 2 | hamt | 6.599 | 0.382 | 4 | ns/op |
| 3 | compact | 6.659 | 0.949 | 4 | ns/op |
| 4 | bifurcan | 7.201 | 1.890 | 4 | ns/op |
| 5 | bifurcanMap | 9.029 | 2.127 | 4 | ns/op |
| 6 | dexx | 12.112 | 5.008 | 4 | ns/op |

#### findMissing

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcan | 1.265 | 0.425 | 4 | ns/op |
| 2 | simple | 2.350 | 0.704 | 4 | ns/op |
| 3 | hamt | 2.623 | 0.564 | 4 | ns/op |
| 4 | compact | 2.769 | 0.380 | 4 | ns/op |
| 5 | dexx | 3.400 | 1.746 | 4 | ns/op |
| 6 | bifurcanMap | 4.565 | 1.043 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 2.461 | 0.525 | 4 | ns/op |
| 2 | hamt | 2.579 | 0.147 | 4 | ns/op |
| 3 | compact | 4.928 | 1.311 | 4 | ns/op |
| 4 | bifurcanMap | 6.321 | 1.516 | 4 | ns/op |
| 5 | bifurcan | 7.150 | 0.727 | 4 | ns/op |
| 6 | dexx | 12.129 | 2.378 | 4 | ns/op |

#### iterateAll

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 264.626 | 25.865 | 4 | ns/op |
| 2 | bifurcanMap | 271.681 | 24.280 | 4 | ns/op |
| 3 | compact | 283.252 | 6.352 | 4 | ns/op |
| 4 | hamt | 369.579 | 48.847 | 4 | ns/op |
| 5 | bifurcan | 1338.446 | 177.434 | 4 | ns/op |
| 6 | dexx | 2378.536 | 669.070 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcanMap | 10113.528 | 21636.252 | 4 | ns/op |
| 2 | compact | 10597.079 | 2165.397 | 4 | ns/op |
| 3 | hamt | 12138.202 | 896.474 | 4 | ns/op |
| 4 | simple | 19523.829 | 10014.684 | 4 | ns/op |
| 5 | bifurcan | 39601.446 | 6839.198 | 4 | ns/op |
| 6 | dexx | 75075.173 | 14819.965 | 4 | ns/op |

#### mixedReadHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 783.950 | 304.440 | 4 | ns/op |
| 2 | bifurcan | 921.570 | 273.452 | 4 | ns/op |
| 3 | compact | 956.693 | 429.817 | 4 | ns/op |
| 4 | dexx | 1317.077 | 350.044 | 4 | ns/op |
| 5 | bifurcanMap | 1322.440 | 863.404 | 4 | ns/op |
| 6 | simple | 9023.577 | 1207.933 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 1209.218 | 374.684 | 4 | ns/op |
| 2 | compact | 1376.539 | 132.552 | 4 | ns/op |
| 3 | bifurcan | 1721.926 | 619.161 | 4 | ns/op |
| 4 | dexx | 2053.128 | 644.037 | 4 | ns/op |
| 5 | bifurcanMap | 2147.475 | 701.701 | 4 | ns/op |
| 6 | simple | 258676.431 | 83455.938 | 4 | ns/op |

#### mixedUpdateHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 4940.197 | 3273.972 | 4 | ns/op |
| 2 | dexx | 5524.489 | 2014.289 | 4 | ns/op |
| 3 | compact | 6171.350 | 2568.366 | 4 | ns/op |
| 4 | bifurcanMap | 11041.280 | 3715.230 | 4 | ns/op |
| 5 | bifurcan | 11414.822 | 2305.110 | 4 | ns/op |
| 6 | simple | 109155.762 | 44953.490 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 8214.411 | 5887.637 | 4 | ns/op |
| 2 | compact | 8350.490 | 5030.859 | 4 | ns/op |
| 3 | hamt | 8578.526 | 4231.602 | 4 | ns/op |
| 4 | bifurcan | 14735.498 | 5495.862 | 4 | ns/op |
| 5 | bifurcanMap | 18633.848 | 7766.718 | 4 | ns/op |
| 6 | simple | 3433659.220 | 822437.596 | 4 | ns/op |

#### putExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 34.328 | 17.875 | 4 | ns/op |
| 2 | hamt | 35.676 | 27.578 | 4 | ns/op |
| 3 | dexx | 38.757 | 22.410 | 4 | ns/op |
| 4 | bifurcan | 58.585 | 21.030 | 4 | ns/op |
| 5 | bifurcanMap | 62.789 | 32.075 | 4 | ns/op |
| 6 | simple | 970.379 | 318.342 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 50.484 | 23.026 | 4 | ns/op |
| 2 | compact | 55.506 | 18.966 | 4 | ns/op |
| 3 | dexx | 59.354 | 42.052 | 4 | ns/op |
| 4 | bifurcan | 98.302 | 54.107 | 4 | ns/op |
| 5 | bifurcanMap | 98.951 | 48.740 | 4 | ns/op |
| 6 | simple | 36888.670 | 16621.774 | 4 | ns/op |

#### putNew

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 47.577 | 24.903 | 4 | ns/op |
| 2 | hamt | 48.325 | 31.758 | 4 | ns/op |
| 3 | compact | 68.299 | 27.955 | 4 | ns/op |
| 4 | bifurcan | 160.845 | 67.235 | 4 | ns/op |
| 5 | bifurcanMap | 200.840 | 27.393 | 4 | ns/op |
| 6 | simple | 1058.448 | 337.110 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcan | 45.526 | 29.197 | 4 | ns/op |
| 2 | dexx | 60.791 | 35.409 | 4 | ns/op |
| 3 | compact | 86.588 | 36.658 | 4 | ns/op |
| 4 | hamt | 159.620 | 35.703 | 4 | ns/op |
| 5 | bifurcanMap | 248.314 | 122.878 | 4 | ns/op |
| 6 | simple | 36685.498 | 13352.961 | 4 | ns/op |

#### removeExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 41.254 | 16.258 | 4 | ns/op |
| 2 | dexx | 49.426 | 22.165 | 4 | ns/op |
| 3 | compact | 64.137 | 27.060 | 4 | ns/op |
| 4 | bifurcanMap | 80.983 | 15.009 | 4 | ns/op |
| 5 | bifurcan | 193.932 | 39.400 | 4 | ns/op |
| 6 | simple | 1187.083 | 179.434 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 52.336 | 23.367 | 4 | ns/op |
| 2 | compact | 60.967 | 21.521 | 4 | ns/op |
| 3 | dexx | 66.684 | 33.378 | 4 | ns/op |
| 4 | bifurcanMap | 120.696 | 84.183 | 4 | ns/op |
| 5 | bifurcan | 225.789 | 44.177 | 4 | ns/op |
| 6 | simple | 36070.586 | 13079.542 | 4 | ns/op |

#### size

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 0.492 | 0.111 | 4 | ns/op |
| 2 | hamt | 0.512 | 0.038 | 4 | ns/op |
| 3 | bifurcanMap | 0.697 | 0.099 | 4 | ns/op |
| 4 | simple | 0.734 | 0.141 | 4 | ns/op |
| 5 | dexx | 0.894 | 0.049 | 4 | ns/op |
| 6 | bifurcan | 0.979 | 0.093 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | hamt | 0.503 | 0.149 | 4 | ns/op |
| 2 | compact | 0.510 | 0.154 | 4 | ns/op |
| 3 | bifurcanMap | 0.733 | 0.158 | 4 | ns/op |
| 4 | simple | 0.742 | 0.063 | 4 | ns/op |
| 5 | dexx | 0.911 | 0.138 | 4 | ns/op |
| 6 | bifurcan | 0.972 | 0.131 | 4 | ns/op |

## PersistentAppendSequence

#### append

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcan | 7.320 | 4.085 | 4 | ns/op |
| 2 | chunkedVector | 16.211 | 5.387 | 4 | ns/op |
| 3 | chunkedAppend | 17.191 | 7.110 | 4 | ns/op |
| 4 | dexx | 30.256 | 28.255 | 4 | ns/op |
| 5 | simple | 356.725 | 150.672 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcan | 9.701 | 5.787 | 4 | ns/op |
| 2 | chunkedAppend | 29.587 | 10.802 | 4 | ns/op |
| 3 | chunkedVector | 30.802 | 15.696 | 4 | ns/op |
| 4 | dexx | 48.418 | 33.565 | 4 | ns/op |
| 5 | simple | 8686.748 | 3914.240 | 4 | ns/op |

#### first

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedVector | 0.757 | 0.163 | 4 | ns/op |
| 2 | chunkedAppend | 0.763 | 0.098 | 4 | ns/op |
| 3 | simple | 1.310 | 0.377 | 4 | ns/op |
| 4 | dexx | 2.112 | 0.374 | 4 | ns/op |
| 5 | bifurcan | 2.752 | 0.897 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedVector | 0.750 | 0.098 | 4 | ns/op |
| 2 | chunkedAppend | 0.764 | 0.056 | 4 | ns/op |
| 3 | simple | 1.266 | 0.220 | 4 | ns/op |
| 4 | dexx | 2.680 | 0.574 | 4 | ns/op |
| 5 | bifurcan | 4.264 | 0.805 | 4 | ns/op |

#### iterateAll

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 23.746 | 5.527 | 4 | ns/op |
| 2 | chunkedVector | 105.934 | 13.755 | 4 | ns/op |
| 3 | dexx | 160.922 | 36.429 | 4 | ns/op |
| 4 | bifurcan | 184.398 | 32.437 | 4 | ns/op |
| 5 | chunkedAppend | 201.643 | 34.370 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 561.695 | 69.894 | 4 | ns/op |
| 2 | chunkedVector | 4471.163 | 3663.489 | 4 | ns/op |
| 3 | dexx | 5221.477 | 1310.828 | 4 | ns/op |
| 4 | bifurcan | 5525.719 | 2117.796 | 4 | ns/op |
| 5 | chunkedAppend | 6580.651 | 1198.236 | 4 | ns/op |

#### last

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedAppend | 0.744 | 0.136 | 4 | ns/op |
| 2 | chunkedVector | 1.007 | 0.068 | 4 | ns/op |
| 3 | simple | 1.404 | 0.092 | 4 | ns/op |
| 4 | dexx | 1.640 | 0.335 | 4 | ns/op |
| 5 | bifurcan | 3.237 | 0.440 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedAppend | 0.730 | 0.212 | 4 | ns/op |
| 2 | chunkedVector | 0.955 | 0.240 | 4 | ns/op |
| 3 | simple | 1.458 | 0.342 | 4 | ns/op |
| 4 | dexx | 1.659 | 0.264 | 4 | ns/op |
| 5 | bifurcan | 4.862 | 1.016 | 4 | ns/op |

#### mixedAppendHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedVector | 1050.237 | 569.456 | 4 | ns/op |
| 2 | chunkedAppend | 1108.974 | 304.563 | 4 | ns/op |
| 3 | bifurcan | 1584.060 | 554.022 | 4 | ns/op |
| 4 | dexx | 1819.530 | 946.800 | 4 | ns/op |
| 5 | simple | 25514.363 | 10246.051 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedAppend | 1130.471 | 341.974 | 4 | ns/op |
| 2 | chunkedVector | 1160.066 | 843.774 | 4 | ns/op |
| 3 | bifurcan | 1801.651 | 817.362 | 4 | ns/op |
| 4 | dexx | 1933.995 | 1261.715 | 4 | ns/op |
| 5 | simple | 536462.382 | 244202.765 | 4 | ns/op |

#### mixedReadHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedVector | 403.649 | 42.652 | 4 | ns/op |
| 2 | chunkedAppend | 441.206 | 87.501 | 4 | ns/op |
| 3 | bifurcan | 537.208 | 139.025 | 4 | ns/op |
| 4 | simple | 1667.115 | 810.879 | 4 | ns/op |
| 5 | dexx | 1691.692 | 377.881 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedAppend | 465.242 | 51.655 | 4 | ns/op |
| 2 | chunkedVector | 511.570 | 229.939 | 4 | ns/op |
| 3 | bifurcan | 707.270 | 242.870 | 4 | ns/op |
| 4 | dexx | 2601.323 | 1330.795 | 4 | ns/op |
| 5 | simple | 34666.974 | 12688.264 | 4 | ns/op |

#### size

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedVector | 0.494 | 0.050 | 4 | ns/op |
| 2 | chunkedAppend | 0.497 | 0.034 | 4 | ns/op |
| 3 | dexx | 0.682 | 0.106 | 4 | ns/op |
| 4 | simple | 0.964 | 0.125 | 4 | ns/op |
| 5 | bifurcan | 1.351 | 0.129 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunkedAppend | 0.468 | 0.086 | 4 | ns/op |
| 2 | chunkedVector | 0.509 | 0.053 | 4 | ns/op |
| 3 | dexx | 0.683 | 0.109 | 4 | ns/op |
| 4 | simple | 0.961 | 0.108 | 4 | ns/op |
| 5 | bifurcan | 1.311 | 0.118 | 4 | ns/op |

## PersistentOrderedQueue

#### addExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 19.382 | 5.235 | 4 | ns/op |
| 2 | treap | 19.690 | 3.979 | 4 | ns/op |
| 3 | dexx | 36.868 | 21.832 | 4 | ns/op |
| 4 | bifurcan | 43.052 | 21.326 | 4 | ns/op |
| 5 | simple | 1022.869 | 460.751 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 54.289 | 41.579 | 4 | ns/op |
| 2 | compact | 62.222 | 29.183 | 4 | ns/op |
| 3 | treap | 63.678 | 27.079 | 4 | ns/op |
| 4 | bifurcan | 71.411 | 27.557 | 4 | ns/op |
| 5 | simple | 31237.229 | 11978.584 | 4 | ns/op |

#### addNew

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 42.369 | 17.881 | 4 | ns/op |
| 2 | compact | 43.002 | 20.939 | 4 | ns/op |
| 3 | treap | 44.383 | 16.435 | 4 | ns/op |
| 4 | bifurcan | 60.064 | 45.724 | 4 | ns/op |
| 5 | simple | 1026.292 | 585.664 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 62.561 | 36.724 | 4 | ns/op |
| 2 | treap | 73.322 | 40.703 | 4 | ns/op |
| 3 | compact | 76.498 | 47.838 | 4 | ns/op |
| 4 | bifurcan | 89.562 | 54.077 | 4 | ns/op |
| 5 | simple | 30753.267 | 10435.176 | 4 | ns/op |

#### first

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 0.765 | 0.067 | 4 | ns/op |
| 2 | treap | 0.769 | 0.282 | 4 | ns/op |
| 3 | simple | 3.757 | 0.773 | 4 | ns/op |
| 4 | bifurcan | 4.649 | 0.992 | 4 | ns/op |
| 5 | dexx | 215.853 | 29.264 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 0.754 | 0.111 | 4 | ns/op |
| 2 | treap | 0.762 | 0.132 | 4 | ns/op |
| 3 | simple | 6.234 | 1.334 | 4 | ns/op |
| 4 | bifurcan | 7.683 | 0.758 | 4 | ns/op |
| 5 | dexx | 5365.325 | 2003.998 | 4 | ns/op |

#### iterateAll

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 342.200 | 83.513 | 4 | ns/op |
| 2 | compact | 346.924 | 73.385 | 4 | ns/op |
| 3 | simple | 395.521 | 103.014 | 4 | ns/op |
| 4 | dexx | 771.225 | 207.780 | 4 | ns/op |
| 5 | bifurcan | 820.402 | 127.543 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 15200.954 | 2984.525 | 4 | ns/op |
| 2 | treap | 15580.592 | 627.954 | 4 | ns/op |
| 3 | simple | 17101.460 | 1261.467 | 4 | ns/op |
| 4 | dexx | 26410.687 | 4961.446 | 4 | ns/op |
| 5 | bifurcan | 142416.722 | 8407.876 | 4 | ns/op |

#### last

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 0.758 | 0.127 | 4 | ns/op |
| 2 | compact | 0.767 | 0.096 | 4 | ns/op |
| 3 | simple | 4.265 | 0.747 | 4 | ns/op |
| 4 | bifurcan | 6.601 | 0.624 | 4 | ns/op |
| 5 | dexx | 211.396 | 33.510 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 0.747 | 0.095 | 4 | ns/op |
| 2 | compact | 0.751 | 0.094 | 4 | ns/op |
| 3 | simple | 6.622 | 0.506 | 4 | ns/op |
| 4 | bifurcan | 11.373 | 3.526 | 4 | ns/op |
| 5 | dexx | 5333.818 | 2079.612 | 4 | ns/op |

#### mixedReadHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 2024.171 | 665.010 | 4 | ns/op |
| 2 | compact | 2533.216 | 447.265 | 4 | ns/op |
| 3 | bifurcan | 4025.330 | 2044.417 | 4 | ns/op |
| 4 | simple | 5676.416 | 2047.570 | 4 | ns/op |
| 5 | dexx | 61728.177 | 2686.255 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 2666.778 | 1180.606 | 4 | ns/op |
| 2 | treap | 2680.927 | 1018.373 | 4 | ns/op |
| 3 | bifurcan | 8596.165 | 3621.582 | 4 | ns/op |
| 4 | simple | 121956.448 | 32838.638 | 4 | ns/op |
| 5 | dexx | 1863847.989 | 374416.925 | 4 | ns/op |

#### mixedUpdateHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 6071.754 | 4074.280 | 4 | ns/op |
| 2 | compact | 6156.251 | 3456.088 | 4 | ns/op |
| 3 | bifurcan | 12028.629 | 5949.876 | 4 | ns/op |
| 4 | dexx | 42019.093 | 8802.558 | 4 | ns/op |
| 5 | simple | 132980.554 | 14934.793 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 10912.947 | 6248.402 | 4 | ns/op |
| 2 | treap | 11000.691 | 5311.708 | 4 | ns/op |
| 3 | bifurcan | 20970.640 | 11800.418 | 4 | ns/op |
| 4 | dexx | 697887.498 | 125009.690 | 4 | ns/op |
| 5 | simple | 3691248.902 | 1242097.171 | 4 | ns/op |

#### removeExisting

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 46.809 | 27.902 | 4 | ns/op |
| 2 | treap | 57.341 | 30.063 | 4 | ns/op |
| 3 | compact | 58.355 | 26.957 | 4 | ns/op |
| 4 | bifurcan | 89.098 | 59.943 | 4 | ns/op |
| 5 | simple | 1036.872 | 514.555 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 59.262 | 39.603 | 4 | ns/op |
| 2 | treap | 59.934 | 40.540 | 4 | ns/op |
| 3 | dexx | 82.043 | 50.800 | 4 | ns/op |
| 4 | bifurcan | 172.847 | 77.041 | 4 | ns/op |
| 5 | simple | 30708.246 | 19386.826 | 4 | ns/op |

#### replaceExistingWithNew

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | dexx | 92.689 | 39.633 | 4 | ns/op |
| 2 | treap | 97.708 | 65.215 | 4 | ns/op |
| 3 | compact | 98.595 | 53.591 | 4 | ns/op |
| 4 | bifurcan | 155.763 | 99.256 | 4 | ns/op |
| 5 | simple | 1057.779 | 349.314 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | compact | 106.719 | 54.943 | 4 | ns/op |
| 2 | treap | 110.576 | 70.115 | 4 | ns/op |
| 3 | dexx | 141.453 | 83.719 | 4 | ns/op |
| 4 | bifurcan | 267.736 | 151.350 | 4 | ns/op |
| 5 | simple | 32090.796 | 12243.751 | 4 | ns/op |

#### size

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 0.500 | 0.119 | 4 | ns/op |
| 2 | compact | 0.523 | 0.261 | 4 | ns/op |
| 3 | bifurcan | 0.921 | 0.081 | 4 | ns/op |
| 4 | simple | 0.986 | 0.162 | 4 | ns/op |
| 5 | dexx | 202.743 | 37.189 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | treap | 0.498 | 0.053 | 4 | ns/op |
| 2 | compact | 0.501 | 0.107 | 4 | ns/op |
| 3 | bifurcan | 0.939 | 0.252 | 4 | ns/op |
| 4 | simple | 1.005 | 0.086 | 4 | ns/op |
| 5 | dexx | 7896.431 | 6861.832 | 4 | ns/op |

## PersistentVector

#### append

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcan | 7.101 | 3.723 | 4 | ns/op |
| 2 | chunked | 16.230 | 9.279 | 4 | ns/op |
| 3 | dexx | 31.227 | 35.844 | 4 | ns/op |
| 4 | simple | 357.968 | 132.568 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | bifurcan | 9.571 | 5.670 | 4 | ns/op |
| 2 | chunked | 29.839 | 8.816 | 4 | ns/op |
| 3 | dexx | 52.539 | 58.902 | 4 | ns/op |
| 4 | simple | 8302.943 | 3184.870 | 4 | ns/op |

#### first

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 0.780 | 0.089 | 4 | ns/op |
| 2 | simple | 1.116 | 0.172 | 4 | ns/op |
| 3 | dexx | 2.038 | 0.466 | 4 | ns/op |
| 4 | bifurcan | 2.829 | 0.525 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 0.773 | 0.082 | 4 | ns/op |
| 2 | simple | 1.132 | 0.128 | 4 | ns/op |
| 3 | dexx | 2.714 | 0.312 | 4 | ns/op |
| 4 | bifurcan | 4.381 | 0.974 | 4 | ns/op |

#### getMiddle

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 1.233 | 0.097 | 4 | ns/op |
| 2 | chunked | 1.873 | 0.579 | 4 | ns/op |
| 3 | dexx | 2.145 | 0.263 | 4 | ns/op |
| 4 | bifurcan | 2.947 | 1.066 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 1.183 | 0.276 | 4 | ns/op |
| 2 | dexx | 2.824 | 0.251 | 4 | ns/op |
| 3 | chunked | 3.336 | 0.572 | 4 | ns/op |
| 4 | bifurcan | 4.638 | 0.725 | 4 | ns/op |

#### getNearEnd

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 1.170 | 0.440 | 4 | ns/op |
| 2 | simple | 1.196 | 0.088 | 4 | ns/op |
| 3 | dexx | 1.641 | 0.301 | 4 | ns/op |
| 4 | bifurcan | 2.887 | 0.544 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 1.132 | 0.283 | 4 | ns/op |
| 2 | simple | 1.229 | 0.317 | 4 | ns/op |
| 3 | dexx | 1.619 | 0.164 | 4 | ns/op |
| 4 | bifurcan | 4.626 | 0.957 | 4 | ns/op |

#### iterateForward

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 23.300 | 3.875 | 4 | ns/op |
| 2 | chunked | 103.344 | 24.768 | 4 | ns/op |
| 3 | dexx | 171.658 | 56.228 | 4 | ns/op |
| 4 | bifurcan | 185.318 | 29.907 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 577.310 | 113.826 | 4 | ns/op |
| 2 | chunked | 4381.674 | 2500.628 | 4 | ns/op |
| 3 | dexx | 5289.607 | 936.176 | 4 | ns/op |
| 4 | bifurcan | 5543.166 | 1879.422 | 4 | ns/op |

#### iterateReverse

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 25.744 | 6.148 | 4 | ns/op |
| 2 | chunked | 93.368 | 23.741 | 4 | ns/op |
| 3 | dexx | 132.261 | 17.313 | 4 | ns/op |
| 4 | bifurcan | 197.462 | 54.184 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 588.102 | 74.156 | 4 | ns/op |
| 2 | chunked | 4241.349 | 1668.430 | 4 | ns/op |
| 3 | dexx | 6004.953 | 1406.125 | 4 | ns/op |
| 4 | bifurcan | 10443.771 | 1388.458 | 4 | ns/op |

#### last

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 0.964 | 0.169 | 4 | ns/op |
| 2 | simple | 1.253 | 0.199 | 4 | ns/op |
| 3 | dexx | 1.663 | 0.410 | 4 | ns/op |
| 4 | bifurcan | 3.361 | 0.246 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 0.980 | 0.048 | 4 | ns/op |
| 2 | simple | 1.244 | 0.132 | 4 | ns/op |
| 3 | dexx | 1.629 | 0.174 | 4 | ns/op |
| 4 | bifurcan | 4.734 | 0.518 | 4 | ns/op |

#### mixedAppendHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 1202.730 | 601.918 | 4 | ns/op |
| 2 | dexx | 1620.008 | 881.447 | 4 | ns/op |
| 3 | bifurcan | 1716.742 | 902.193 | 4 | ns/op |
| 4 | simple | 25849.112 | 10829.531 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 1283.935 | 589.466 | 4 | ns/op |
| 2 | dexx | 1816.063 | 1068.044 | 4 | ns/op |
| 3 | bifurcan | 1974.883 | 842.818 | 4 | ns/op |
| 4 | simple | 537910.607 | 192432.111 | 4 | ns/op |

#### mixedReadHeavy

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 129.094 | 27.149 | 4 | ns/op |
| 2 | chunked | 227.341 | 85.339 | 4 | ns/op |
| 3 | dexx | 457.052 | 80.549 | 4 | ns/op |
| 4 | bifurcan | 528.545 | 136.977 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | simple | 127.964 | 17.336 | 4 | ns/op |
| 2 | chunked | 308.485 | 67.159 | 4 | ns/op |
| 3 | dexx | 580.867 | 64.853 | 4 | ns/op |
| 4 | bifurcan | 845.169 | 180.266 | 4 | ns/op |

#### size

##### size = 128

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 0.493 | 0.100 | 4 | ns/op |
| 2 | dexx | 0.710 | 0.140 | 4 | ns/op |
| 3 | simple | 0.861 | 0.106 | 4 | ns/op |
| 4 | bifurcan | 1.419 | 0.210 | 4 | ns/op |

##### size = 4096

| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |
| --- | --- | ---: | ---: | ---: | --- |
| 1 | chunked | 0.495 | 0.078 | 4 | ns/op |
| 2 | dexx | 0.686 | 0.130 | 4 | ns/op |
| 3 | simple | 0.897 | 0.164 | 4 | ns/op |
| 4 | bifurcan | 1.383 | 0.190 | 4 | ns/op |
