package io.github.alexoooo.vibe.data.benchmark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class PerformanceBenchmarkReport {

    private static final String[] EXPECTED_RESULT_SUFFIXES = {
        "double-object-persistent-sorted-map.csv",
        "long-object-persistent-map.csv",
        "persistent-ordered-queue.csv",
        "persistent-append-sequence.csv",
        "persistent-vector.csv"
    };
    private static final DateTimeFormatter FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH-mm-ss'Z'", Locale.ROOT).withZone(ZoneOffset.UTC);

    public static void main(String[] args) throws IOException {
        Instant generatedAt = Instant.now();
        Path resultsDirectory = resolveResultsDirectory();
        Files.createDirectories(resultsDirectory);

        List<Path> sourceCsvPaths = args.length == 0 ? resolveLatestCsvs(resultsDirectory) : resolveArgs(args);
        List<String> sourceCsvFileNames = new ArrayList<>(sourceCsvPaths.size());
        List<MeasurementRow> rows = new ArrayList<>();
        for (Path sourceCsvPath : sourceCsvPaths) {
            String sourceCsvFileName = sourceCsvPath.getFileName().toString();
            sourceCsvFileNames.add(sourceCsvFileName);
            rows.addAll(parseCsv(sourceCsvFileName, Files.readString(sourceCsvPath, StandardCharsets.UTF_8)));
        }

        RunMetadata metadata = new RunMetadata(
                generatedAt,
                System.getProperty("java.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.vendor"),
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                List.copyOf(sourceCsvFileNames));

        String timestamp = FILE_TIMESTAMP.format(generatedAt);
        String summaryCsvFileName = timestamp + "-performance-benchmark-summary.csv";
        Path summaryCsvPath = resultsDirectory.resolve(summaryCsvFileName);
        Path markdownPath = resultsDirectory.resolve("performance-benchmark-summary.md");

        Files.writeString(summaryCsvPath, PerformanceBenchmarkReportFormatter.toCsv(rows), StandardCharsets.UTF_8);
        Files.writeString(
                markdownPath,
                PerformanceBenchmarkReportFormatter.toMarkdown(metadata, summaryCsvFileName, rows),
                StandardCharsets.UTF_8);

        System.out.println("Wrote performance benchmark summary CSV to " + summaryCsvPath);
        System.out.println("Wrote performance benchmark summary to " + markdownPath);
    }

    static record MeasurementRow(
            String interfaceName,
            String variant,
            String operation,
            String implementation,
            int size,
            int samples,
            double score,
            double scoreError,
            String unit,
            String sourceCsvFileName) {
    }

    static record RunMetadata(
            Instant generatedAtUtc,
            String javaVersion,
            String javaVmName,
            String javaVmVendor,
            String osName,
            String osVersion,
            List<String> sourceCsvFileNames) {
    }

    static List<MeasurementRow> parseCsv(String sourceCsvFileName, String csv) {
        List<String> lines = csv.lines().filter(line -> !line.isBlank()).toList();
        if (lines.isEmpty()) {
            return List.of();
        }

        List<String> headers = parseCsvLine(lines.get(0));
        List<MeasurementRow> rows = new ArrayList<>();
        for (int index = 1; index < lines.size(); index++) {
            List<String> cells = parseCsvLine(lines.get(index));
            if (cells.size() != headers.size()) {
                throw new IllegalArgumentException("Unexpected CSV column count in " + sourceCsvFileName + " line " + (index + 1));
            }

            Map<String, String> fields = new LinkedHashMap<>();
            for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
                fields.put(headers.get(headerIndex), cells.get(headerIndex));
            }

            String benchmark = requiredField(fields, "Benchmark");
            int lastDot = benchmark.lastIndexOf('.');
            if (lastDot < 0) {
                throw new IllegalArgumentException("Unexpected benchmark name: " + benchmark);
            }

            String benchmarkClassName = benchmark.substring(0, lastDot);
            String operation = benchmark.substring(lastDot + 1);
            String benchmarkClassSimpleName = benchmarkClassName.substring(benchmarkClassName.lastIndexOf('.') + 1);

            rows.add(new MeasurementRow(
                    interfaceNameForBenchmarkClass(benchmarkClassSimpleName),
                    fields.getOrDefault("Param: order", ""),
                    operation,
                    requiredField(fields, "Param: implementation"),
                    Integer.parseInt(requiredField(fields, "Param: size")),
                    Integer.parseInt(requiredField(fields, "Samples")),
                    Double.parseDouble(requiredField(fields, "Score")),
                    parseOptionalDouble(fields.getOrDefault("Score Error (99.9%)", "")),
                    requiredField(fields, "Unit"),
                    sourceCsvFileName));
        }

        return rows;
    }

    private static List<Path> resolveArgs(String[] args) {
        List<Path> paths = new ArrayList<>(args.length);
        for (String arg : args) {
            paths.add(Path.of(arg).toAbsolutePath().normalize());
        }
        return paths;
    }

    private static List<Path> resolveLatestCsvs(Path resultsDirectory) throws IOException {
        List<Path> resultFiles = new ArrayList<>();
        try (var stream = Files.list(resultsDirectory)) {
            List<Path> files = stream.filter(Files::isRegularFile).toList();
            for (String suffix : EXPECTED_RESULT_SUFFIXES) {
                Path latest = files.stream()
                        .filter(path -> path.getFileName().toString().endsWith(suffix))
                        .max(Comparator.comparing(path -> path.getFileName().toString()))
                        .orElseThrow(() -> new IllegalStateException("Missing benchmark result file with suffix: " + suffix));
                resultFiles.add(latest);
            }
        }
        return resultFiles;
    }

    private static String interfaceNameForBenchmarkClass(String benchmarkClassSimpleName) {
        return switch (benchmarkClassSimpleName) {
            case "DoubleObjectPersistentSortedMapBenchmark" -> "DoubleObjectPersistentSortedMap";
            case "LongObjectPersistentMapBenchmark" -> "LongObjectPersistentMap";
            case "PersistentOrderedQueueBenchmark" -> "PersistentOrderedQueue";
            case "PersistentAppendSequenceBenchmark" -> "PersistentAppendSequence";
            case "PersistentVectorBenchmark" -> "PersistentVector";
            default -> throw new IllegalArgumentException("Unknown benchmark class: " + benchmarkClassSimpleName);
        };
    }

    private static String requiredField(Map<String, String> fields, String fieldName) {
        String value = fields.get(fieldName);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required CSV field: " + fieldName);
        }
        return value;
    }

    private static double parseOptionalDouble(String value) {
        return value == null || value.isBlank() ? Double.NaN : Double.parseDouble(value);
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }

    private static Path resolveResultsDirectory() {
        Path current = Path.of("").toAbsolutePath().normalize();
        for (Path candidate = current; candidate != null; candidate = candidate.getParent()) {
            Path moduleResults = candidate.resolve("src").resolve("main").resolve("resources").resolve("results");
            if (Files.isDirectory(moduleResults) && Files.exists(candidate.resolve("pom.xml"))) {
                return moduleResults;
            }

            Path repoResults = candidate.resolve("benchmarks")
                    .resolve("src")
                    .resolve("main")
                    .resolve("resources")
                    .resolve("results");
            if (Files.isDirectory(repoResults)) {
                return repoResults;
            }
        }

        return current.resolve("benchmarks").resolve("src").resolve("main").resolve("resources").resolve("results");
    }
}
