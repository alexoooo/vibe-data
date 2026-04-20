package io.github.alexoooo.vibe.data.benchmark;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class MemoryUsageReportFormatter {

    private static final DateTimeFormatter DISPLAY_TIMESTAMP =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    private MemoryUsageReportFormatter() {
    }

    static String toCsv(List<MemoryUsageReport.MeasurementRow> rows) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"Interface\",\"Variant\",\"Implementation\",\"Size\",\"Retained Bytes\",")
                .append("\"Structural Bytes\",\"Retained Bytes/Element\",\"Structural Bytes/Element\",")
                .append("\"Object Count\",\"Structural Object Count\"\n");

        for (MemoryUsageReport.MeasurementRow row : sortRows(rows)) {
            builder.append(quoted(row.interfaceName())).append(',')
                    .append(quoted(row.variant())).append(',')
                    .append(quoted(row.implementation())).append(',')
                    .append(row.size()).append(',')
                    .append(row.retainedBytes()).append(',')
                    .append(row.structuralBytes()).append(',')
                    .append(formatCsvDouble(row.retainedBytesPerElement())).append(',')
                    .append(formatCsvDouble(row.structuralBytesPerElement())).append(',')
                    .append(row.objectCount()).append(',')
                    .append(row.structuralObjectCount()).append('\n');
        }

        return builder.toString();
    }

    static String toMarkdown(
            MemoryUsageReport.RunMetadata metadata,
            String csvFileName,
            List<MemoryUsageReport.MeasurementRow> rows) {
        List<MemoryUsageReport.MeasurementRow> sortedRows = sortRows(rows);
        StringBuilder builder = new StringBuilder();
        builder.append("# Memory usage summary\n\n")
                .append("- Generated: `").append(DISPLAY_TIMESTAMP.format(metadata.generatedAtUtc())).append("`\n")
                .append("- Source CSV: [`").append(csvFileName).append("`](./").append(csvFileName).append(")\n")
                .append("- Java: `").append(metadata.javaVersion()).append("` (`")
                .append(metadata.javaVmName()).append("`, `").append(metadata.javaVmVendor()).append("`)\n")
                .append("- OS: `").append(metadata.osName()).append(' ').append(metadata.osVersion()).append("`\n")
                .append("- Measured sizes: `0`, `128`, `4096`\n")
                .append("- Retention model: all versions from the build sequence are retained (`empty`, then every mutation through the final size)\n")
                .append("- Payload model: one small comparable payload object per logical element; ")
                .append("`structural bytes` subtract that payload cost from the retained graph to isolate collection overhead more directly.\n")
                .append("- Payload bytes per element: `").append(metadata.payloadBytesPerElement()).append("`\n\n")
                .append("## Methodology\n\n")
                .append("- Retained footprint is measured from a retained-history container holding the empty version and every subsequent mutation snapshot with JOL `GraphLayout`.\n")
                .append("- Sorted maps are measured in both `ascending` and `descending` order configurations.\n")
                .append("- `Retained bytes` is the full reachable graph size of the retained history; `structural bytes` subtract the unique per-element payload objects while keeping collection nodes, arrays, roots, and references.\n\n")
                .append("## JVM layout details\n\n```text\n")
                .append(metadata.vmDetails().strip())
                .append("\n```\n");

        Map<String, List<MemoryUsageReport.MeasurementRow>> byInterface = new LinkedHashMap<>();
        for (MemoryUsageReport.MeasurementRow row : sortedRows) {
            byInterface.computeIfAbsent(row.interfaceName(), ignored -> new ArrayList<>()).add(row);
        }

        for (Map.Entry<String, List<MemoryUsageReport.MeasurementRow>> interfaceEntry : byInterface.entrySet()) {
            builder.append("\n## ").append(interfaceEntry.getKey()).append("\n");

            Map<String, List<MemoryUsageReport.MeasurementRow>> byVariant = new LinkedHashMap<>();
            for (MemoryUsageReport.MeasurementRow row : interfaceEntry.getValue()) {
                byVariant.computeIfAbsent(row.variant(), ignored -> new ArrayList<>()).add(row);
            }

            boolean showVariantHeader = byVariant.size() > 1 || !byVariant.containsKey("");
            for (Map.Entry<String, List<MemoryUsageReport.MeasurementRow>> variantEntry : byVariant.entrySet()) {
                if (showVariantHeader) {
                    builder.append("\n### ")
                            .append(variantEntry.getKey().isBlank() ? "default" : variantEntry.getKey())
                            .append('\n');
                }

                Map<Integer, List<MemoryUsageReport.MeasurementRow>> bySize = new LinkedHashMap<>();
                for (MemoryUsageReport.MeasurementRow row : variantEntry.getValue()) {
                    bySize.computeIfAbsent(row.size(), ignored -> new ArrayList<>()).add(row);
                }

                for (Map.Entry<Integer, List<MemoryUsageReport.MeasurementRow>> sizeEntry : bySize.entrySet()) {
                    builder.append("\n#### size = ").append(sizeEntry.getKey()).append("\n\n")
                            .append("| Rank | Implementation | Retained bytes | Structural bytes | Retained bytes/element | Structural bytes/element | Objects |\n")
                            .append("| --- | --- | ---: | ---: | ---: | ---: | ---: |\n");

                    List<MemoryUsageReport.MeasurementRow> rankedRows = new ArrayList<>(sizeEntry.getValue());
                    rankedRows.sort(Comparator.comparingLong(MemoryUsageReport.MeasurementRow::retainedBytes)
                            .thenComparing(MemoryUsageReport.MeasurementRow::implementation));
                    for (int index = 0; index < rankedRows.size(); index++) {
                        MemoryUsageReport.MeasurementRow row = rankedRows.get(index);
                        builder.append("| ").append(index + 1)
                                .append(" | ").append(row.implementation())
                                .append(" | ").append(row.retainedBytes())
                                .append(" | ").append(row.structuralBytes())
                                .append(" | ").append(formatMarkdownDouble(row.retainedBytesPerElement()))
                                .append(" | ").append(formatMarkdownDouble(row.structuralBytesPerElement()))
                                .append(" | ").append(row.objectCount())
                                .append(" |\n");
                    }
                }
            }
        }

        return builder.toString();
    }

    private static List<MemoryUsageReport.MeasurementRow> sortRows(List<MemoryUsageReport.MeasurementRow> rows) {
        List<MemoryUsageReport.MeasurementRow> sortedRows = new ArrayList<>(rows);
        sortedRows.sort(Comparator.comparing(MemoryUsageReport.MeasurementRow::interfaceName)
                .thenComparing(MemoryUsageReport.MeasurementRow::variant)
                .thenComparingInt(MemoryUsageReport.MeasurementRow::size)
                .thenComparing(MemoryUsageReport.MeasurementRow::implementation));
        return sortedRows;
    }

    private static String quoted(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private static String formatCsvDouble(double value) {
        return Double.isNaN(value) ? "" : String.format(Locale.ROOT, "%.3f", value);
    }

    private static String formatMarkdownDouble(double value) {
        return Double.isNaN(value) ? "-" : String.format(Locale.ROOT, "%.3f", value);
    }
}
