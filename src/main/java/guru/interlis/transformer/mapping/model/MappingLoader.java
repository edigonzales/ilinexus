package guru.interlis.transformer.mapping.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class MappingLoader {

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public JobConfig load(Path path) throws IOException {
        JobConfig config = objectMapper.readValue(path.toFile(), JobConfig.class);
        normalize(config);
        return config;
    }

    /**
     * Normalizes backward-compatible fields into their canonical equivalents.
     * After normalization, consumers should only read the new fields.
     */
    void normalize(JobConfig config) {
        if (config.mapping == null) {
            config.mapping = new JobConfig.MappingSection();
        }
        if (config.job == null) {
            config.job = new JobConfig.JobSection();
        }
        if (config.mapping.oidStrategy == null) {
            config.mapping.oidStrategy = new JobConfig.OidStrategySpec();
        }
        if (config.mapping.basketStrategy == null) {
            config.mapping.basketStrategy = new JobConfig.BasketStrategySpec();
        }

        for (JobConfig.RuleSpec rule : config.mapping.rules) {
            if (rule.sources == null) {
                rule.sources = new java.util.ArrayList<>();
            }
            // Normalize flat targetClass/output into nested target
            if (rule.target == null) {
                if ((rule.targetClass != null && !rule.targetClass.isBlank())
                        || (rule.output != null && !rule.output.isBlank())) {
                    rule.target = new JobConfig.TargetSpec();
                    rule.target.clazz = rule.targetClass;
                    rule.target.output = rule.output;
                }
            }
            // Normalize single input string into inputs list
            for (JobConfig.SourceSpec source : rule.sources) {
                if (source.getInputIds().size() == 1
                        && (source.inputs == null || source.inputs.isEmpty())
                        && source.input != null && !source.input.isBlank()) {
                    source.inputs = List.of(source.input);
                }
            }
            // Normalize old-style refs (target/expr) into new-style if new fields are missing
            if (rule.refs != null) {
                for (JobConfig.RefMapping ref : rule.refs) {
                    if ((ref.association == null || ref.association.isBlank())
                            && ref.target != null && ref.expr != null) {
                        // Cannot auto-convert old ref('alias','role') syntax to
                        // new association/role/sourceRef format. Keep as-is; engine handles both.
                    }
                }
            }
        }
    }
}
