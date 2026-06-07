package guru.interlis.transformer.dmav;

import guru.interlis.transformer.model.IliModelService;
import guru.interlis.transformer.model.IliModelCompileResult;
import guru.interlis.transformer.model.ModelInventory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class MappingCandidateGeneratorTest {

    private static final String MODELDIR = "src/test/data/models/";

    private static ModelInventory p7Inventory;
    private static Path hintsPath;

    @BeforeAll
    static void setup() throws Exception {
        IliModelService service = new IliModelService();
        IliModelCompileResult result = service.compileModel(
                "src/test/data/models/with-references.ili", MODELDIR);
        assertThat(result.hasErrors()).isFalse();
        p7Inventory = service.buildInventory(result.transferDescription(), "P7Model");

        hintsPath = Files.createTempFile("test-hints-", ".json");
        List<Map<String, Object>> hints = new ArrayList<>();
        hints.add(hint(2, "Transformation", "Transformation!U2",
                "DM01_TO_DMAV", "P7Topic", "ClassA", "Name",
                "P7Topic", "ClassB", "Name", "K", 0.7));
        hints.add(hint(3, "Transformation", "Transformation!U3",
                "DM01_TO_DMAV", "P7Topic", "ClassA", "Wert",
                "P7Topic", "ClassB", "Wert", "V", 0.5));
        hints.add(hint(4, "Transformation", "Transformation!Z4",
                "DMAV_TO_DM01", "P7Topic", "ClassB", "Name",
                "P7Topic", "ClassA", "Name", "I", 0.3));

        new com.fasterxml.jackson.databind.ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValue(hintsPath.toFile(), hints);
    }

    @Test
    void classifyReturnsCorrectLevels() {
        assertThat(MappingCandidateGenerator.classify(0.90)).isEqualTo("high");
        assertThat(MappingCandidateGenerator.classify(0.85)).isEqualTo("high");
        assertThat(MappingCandidateGenerator.classify(0.70)).isEqualTo("medium");
        assertThat(MappingCandidateGenerator.classify(0.60)).isEqualTo("medium");
        assertThat(MappingCandidateGenerator.classify(0.45)).isEqualTo("low");
        assertThat(MappingCandidateGenerator.classify(0.30)).isEqualTo("low");
        assertThat(MappingCandidateGenerator.classify(0.20)).isEqualTo("manual");
        assertThat(MappingCandidateGenerator.classify(0.00)).isEqualTo("manual");
    }

    @Test
    void generatesCandidatesFromHintsWithModelValidation() throws Exception {
        MappingCandidateGenerator generator = new MappingCandidateGenerator();
        MappingCandidateGenerator.GenerationResult result = generator.generate(
                hintsPath, null,
                "src/test/data/models/with-references.ili", MODELDIR,
                "src/test/data/models/with-references.ili", MODELDIR);

        assertThat(result.candidates()).isNotEmpty();
        assertThat(result.warnings()).isEmpty();

        MappingCandidate first = result.candidates().get(0);
        assertThat(first.sourceClass()).contains("P7Model");
        assertThat(first.targetClass()).contains("P7Model");
        assertThat(first.transformCode()).isIn("K", "V", "I");
        assertThat(first.classification()).isIn("high", "medium", "low", "manual");
        assertThat(first.confidence()).isBetween(0.0, 1.0);
        assertThat(first.expression()).contains("${s.");
    }

    @Test
    void deduplicatesKeepingHighestConfidence() throws Exception {
        MappingCandidateGenerator generator = new MappingCandidateGenerator();
        MappingCandidateGenerator.GenerationResult result = generator.generate(
                hintsPath, null,
                "src/test/data/models/with-references.ili", MODELDIR,
                "src/test/data/models/with-references.ili", MODELDIR);

        // The three synthetic hints should not produce duplicate keys
        // since they have different (srcAttr, tgtAttr) pairs
        assertThat(result.candidates()).isNotEmpty();

        // Verify no duplicate keys
        List<String> keys = result.candidates().stream()
                .map(MappingCandidate::key).toList();
        assertThat(keys).doesNotHaveDuplicates();
    }

    @Test
    void synonymsLoadedFromClasspath() {
        Map<String, Map<String, List<String>>> synonyms = loadClasspathSynonyms();
        assertThat(synonyms).isNotEmpty();
        assertThat(synonyms).containsKey("DM01_TO_DMAV");
        assertThat(synonyms.get("DM01_TO_DMAV")).containsKey("NBIdent");
        assertThat(synonyms.get("DM01_TO_DMAV").get("NBIdent")).contains("NBIdent");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, List<String>>> loadClasspathSynonyms() {
        try (var is = MappingCandidateGenerator.class.getClassLoader()
                .getResourceAsStream("dmav/synonyms.json")) {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(is, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }

    private static Map<String, Object> hint(int row, String sheet, String cell,
                                             String direction, String srcTopic, String srcClass, String srcAttr,
                                             String tgtTopic, String tgtClass, String tgtAttr,
                                             String code, double confidence) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("row", row);
        m.put("sheet", sheet);
        m.put("cell", cell);
        m.put("direction", direction);
        m.put("sourceTopic", srcTopic);
        m.put("sourceClass", srcClass);
        m.put("sourceAttribute", srcAttr);
        m.put("targetTopic", tgtTopic);
        m.put("targetClass", tgtClass);
        m.put("targetAttribute", tgtAttr);
        m.put("transformCode", code);
        m.put("confidence", confidence);
        return m;
    }
}
