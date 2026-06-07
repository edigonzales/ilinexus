package guru.interlis.transformer.dmav;

import java.util.List;

public record MappingCandidate(
        String id,
        Direction direction,
        String sourceClass,
        String sourceAttribute,
        String targetClass,
        String targetAttribute,
        String expression,
        String transformCode,
        double confidence,
        String classification,
        String origin,
        List<String> warnings
) {
    public MappingCandidate {
        warnings = warnings != null ? List.copyOf(warnings) : List.of();
    }

    public String key() {
        return sourceClass + "::" + sourceAttribute + "::" + targetClass + "::" + targetAttribute;
    }
}
