package guru.interlis.transformer.dmav;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.interlis.transformer.model.IliModelService;
import guru.interlis.transformer.model.IliModelCompileResult;
import guru.interlis.transformer.model.ModelInventory;
import guru.interlis.transformer.model.TypeSystemFacade;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class MappingCandidateGenerator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public GenerationResult generate(
            Path hintsPath,
            Path synonymsPath,
            String dm01ModelName,
            String dm01ModelDir,
            String dmavModelName,
            String dmavModelDir) throws Exception {

        List<MappingCandidate> candidates = new ArrayList<>();
        List<String> genWarnings = new ArrayList<>();

        // 1. Load hints
        List<CorrelationHint> hints = loadHints(hintsPath);

        // 2. Load synonyms
        Map<String, Map<String, List<String>>> synonyms = loadSynonyms(synonymsPath);

        // 3. Compile models and build inventories
        IliModelService modelService = new IliModelService();

        Map<String, String> dm01ClassPaths = new LinkedHashMap<>();
        Map<String, ModelInventory.AttributeInventory> dm01Attrs = new LinkedHashMap<>();
        if (dm01ModelName != null && !dm01ModelName.isBlank()) {
            IliModelCompileResult dm01Result = modelService.compileModel(dm01ModelName, dm01ModelDir);
            if (!dm01Result.hasErrors() && dm01Result.transferDescription() != null) {
                ModelInventory dm01Inv = modelService.buildInventory(
                        dm01Result.transferDescription(), dm01ModelName);
                for (var topic : dm01Inv.topics()) {
                    for (var cls : topic.classes()) {
                        dm01ClassPaths.put(cls.name().toLowerCase(), cls.path());
                        dm01ClassPaths.put(cls.path().toLowerCase(), cls.path());
                        for (var attr : cls.attributes()) {
                            dm01Attrs.put(key(cls.path(), attr.name()), attr);
                        }
                    }
                }
            } else {
                genWarnings.add("DM01 model compilation failed: " + dm01ModelName);
            }
        }

        Map<String, String> dmavClassPaths = new LinkedHashMap<>();
        Map<String, ModelInventory.AttributeInventory> dmavAttrs = new LinkedHashMap<>();
        if (dmavModelName != null && !dmavModelName.isBlank()) {
            IliModelCompileResult dmavResult = modelService.compileModel(dmavModelName, dmavModelDir);
            if (!dmavResult.hasErrors() && dmavResult.transferDescription() != null) {
                ModelInventory dmavInv = modelService.buildInventory(
                        dmavResult.transferDescription(), dmavModelName);
                for (var topic : dmavInv.topics()) {
                    for (var cls : topic.classes()) {
                        dmavClassPaths.put(cls.name().toLowerCase(), cls.path());
                        dmavClassPaths.put(cls.path().toLowerCase(), cls.path());
                        for (var attr : cls.attributes()) {
                            dmavAttrs.put(key(cls.path(), attr.name()), attr);
                        }
                    }
                }
            } else {
                genWarnings.add("DMAV model compilation failed: " + dmavModelName);
            }
        }

        // 4. Generate candidates from hints
        Set<String> coveredKeys = new java.util.HashSet<>();
        for (CorrelationHint hint : hints) {
            MappingCandidate candidate = fromHint(hint, dm01ClassPaths, dm01Attrs,
                    dmavClassPaths, dmavAttrs, genWarnings);
            if (candidate != null) {
                candidates.add(candidate);
                coveredKeys.add(candidate.key());
            }
        }

        // 5. Generate candidates from synonyms
        String hintDir = hintDirection(hints);
        if (hintDir != null) {
            Map<String, List<String>> dirSynonyms = synonyms.getOrDefault(hintDir, Map.of());
            for (var entry : dirSynonyms.entrySet()) {
                String srcAttr = entry.getKey();
                for (String tgtAttr : entry.getValue()) {
                    for (var srcPath : dm01ClassPaths.entrySet()) {
                        for (var tgtPath : dmavClassPaths.entrySet()) {
                            String key = srcPath.getValue() + "::" + srcAttr + "::"
                                    + tgtPath.getValue() + "::" + tgtAttr;
                            if (coveredKeys.contains(key)) continue;
                            if (dm01Attrs.containsKey(key(srcPath.getValue(), srcAttr))
                                    && dmavAttrs.containsKey(key(tgtPath.getValue(), tgtAttr))) {
                                candidates.add(new MappingCandidate(
                                        candidateId(srcPath.getValue(), tgtPath.getValue(), srcAttr),
                                        Direction.valueOf(hintDir),
                                        srcPath.getValue(),
                                        srcAttr,
                                        tgtPath.getValue(),
                                        tgtAttr,
                                        "${s." + srcAttr + "}",
                                        null,
                                        0.4,
                                        classify(0.4),
                                        "synonym:" + srcAttr,
                                        List.of()));
                                coveredKeys.add(key);
                            }
                        }
                    }
                }
            }
        }

        // 6. Deduplicate
        candidates = deduplicate(candidates);

        // 7. Classify
        for (int i = 0; i < candidates.size(); i++) {
            MappingCandidate c = candidates.get(i);
            double adjConfidence = adjustConfidence(c, dm01Attrs, dmavAttrs);
            candidates.set(i, new MappingCandidate(
                    c.id(), c.direction(), c.sourceClass(), c.sourceAttribute(),
                    c.targetClass(), c.targetAttribute(), c.expression(),
                    c.transformCode(), adjConfidence, classify(adjConfidence),
                    c.origin(), c.warnings()));
        }

        return new GenerationResult(candidates, genWarnings);
    }

    private MappingCandidate fromHint(CorrelationHint hint,
                                       Map<String, String> dm01ClassPaths,
                                       Map<String, ModelInventory.AttributeInventory> dm01Attrs,
                                       Map<String, String> dmavClassPaths,
                                       Map<String, ModelInventory.AttributeInventory> dmavAttrs,
                                       List<String> warnings) {
        List<String> hintWarnings = new ArrayList<>(hint.warnings());

        String srcClass = hint.sourceClass();
        String tgtClass = hint.targetClass();
        if (srcClass == null || srcClass.isBlank() || tgtClass == null || tgtClass.isBlank()) return null;

        String fullSrcClass;
        String fullTgtClass;
        if (hint.direction() == Direction.DM01_TO_DMAV) {
            fullSrcClass = resolveClass(srcClass, dm01ClassPaths, warnings);
            fullTgtClass = resolveClass(tgtClass, dmavClassPaths, warnings);
        } else if (hint.direction() == Direction.DMAV_TO_DM01) {
            fullSrcClass = resolveClass(srcClass, dmavClassPaths, warnings);
            fullTgtClass = resolveClass(tgtClass, dm01ClassPaths, warnings);
        } else {
            fullSrcClass = resolveClass(srcClass, dm01ClassPaths, dmavClassPaths, warnings);
            fullTgtClass = resolveClass(tgtClass, dm01ClassPaths, dmavClassPaths, warnings);
        }
        if (fullSrcClass == null || fullTgtClass == null) {
            hintWarnings.add("Could not resolve class: src=" + srcClass + " tgt=" + tgtClass);
            return null;
        }

        String srcAttr = hint.sourceAttribute();
        String tgtAttr = hint.targetAttribute();
        if (srcAttr == null || srcAttr.isBlank() || tgtAttr == null || tgtAttr.isBlank()) return null;

        double confidence = hint.confidence();
        String expression = "${s." + srcAttr + "}";
        if (confidence < 0.3) {
            expression = "TODO(" + srcAttr + " -> " + tgtAttr + ")";
        }

        String id = candidateId(fullSrcClass, fullTgtClass, srcAttr);

        return new MappingCandidate(id, hint.direction(),
                fullSrcClass, srcAttr, fullTgtClass, tgtAttr,
                expression, hint.transformCode(), confidence,
                classify(confidence), hint.cellPosition(), hintWarnings);
    }

    private String resolveClass(String shortName,
                                 Map<String, String> paths,
                                 Map<String, String> altPaths,
                                 List<String> warnings) {
        return resolveClass(shortName, paths, warnings);
    }

    private String resolveClass(String shortName,
                                 Map<String, String> paths,
                                 List<String> warnings) {
        String lower = shortName.trim().toLowerCase();
        if (paths.containsKey(lower)) return paths.get(lower);
        // Try partial match
        for (var entry : paths.entrySet()) {
            if (entry.getKey().contains(lower) || lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private double adjustConfidence(MappingCandidate c,
                                     Map<String, ModelInventory.AttributeInventory> dm01Attrs,
                                     Map<String, ModelInventory.AttributeInventory> dmavAttrs) {
        double conf = c.confidence();

        ModelInventory.AttributeInventory srcAttr = dm01Attrs.get(key(c.sourceClass(), c.sourceAttribute()));
        if (srcAttr == null) srcAttr = dmavAttrs.get(key(c.sourceClass(), c.sourceAttribute()));
        ModelInventory.AttributeInventory tgtAttr = dmavAttrs.get(key(c.targetClass(), c.targetAttribute()));
        if (tgtAttr == null) tgtAttr = dm01Attrs.get(key(c.targetClass(), c.targetAttribute()));

        if (srcAttr != null && tgtAttr != null) conf += 0.20;
        else if (srcAttr != null || tgtAttr != null) conf += 0.10;
        else conf -= 0.10;

        if (tgtAttr != null && tgtAttr.mandatory()) conf += 0.10;

        if (c.transformCode() != null) {
            conf += switch (c.transformCode()) {
                case "K" -> 0.10;
                case "V" -> 0.00;
                case "I" -> -0.10;
                default -> 0.00;
            };
        }

        return Math.max(0.0, Math.min(1.0, conf));
    }

    public static String classify(double confidence) {
        if (confidence >= 0.85) return "high";
        if (confidence >= 0.60) return "medium";
        if (confidence >= 0.30) return "low";
        return "manual";
    }

    private List<MappingCandidate> deduplicate(List<MappingCandidate> candidates) {
        Map<String, MappingCandidate> best = new LinkedHashMap<>();
        for (MappingCandidate c : candidates) {
            MappingCandidate existing = best.get(c.key());
            if (existing == null || c.confidence() > existing.confidence()) {
                best.put(c.key(), c);
            }
        }
        return new ArrayList<>(best.values());
    }

    private String candidateId(String srcClass, String tgtClass, String attr) {
        String srcShort = lastSegment(srcClass);
        String tgtShort = lastSegment(tgtClass);
        return (srcShort + "-" + tgtShort + "-" + attr).toLowerCase().replaceAll("[^a-z0-9-]", "-");
    }

    @SuppressWarnings("unchecked")
    private List<CorrelationHint> loadHints(Path path) throws IOException {
        List<Map<String, Object>> raw = MAPPER.readValue(path.toFile(),
                new TypeReference<List<Map<String, Object>>>() {});
        return raw.stream().map(this::mapToHint).collect(Collectors.toList());
    }

    private CorrelationHint mapToHint(Map<String, Object> map) {
        return new CorrelationHint(
                toInt(map.get("row")),
                toString(map.get("sheet")),
                toString(map.get("cell")),
                map.containsKey("direction") ? Direction.valueOf(toString(map.get("direction"))) : null,
                toString(map.get("sourceTopic")),
                toString(map.get("sourceClass")),
                toString(map.get("sourceAttribute")),
                toString(map.get("targetTopic")),
                toString(map.get("targetClass")),
                toString(map.get("targetAttribute")),
                toString(map.get("targetPath")),
                toString(map.get("condition")),
                toString(map.get("transformCode")),
                toString(map.get("addition")),
                toString(map.get("comment")),
                toDouble(map.get("confidence")),
                map.containsKey("warnings") ? ((List<String>) map.get("warnings")) : List.of()
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, List<String>>> loadSynonyms(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            // Try classpath resource
            InputStream is = getClass().getClassLoader().getResourceAsStream("dmav/synonyms.json");
            if (is != null) {
                try (is) {
                    return MAPPER.readValue(is,
                            new TypeReference<Map<String, Map<String, List<String>>>>() {});
                }
            }
            return Map.of();
        }
        return MAPPER.readValue(path.toFile(),
                new TypeReference<Map<String, Map<String, List<String>>>>() {});
    }

    private String hintDirection(List<CorrelationHint> hints) {
        long dm = hints.stream().filter(h -> h.direction() == Direction.DM01_TO_DMAV).count();
        long md = hints.stream().filter(h -> h.direction() == Direction.DMAV_TO_DM01).count();
        return dm >= md ? "DM01_TO_DMAV" : "DMAV_TO_DM01";
    }

    private static String key(String classPath, String attrName) {
        return (classPath + "::" + attrName).toLowerCase();
    }

    private static String lastSegment(String path) {
        if (path == null) return "";
        int dot = path.lastIndexOf('.');
        return dot >= 0 ? path.substring(dot + 1) : path;
    }

    private static String toString(Object val) {
        return val != null ? val.toString() : null;
    }

    private static int toInt(Object val) {
        if (val instanceof Number n) return n.intValue();
        return 0;
    }

    private static double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        return 0.5;
    }

    public record GenerationResult(List<MappingCandidate> candidates, List<String> warnings) {
        public long highCount() { return count("high"); }
        public long mediumCount() { return count("medium"); }
        public long lowCount() { return count("low"); }
        public long manualCount() { return count("manual"); }
        private long count(String classification) {
            return candidates.stream().filter(c -> c.classification().equals(classification)).count();
        }
        public long dm01ToDmavCount() { return dirCount(Direction.DM01_TO_DMAV); }
        public long dmavToDm01Count() { return dirCount(Direction.DMAV_TO_DM01); }
        private long dirCount(Direction d) {
            return candidates.stream().filter(c -> c.direction() == d).count();
        }
    }
}
