package io.github.alexoooo.vibe.data.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PerformanceBenchmarkReportTest {

    @Test
    void parsesJmhCsvRowsIntoNormalizedMeasurements() {
        String csv = """
                "Benchmark","Mode","Threads","Samples","Score","Score Error (99.9%)","Unit","Param: implementation","Param: order","Param: size"
                "io.github.alexoooo.vibe.data.benchmark.DoubleObjectPersistentSortedMapBenchmark.putNew","avgt",1,4,42.125,3.500,"ns/op",compact,ascending,4096
                "io.github.alexoooo.vibe.data.benchmark.IntObjectPersistentMapBenchmark.putNew","avgt",1,4,55.750,4.000,"ns/op",hamt,,128
                """;

        List<PerformanceBenchmarkReport.MeasurementRow> rows =
                PerformanceBenchmarkReport.parseCsv("example-double-object-persistent-sorted-map.csv", csv);

        assertEquals(2, rows.size());
        assertEquals(
                new PerformanceBenchmarkReport.MeasurementRow(
                        "DoubleObjectPersistentSortedMap",
                        "ascending",
                        "putNew",
                        "compact",
                        4096,
                        4,
                        42.125,
                        3.5,
                        "ns/op",
                        "example-double-object-persistent-sorted-map.csv"),
                rows.get(0));
        assertEquals("IntObjectPersistentMap", rows.get(1).interfaceName());
        assertEquals("", rows.get(1).variant());
        assertEquals(128, rows.get(1).size());
    }
}
