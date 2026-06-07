package guru.interlis.transformer.dmav;

import java.util.List;

public record CorrelationHint(
        int rowNumber,
        String sheetName,
        String cellPosition,
        Direction direction,
        String sourceTopic,
        String sourceClass,
        String sourceAttribute,
        String targetTopic,
        String targetClass,
        String targetAttribute,
        String targetPath,
        String conditionText,
        String transformCode,
        String additionText,
        String comment,
        double confidence,
        List<String> warnings
) {
    public CorrelationHint {
        warnings = warnings != null ? List.copyOf(warnings) : List.of();
    }
}
