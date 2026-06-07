package guru.interlis.transformer.state;

public enum BasketStrategy {
    PRESERVE,
    GENERATE_UUID,
    PRESERVE_OR_GENERATE_UUID,
    BY_TOPIC,
    EXPRESSION;

    public static BasketStrategy fromString(String value) {
        if (value == null || value.isBlank()) return PRESERVE;
        return switch (value.trim().toLowerCase()) {
            case "preserve" -> PRESERVE;
            case "generateuuid", "generate_uuid" -> GENERATE_UUID;
            case "preserveorgenerateuuid", "preserve_or_generate_uuid" -> PRESERVE_OR_GENERATE_UUID;
            case "bytopic", "by_topic" -> BY_TOPIC;
            case "expression" -> EXPRESSION;
            default -> throw new IllegalArgumentException("Unknown basket strategy: " + value);
        };
    }
}
