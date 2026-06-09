package guru.interlis.transformer.engine;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ExecutionMetrics {
    private long readStartMs;
    private long readEndMs;
    private long sourceRecordsRead;
    private long sourceRecordsFiltered;
    private long targetsCreated;
    private long targetsWritten;
    private long joinLookups;
    private long bagLookups;
    private long ruleMatches;
    private final Map<String, Long> targetsByClass = new LinkedHashMap<>();

    public void recordReadStart() {
        readStartMs = System.currentTimeMillis();
    }

    public void recordReadEnd(int recordCount) {
        readEndMs = System.currentTimeMillis();
        sourceRecordsRead = recordCount;
    }

    public void recordRuleMatch(String ruleId) {
        ruleMatches++;
    }

    public void recordJoinLookup() {
        joinLookups++;
    }

    public void recordBagLookup() {
        bagLookups++;
    }

    public void recordTarget(String targetClass) {
        targetsCreated++;
        targetsByClass.merge(targetClass, 1L, Long::sum);
    }

    public void recordFiltered() {
        sourceRecordsFiltered++;
    }

    public void recordWritten() {
        targetsWritten++;
    }

    public ExecutionMetricsSnapshot snapshot() {
        long elapsed = readEndMs > 0 ? readEndMs - readStartMs : 0;
        return new ExecutionMetricsSnapshot(
                sourceRecordsRead,
                sourceRecordsFiltered,
                targetsCreated,
                targetsWritten,
                joinLookups,
                bagLookups,
                ruleMatches,
                new LinkedHashMap<>(targetsByClass),
                elapsed);
    }

    public long getSourceRecordsRead() {
        return sourceRecordsRead;
    }

    public long getSourceRecordsFiltered() {
        return sourceRecordsFiltered;
    }

    public long getTargetsCreated() {
        return targetsCreated;
    }

    public long getTargetsWritten() {
        return targetsWritten;
    }

    public long getJoinLookups() {
        return joinLookups;
    }

    public long getBagLookups() {
        return bagLookups;
    }

    public long getRuleMatches() {
        return ruleMatches;
    }

    public Map<String, Long> getTargetsByClass() {
        return new LinkedHashMap<>(targetsByClass);
    }
}
