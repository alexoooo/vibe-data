package io.github.alexoooo.vibe.data.benchmark;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class PerformanceBenchmarkReportFormatterTest {

    @Test
    void rendersCsvWithExpectedColumns() {
        String csv = PerformanceBenchmarkReportFormatter.toCsv(exampleRowsForFormattingOnly());

        assertTrue(csv.startsWith(
                "\"Interface\",\"Variant\",\"Operation\",\"Implementation\",\"Size\",\"Samples\",\"Score\""));
        assertTrue(csv.contains("\"PersistentOrderedQueue\",\"\",\"addNew\",\"compact\",4096,4,41.250000,2.500000,\"ns/op\",\"2026-04-19T23-00-00Z-persistent-ordered-queue.csv\""));
    }

    @Test
    void rendersMarkdownRankedByScore() {
        String markdown = PerformanceBenchmarkReportFormatter.toMarkdown(
                exampleMetadata(),
                "2026-04-19T23-10-00Z-performance-benchmark-summary.csv",
                exampleRowsForFormattingOnly());

        assertTrue(markdown.contains("# Performance benchmark summary"));
        assertTrue(markdown.contains("Summary CSV: [`2026-04-19T23-10-00Z-performance-benchmark-summary.csv`]"));
        assertTrue(markdown.contains("## PersistentOrderedQueue"));
        assertTrue(markdown.contains("#### addNew"));
        assertTrue(markdown.indexOf("| 1 | compact | 41.250 | 2.500 |") < markdown.indexOf("| 2 | treap | 55.750 | 4.000 |"));
    }

    private static PerformanceBenchmarkReport.RunMetadata exampleMetadata() {
        return new PerformanceBenchmarkReport.RunMetadata(
                Instant.parse("2026-04-19T23:10:00Z"),
                "25.0.1",
                "OpenJDK 64-Bit Server VM",
                "Eclipse Adoptium",
                "Windows 11",
                "10.0",
                List.of(
                        "2026-04-19T23-00-00Z-persistent-ordered-queue.csv",
                        "2026-04-19T23-00-00Z-persistent-vector.csv"));
    }

    private static List<PerformanceBenchmarkReport.MeasurementRow> exampleRowsForFormattingOnly() {
        return List.of(
                new PerformanceBenchmarkReport.MeasurementRow(
                        "PersistentOrderedQueue",
                        "",
                        "addNew",
                        "treap",
                        4096,
                        4,
                        55.75,
                        4.0,
                        "ns/op",
                        "2026-04-19T23-00-00Z-persistent-ordered-queue.csv"),
                new PerformanceBenchmarkReport.MeasurementRow(
                        "PersistentOrderedQueue",
                        "",
                        "addNew",
                        "compact",
                        4096,
                        4,
                        41.25,
                        2.5,
                        "ns/op",
                        "2026-04-19T23-00-00Z-persistent-ordered-queue.csv"),
                new PerformanceBenchmarkReport.MeasurementRow(
                        "PersistentVector",
                        "",
                        "iterateForward",
                        "chunked",
                        128,
                        4,
                        275.125,
                        12.75,
                        "ns/op",
                        "2026-04-19T23-00-00Z-persistent-vector.csv"));
    }
}
