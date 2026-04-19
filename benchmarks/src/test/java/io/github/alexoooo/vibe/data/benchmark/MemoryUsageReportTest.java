package io.github.alexoooo.vibe.data.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class MemoryUsageReportTest {

    @Test
    void measuresRealRowsForAllConfiguredImplementations() {
        List<MemoryUsageReport.MeasurementRow> rows = MemoryUsageReport.measureAll();

        assertEquals(84, rows.size());
        assertFalse(rows.isEmpty());
        assertEquals(
                Set.of(
                        "DoubleObjectPersistentSortedMap",
                        "LongObjectPersistentMap",
                        "PersistentOrderedQueue",
                        "PersistentAppendSequence",
                        "PersistentVector"),
                rows.stream()
                        .map(MemoryUsageReport.MeasurementRow::interfaceName)
                        .collect(Collectors.toSet()));
        assertTrue(rows.stream().allMatch(row -> row.retainedBytes() >= row.structuralBytes()));
        assertTrue(rows.stream().allMatch(row -> row.objectCount() >= row.structuralObjectCount()));
        assertTrue(rows.stream().allMatch(row -> row.retainedBytes() >= 0));
    }
}
