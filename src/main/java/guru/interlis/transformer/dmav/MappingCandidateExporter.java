package guru.interlis.transformer.dmav;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import guru.interlis.transformer.mapping.model.JobConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MappingCandidateExporter {

    private static final ObjectMapper JSON = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    public void writeJson(List<MappingCandidate> candidates, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        List<Map<String, Object>> data = candidates.stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        JSON.writeValue(path.toFile(), data);
    }

    public void writeReport(MappingCandidateGenerator.GenerationResult result,
                             Path path) throws IOException {
        Files.createDirectories(path.getParent());
        StringBuilder md = new StringBuilder();
        var candidates = result.candidates();

        md.append("# Mapping Candidate Generation Report\n\n");
        md.append("## Summary\n\n");
        md.append("| Metric | Count |\n");
        md.append("|---|---|\n");
        md.append("| Total candidates | ").append(candidates.size()).append(" |\n");
        md.append("| DM01→DMAV | ").append(result.dm01ToDmavCount()).append(" |\n");
        md.append("| DMAV→DM01 | ").append(result.dmavToDm01Count()).append(" |\n");

        md.append("\n## By Classification\n\n");
        md.append("| Classification | Count |\n");
        md.append("|---|---|\n");
        md.append("| high (≥0.85) | ").append(result.highCount()).append(" |\n");
        md.append("| medium (0.60-0.84) | ").append(result.mediumCount()).append(" |\n");
        md.append("| low (0.30-0.59) | ").append(result.lowCount()).append(" |\n");
        md.append("| manual (<0.30) | ").append(result.manualCount()).append(" |\n");

        md.append("\n## Warnings\n\n");
        if (result.warnings().isEmpty()) {
            md.append("None.\n");
        } else {
            for (String w : result.warnings()) {
                md.append("- ").append(w).append("\n");
            }
        }

        Files.writeString(path, md.toString());
    }

    public void writeYaml(List<MappingCandidate> candidates, Direction direction,
                           String dm01Model, String dmavModel, Path path) throws IOException {
        Files.createDirectories(path.getParent());

        var filtered = candidates.stream()
                .filter(c -> c.direction() == direction)
                .filter(c -> !"manual".equals(c.classification()))
                .collect(Collectors.toList());

        JobConfig config = buildJobConfig(filtered, direction, dm01Model, dmavModel);
        YAML.writeValue(path.toFile(), config);
    }

    private static final String LOOKUP_PREFIX = "LOOKUP|";

    private JobConfig buildJobConfig(List<MappingCandidate> candidates, Direction direction,
                                       String dm01Model, String dmavModel) {
        JobConfig config = new JobConfig();
        config.version = 1;
        config.job.direction = direction.name();
        config.job.failPolicy = "lenient";
        config.job.name = "generated-" + direction.name().toLowerCase();

        String sourceModel = direction == Direction.DM01_TO_DMAV ? dm01Model : dmavModel;
        String targetModel = direction == Direction.DM01_TO_DMAV ? dmavModel : dm01Model;

        config.job.inputs.add(buildInput("in1", sourceModel));
        config.job.outputs.add(buildOutput("out1", targetModel));

        // Split normal vs lookup candidates
        List<MappingCandidate> normal = new ArrayList<>();
        List<MappingCandidate> lookups = new ArrayList<>();
        for (MappingCandidate c : candidates) {
            if (c.expression() != null && c.expression().startsWith(LOOKUP_PREFIX)) {
                lookups.add(c);
            } else {
                normal.add(c);
            }
        }

        // Group normal candidates by target class
        Map<String, List<MappingCandidate>> byTarget = new LinkedHashMap<>();
        for (MappingCandidate c : normal) {
            byTarget.computeIfAbsent(c.targetClass(), k -> new ArrayList<>()).add(c);
        }

        // Build source aliases from normal candidates only
        Map<String, String> sourceAliases = new LinkedHashMap<>();
        int aliasIdx = 0;
        for (MappingCandidate c : normal) {
            sourceAliases.putIfAbsent(c.sourceClass(), "s" + (aliasIdx++));
        }

        int ruleIdx = 0;
        Map<String, JobConfig.RuleSpec> rulesByTarget = new LinkedHashMap<>();
        for (var entry : byTarget.entrySet()) {
            String targetClass = entry.getKey();
            List<MappingCandidate> group = entry.getValue();
            String firstSourceClass = group.get(0).sourceClass();
            String alias = sourceAliases.get(firstSourceClass);

            JobConfig.RuleSpec rule = new JobConfig.RuleSpec();
            rule.id = "rule-" + (ruleIdx++);
            rule.target = new JobConfig.TargetSpec();
            rule.target.output = "out1";
            rule.target.clazz = targetClass;

            JobConfig.SourceSpec src = new JobConfig.SourceSpec();
            src.alias = alias;
            src.clazz = firstSourceClass;
            src.inputs = List.of("in1");
            rule.sources.add(src);

            rule.assign = new LinkedHashMap<>();
            for (MappingCandidate c : group) {
                if ("high".equals(c.classification()) || "medium".equals(c.classification())) {
                    rule.assign.put(c.targetAttribute(), c.expression());
                }
            }

            config.mapping.rules.add(rule);
            rulesByTarget.put(targetClass, rule);
        }

        // Process lookup candidates as assignments into existing rules
        for (MappingCandidate lc : lookups) {
            JobConfig.RuleSpec rule = rulesByTarget.get(lc.targetClass());
            if (rule == null) {
                // No parent rule to attach to – skip lookup candidate
                continue;
            }
            String parentAlias = rule.sources.isEmpty() ? "s0" : rule.sources.get(0).alias;
            String[] parts = lc.expression().split("\\|", 4);
            if (parts.length < 4) continue;
            String childClass = parts[1];
            String refRole = parts[2];
            String childAttr = parts[3];

            String lookupExpr = "lookup('" + childClass + "', '" + refRole + "', oid(" + parentAlias + "), '" + childAttr + "')";

            if (rule.assign == null) {
                rule.assign = new LinkedHashMap<>();
            }
            rule.assign.put(lc.targetAttribute(), lookupExpr);
        }

        return config;
    }

    private JobConfig.InputSpec buildInput(String id, String model) {
        JobConfig.InputSpec s = new JobConfig.InputSpec();
        s.id = id;
        s.model = model;
        s.path = "input.xtf";
        return s;
    }

    private JobConfig.OutputSpec buildOutput(String id, String model) {
        JobConfig.OutputSpec s = new JobConfig.OutputSpec();
        s.id = id;
        s.model = model;
        s.path = "output.xtf";
        return s;
    }

    private Map<String, Object> toMap(MappingCandidate c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.id());
        map.put("direction", c.direction().name());
        map.put("sourceClass", c.sourceClass());
        map.put("sourceAttribute", c.sourceAttribute());
        map.put("targetClass", c.targetClass());
        map.put("targetAttribute", c.targetAttribute());
        map.put("expression", c.expression());
        map.put("transformCode", c.transformCode());
        map.put("confidence", c.confidence());
        map.put("classification", c.classification());
        map.put("origin", c.origin());
        if (!c.warnings().isEmpty()) map.put("warnings", c.warnings());
        return map;
    }
}
