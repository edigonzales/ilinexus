package guru.interlis.transformer.state;

public enum OidStrategy {
    PRESERVE,
    INTEGER,
    UUID,
    DETERMINISTIC_UUID,
    EXTERNAL;

    public static OidStrategy fromString(String value) {
        if (value == null || value.isBlank()) return INTEGER;
        return switch (value.trim().toLowerCase()) {
            case "preserve" -> PRESERVE;
            case "integer" -> INTEGER;
            case "uuid" -> UUID;
            case "deterministicuuid", "deterministic_uuid" -> DETERMINISTIC_UUID;
            case "external" -> EXTERNAL;
            default -> throw new IllegalArgumentException("Unknown OID strategy: " + value);
        };
    }
}
