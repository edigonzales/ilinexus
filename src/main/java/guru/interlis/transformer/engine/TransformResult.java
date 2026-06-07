package guru.interlis.transformer.engine;

public record TransformResult(
        long sourceRecordsRead,
        long sourceRecordsFiltered,
        long targetsCreated,
        long targetsWritten,
        long errors,
        long warnings
) {
    public long targetsWritten() {
        return targetsWritten;
    }

    public String summary() {
        return String.format(
                "Transform summary: %d source records read, %d filtered, %d targets created, %d written (%d errors, %d warnings)",
                sourceRecordsRead, sourceRecordsFiltered, targetsCreated, targetsWritten, errors, warnings);
    }
}
