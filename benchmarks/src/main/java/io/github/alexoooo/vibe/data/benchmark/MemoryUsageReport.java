package io.github.alexoooo.vibe.data.benchmark;

import io.github.alexoooo.vibe.data.DoubleObjectPersistentSortedMap;
import io.github.alexoooo.vibe.data.LongObjectPersistentMap;
import io.github.alexoooo.vibe.data.PersistentAppendSequence;
import io.github.alexoooo.vibe.data.PersistentOrderedQueue;
import io.github.alexoooo.vibe.data.PersistentVector;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

public final class MemoryUsageReport {

    private static final String[] APPEND_SEQUENCE_IMPLEMENTATIONS =
            {"simple", "chunkedVector", "chunkedAppend", "bifurcan", "dexx"};
    private static final String[] LONG_MAP_IMPLEMENTATIONS =
            {"simple", "hamt", "compact", "dexx", "bifurcan", "bifurcanMap"};
    private static final String[] ORDERED_QUEUE_IMPLEMENTATIONS = {"simple", "treap", "compact", "dexx", "bifurcan"};
    private static final String[] SORTED_MAP_IMPLEMENTATIONS =
            {"simple", "treap", "compact", "dexx", "bifurcanSorted", "bifurcanFloat"};
    private static final String[] SORT_ORDERS = {"ascending", "descending"};
    private static final String[] VECTOR_IMPLEMENTATIONS = {"simple", "chunked", "bifurcan", "dexx"};
    private static final int[] SIZES = {0, 128, 4096};
    private static final DateTimeFormatter FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH-mm-ss'Z'", Locale.ROOT).withZone(ZoneOffset.UTC);
    private static final long PAYLOAD_BYTES_PER_ELEMENT = GraphLayout.parseInstance(new BenchmarkValue(0)).totalSize();
    private static final long PAYLOAD_OBJECTS_PER_ELEMENT = GraphLayout.parseInstance(new BenchmarkValue(0)).totalCount();

    public static void main(String[] args) throws IOException {
        Instant generatedAt = Instant.now();
        RunMetadata metadata = new RunMetadata(
                generatedAt,
                System.getProperty("java.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.vendor"),
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                VM.current().details(),
                PAYLOAD_BYTES_PER_ELEMENT);

        List<MeasurementRow> rows = measureAll(SIZES);
        Path resultsDirectory = resolveResultsDirectory();
        Files.createDirectories(resultsDirectory);

        String timestamp = FILE_TIMESTAMP.format(generatedAt);
        String csvFileName = timestamp + "-memory-usage.csv";
        Path csvPath = resultsDirectory.resolve(csvFileName);
        Path markdownPath = resultsDirectory.resolve("memory-usage-summary.md");

        Files.writeString(csvPath, MemoryUsageReportFormatter.toCsv(rows), StandardCharsets.UTF_8);
        Files.writeString(
                markdownPath,
                MemoryUsageReportFormatter.toMarkdown(metadata, csvFileName, rows),
                StandardCharsets.UTF_8);

        System.out.println("Wrote memory usage CSV to " + csvPath);
        System.out.println("Wrote memory usage summary to " + markdownPath);
    }

    static record MeasurementRow(
            String interfaceName,
            String variant,
            String implementation,
            int size,
            long retainedBytes,
            long structuralBytes,
            double retainedBytesPerElement,
            double structuralBytesPerElement,
            long objectCount,
            long structuralObjectCount) {
    }

    static record RunMetadata(
            Instant generatedAtUtc,
            String javaVersion,
            String javaVmName,
            String javaVmVendor,
            String osName,
            String osVersion,
            String vmDetails,
            long payloadBytesPerElement) {
    }

    static List<MeasurementRow> measureAll() {
        return measureAll(SIZES);
    }

    static List<MeasurementRow> measureAll(int[] sizes) {
        List<MeasurementRow> rows = new ArrayList<>();
        measureSortedMaps(rows, sizes);
        measureLongMaps(rows, sizes);
        measureOrderedQueues(rows, sizes);
        measureAppendSequences(rows, sizes);
        measureVectors(rows, sizes);
        return rows;
    }

    private static void measureSortedMaps(List<MeasurementRow> rows, int[] sizes) {
        for (String order : SORT_ORDERS) {
            for (String implementation : SORTED_MAP_IMPLEMENTATIONS) {
                for (int size : sizes) {
                    rows.add(measure(
                            "DoubleObjectPersistentSortedMap",
                            order,
                            implementation,
                            size,
                            retainSortedMapHistory(implementation, order, size)));
                }
            }
        }
    }

    private static void measureLongMaps(List<MeasurementRow> rows, int[] sizes) {
        for (String implementation : LONG_MAP_IMPLEMENTATIONS) {
            for (int size : sizes) {
                rows.add(measure(
                        "LongObjectPersistentMap",
                        "",
                        implementation,
                        size,
                        retainLongMapHistory(implementation, size)));
            }
        }
    }

    private static void measureOrderedQueues(List<MeasurementRow> rows, int[] sizes) {
        for (String implementation : ORDERED_QUEUE_IMPLEMENTATIONS) {
            for (int size : sizes) {
                rows.add(measure(
                        "PersistentOrderedQueue",
                        "",
                        implementation,
                        size,
                        retainOrderedQueueHistory(implementation, size)));
            }
        }
    }

    private static void measureAppendSequences(List<MeasurementRow> rows, int[] sizes) {
        for (String implementation : APPEND_SEQUENCE_IMPLEMENTATIONS) {
            for (int size : sizes) {
                rows.add(measure(
                        "PersistentAppendSequence",
                        "",
                        implementation,
                        size,
                        retainAppendSequenceHistory(implementation, size)));
            }
        }
    }

    private static void measureVectors(List<MeasurementRow> rows, int[] sizes) {
        for (String implementation : VECTOR_IMPLEMENTATIONS) {
            for (int size : sizes) {
                rows.add(measure(
                        "PersistentVector",
                        "",
                        implementation,
                        size,
                        retainVectorHistory(implementation, size)));
            }
        }
    }

    private static MeasurementRow measure(
            String interfaceName,
            String variant,
            String implementation,
            int size,
            Object root) {
        GraphLayout layout = GraphLayout.parseInstance(root);
        long payloadBytes = PAYLOAD_BYTES_PER_ELEMENT * size;
        long payloadObjects = PAYLOAD_OBJECTS_PER_ELEMENT * size;
        long retainedBytes = layout.totalSize();
        long objectCount = layout.totalCount();
        long structuralBytes = Math.max(0L, retainedBytes - payloadBytes);
        long structuralObjectCount = Math.max(0L, objectCount - payloadObjects);
        double retainedBytesPerElement = size == 0 ? Double.NaN : (double) retainedBytes / size;
        double structuralBytesPerElement = size == 0 ? Double.NaN : (double) structuralBytes / size;
        return new MeasurementRow(
                interfaceName,
                variant,
                implementation,
                size,
                retainedBytes,
                structuralBytes,
                retainedBytesPerElement,
                structuralBytesPerElement,
                objectCount,
                structuralObjectCount);
    }

    private static Object retainSortedMapHistory(String implementation, String order, int size) {
        List<Object> snapshots = new ArrayList<>(size + 1);
        DoubleObjectPersistentSortedMap<BenchmarkValue> map =
                BenchmarkFixtures.createDoubleObjectPersistentSortedMap(implementation, order);
        snapshots.add(map);
        for (int index = 0; index < size; index++) {
            map = map.put(index, new BenchmarkValue(index));
            snapshots.add(map);
        }
        return new RetainedHistory(snapshots.toArray());
    }

    private static Object retainLongMapHistory(String implementation, int size) {
        List<Object> snapshots = new ArrayList<>(size + 1);
        LongObjectPersistentMap<BenchmarkValue> map =
                BenchmarkFixtures.createLongObjectPersistentMap(implementation);
        snapshots.add(map);
        for (int index = 0; index < size; index++) {
            map = map.put(index, new BenchmarkValue(index));
            snapshots.add(map);
        }
        return new RetainedHistory(snapshots.toArray());
    }

    private static Object retainOrderedQueueHistory(String implementation, int size) {
        List<Object> snapshots = new ArrayList<>(size + 1);
        PersistentOrderedQueue<BenchmarkValue> queue =
                BenchmarkFixtures.createPersistentOrderedQueue(implementation);
        snapshots.add(queue);
        for (int index = 0; index < size; index++) {
            queue = queue.add(new BenchmarkValue(index));
            snapshots.add(queue);
        }
        return new RetainedHistory(snapshots.toArray());
    }

    private static Object retainAppendSequenceHistory(String implementation, int size) {
        List<Object> snapshots = new ArrayList<>(size + 1);
        PersistentAppendSequence<BenchmarkValue> sequence =
                BenchmarkFixtures.createPersistentAppendSequence(implementation);
        snapshots.add(sequence);
        for (int index = 0; index < size; index++) {
            sequence = sequence.append(new BenchmarkValue(index));
            snapshots.add(sequence);
        }
        return new RetainedHistory(snapshots.toArray());
    }

    private static Object retainVectorHistory(String implementation, int size) {
        List<Object> snapshots = new ArrayList<>(size + 1);
        PersistentVector<BenchmarkValue> vector =
                BenchmarkFixtures.createPersistentVector(implementation);
        snapshots.add(vector);
        for (int index = 0; index < size; index++) {
            vector = vector.append(new BenchmarkValue(index));
            snapshots.add(vector);
        }
        return new RetainedHistory(snapshots.toArray());
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

    private static final class BenchmarkValue implements Comparable<BenchmarkValue> {

        private final int id;

        private BenchmarkValue(int id) {
            this.id = id;
        }

        @Override
        public int compareTo(BenchmarkValue other) {
            return Integer.compare(id, other.id);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    private static final class RetainedHistory {

        private final Object[] snapshots;

        private RetainedHistory(Object[] snapshots) {
            this.snapshots = snapshots;
        }
    }
}
