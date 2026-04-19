package io.github.alexoooo.vibe.data.benchmark;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class MemoryUsageReportFormatterTest {

    @Test
    void rendersCsvWithExpectedColumns() {
        String csv = MemoryUsageReportFormatter.toCsv(exampleRowsForFormattingOnly());

        assertTrue(csv.startsWith(
                "\"Interface\",\"Variant\",\"Implementation\",\"Size\",\"Retained Bytes\",\"Structural Bytes\""));
        assertTrue(csv.contains("\"DoubleObjectPersistentSortedMap\",\"ascending\",\"treap\",128,960,704,7.500,5.500,48,32"));
    }

    @Test
    void rendersMarkdownRankedByRetainedBytes() {
        String markdown = MemoryUsageReportFormatter.toMarkdown(
                exampleMetadata(),
                "2026-04-19T18-40-00Z-memory-usage.csv",
                exampleRowsForFormattingOnly());

        assertTrue(markdown.contains("# Memory usage summary"));
        assertTrue(markdown.contains("## DoubleObjectPersistentSortedMap"));
        assertTrue(markdown.contains("### ascending"));
        assertTrue(markdown.indexOf("| 1 | treap | 960 | 704 |") < markdown.indexOf("| 2 | simple | 1024 | 768 |"));
        assertTrue(markdown.contains("Source CSV: [`2026-04-19T18-40-00Z-memory-usage.csv`](./2026-04-19T18-40-00Z-memory-usage.csv)"));
    }

    private static MemoryUsageReport.RunMetadata exampleMetadata() {
        return new MemoryUsageReport.RunMetadata(
                Instant.parse("2026-04-19T18:40:00Z"),
                "25.0.1",
                "OpenJDK 64-Bit Server VM",
                "Eclipse Adoptium",
                "Windows 11",
                "10.0",
                "Mock VM details",
                16L);
    }

    private static List<MemoryUsageReport.MeasurementRow> exampleRowsForFormattingOnly() {
        return List.of(
                new MemoryUsageReport.MeasurementRow(
                        "DoubleObjectPersistentSortedMap",
                        "ascending",
                        "simple",
                        128,
                        1024,
                        768,
                        8.0,
                        6.0,
                        56,
                        40),
                new MemoryUsageReport.MeasurementRow(
                        "DoubleObjectPersistentSortedMap",
                        "ascending",
                        "treap",
                        128,
                        960,
                        704,
                        7.5,
                        5.5,
                        48,
                        32),
                new MemoryUsageReport.MeasurementRow(
                        "PersistentVector",
                        "",
                        "chunked",
                        4096,
                        65536,
                        57344,
                        16.0,
                        14.0,
                        8192,
                        4096));
    }
}
