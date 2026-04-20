package io.github.alexoooo.vibe.data.benchmark;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

final class PerformanceBenchmarkReportFormatter {

    private static final DateTimeFormatter DISPLAY_TIMESTAMP =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    private PerformanceBenchmarkReportFormatter() {
    }

    static String toCsv(List<PerformanceBenchmarkReport.MeasurementRow> rows) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"Interface\",\"Variant\",\"Operation\",\"Implementation\",\"Size\",\"Samples\",")
                .append("\"Score\",\"Score Error (99.9%)\",\"Unit\",\"Source CSV\"\n");

        for (PerformanceBenchmarkReport.MeasurementRow row : sortRows(rows)) {
            builder.append(quoted(row.interfaceName())).append(',')
                    .append(quoted(row.variant())).append(',')
                    .append(quoted(row.operation())).append(',')
                    .append(quoted(row.implementation())).append(',')
                    .append(row.size()).append(',')
                    .append(row.samples()).append(',')
                    .append(formatCsvDouble(row.score())).append(',')
                    .append(formatCsvDouble(row.scoreError())).append(',')
                    .append(quoted(row.unit())).append(',')
                    .append(quoted(row.sourceCsvFileName())).append('\n');
        }

        return builder.toString();
    }

    static String toMarkdown(
            PerformanceBenchmarkReport.RunMetadata metadata,
            String summaryCsvFileName,
            List<PerformanceBenchmarkReport.MeasurementRow> rows) {
        List<PerformanceBenchmarkReport.MeasurementRow> sortedRows = sortRows(rows);
        TreeSet<Integer> sizes = new TreeSet<>();
        for (PerformanceBenchmarkReport.MeasurementRow row : sortedRows) {
            sizes.add(row.size());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("# Performance benchmark summary\n\n")
                .append("- Generated: `").append(DISPLAY_TIMESTAMP.format(metadata.generatedAtUtc())).append("`\n")
                .append("- Summary CSV: [`").append(summaryCsvFileName).append("`](./").append(summaryCsvFileName).append(")\n")
                .append("- Source JMH CSVs:\n");
        for (String sourceCsvFileName : metadata.sourceCsvFileNames()) {
            builder.append("  - [`").append(sourceCsvFileName).append("`](./").append(sourceCsvFileName).append(")\n");
        }
        builder.append("- Java: `").append(metadata.javaVersion()).append("` (`")
                .append(metadata.javaVmName()).append("`, `").append(metadata.javaVmVendor()).append("`)\n")
                .append("- OS: `").append(metadata.osName()).append(' ').append(metadata.osVersion()).append("`\n")
                .append("- Measured sizes: `")
                .append(String.join("`, `", sizes.stream().map(String::valueOf).toList()))
                .append("`\n\n")
                .append("## Methodology\n\n")
                .append("- Results are parsed from raw JMH CSV output; the CSV snapshots remain the source of truth.\n")
                .append("- Lower scores are better because all benchmark classes use `Mode.AverageTime` with `ns/op` output.\n")
                .append("- Shared benchmark settings across the five families: `@Warmup(iterations = 2, time = 200ms)`, `@Measurement(iterations = 4, time = 200ms)`, and `@Fork(1)`.\n");

        Map<String, List<PerformanceBenchmarkReport.MeasurementRow>> byInterface = new LinkedHashMap<>();
        for (PerformanceBenchmarkReport.MeasurementRow row : sortedRows) {
            byInterface.computeIfAbsent(row.interfaceName(), ignored -> new ArrayList<>()).add(row);
        }

        for (Map.Entry<String, List<PerformanceBenchmarkReport.MeasurementRow>> interfaceEntry : byInterface.entrySet()) {
            builder.append("\n## ").append(interfaceEntry.getKey()).append("\n");

            Map<String, List<PerformanceBenchmarkReport.MeasurementRow>> byVariant = new LinkedHashMap<>();
            for (PerformanceBenchmarkReport.MeasurementRow row : interfaceEntry.getValue()) {
                byVariant.computeIfAbsent(row.variant(), ignored -> new ArrayList<>()).add(row);
            }

            boolean showVariantHeader = byVariant.size() > 1 || !byVariant.containsKey("");
            for (Map.Entry<String, List<PerformanceBenchmarkReport.MeasurementRow>> variantEntry : byVariant.entrySet()) {
                if (showVariantHeader) {
                    builder.append("\n### ")
                            .append(variantEntry.getKey().isBlank() ? "default" : variantEntry.getKey())
                            .append('\n');
                }

                Map<String, List<PerformanceBenchmarkReport.MeasurementRow>> byOperation = new LinkedHashMap<>();
                for (PerformanceBenchmarkReport.MeasurementRow row : variantEntry.getValue()) {
                    byOperation.computeIfAbsent(row.operation(), ignored -> new ArrayList<>()).add(row);
                }

                for (Map.Entry<String, List<PerformanceBenchmarkReport.MeasurementRow>> operationEntry : byOperation.entrySet()) {
                    builder.append("\n#### ").append(operationEntry.getKey()).append("\n");

                    Map<Integer, List<PerformanceBenchmarkReport.MeasurementRow>> bySize = new LinkedHashMap<>();
                    for (PerformanceBenchmarkReport.MeasurementRow row : operationEntry.getValue()) {
                        bySize.computeIfAbsent(row.size(), ignored -> new ArrayList<>()).add(row);
                    }

                    for (Map.Entry<Integer, List<PerformanceBenchmarkReport.MeasurementRow>> sizeEntry : bySize.entrySet()) {
                        builder.append("\n##### size = ").append(sizeEntry.getKey()).append("\n\n")
                                .append("| Rank | Implementation | Score | Score Error (99.9%) | Samples | Unit |\n")
                                .append("| --- | --- | ---: | ---: | ---: | --- |\n");

                        List<PerformanceBenchmarkReport.MeasurementRow> rankedRows = new ArrayList<>(sizeEntry.getValue());
                        rankedRows.sort(Comparator.comparingDouble(PerformanceBenchmarkReport.MeasurementRow::score)
                                .thenComparing(PerformanceBenchmarkReport.MeasurementRow::implementation));
                        for (int index = 0; index < rankedRows.size(); index++) {
                            PerformanceBenchmarkReport.MeasurementRow row = rankedRows.get(index);
                            builder.append("| ").append(index + 1)
                                    .append(" | ").append(row.implementation())
                                    .append(" | ").append(formatMarkdownDouble(row.score()))
                                    .append(" | ").append(formatMarkdownDouble(row.scoreError()))
                                    .append(" | ").append(row.samples())
                                    .append(" | ").append(row.unit())
                                    .append(" |\n");
                        }
                    }
                }
            }
        }

        return builder.toString();
    }

    private static List<PerformanceBenchmarkReport.MeasurementRow> sortRows(List<PerformanceBenchmarkReport.MeasurementRow> rows) {
        List<PerformanceBenchmarkReport.MeasurementRow> sortedRows = new ArrayList<>(rows);
        sortedRows.sort(Comparator.comparing(PerformanceBenchmarkReport.MeasurementRow::interfaceName)
                .thenComparing(PerformanceBenchmarkReport.MeasurementRow::variant)
                .thenComparing(PerformanceBenchmarkReport.MeasurementRow::operation)
                .thenComparingInt(PerformanceBenchmarkReport.MeasurementRow::size)
                .thenComparing(PerformanceBenchmarkReport.MeasurementRow::implementation));
        return sortedRows;
    }

    private static String quoted(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private static String formatCsvDouble(double value) {
        return Double.isNaN(value) ? "" : String.format(Locale.ROOT, "%.6f", value);
    }

    private static String formatMarkdownDouble(double value) {
        return Double.isNaN(value) ? "-" : String.format(Locale.ROOT, "%.3f", value);
    }
}
