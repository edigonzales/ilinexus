package guru.interlis.transformer.state;

import ch.interlis.iom.IomObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class InMemorySourceLookupIndex implements SourceLookupIndex {

    private final Map<String, Map<String, Map<CanonicalValue, List<SourceRecord>>>> index = new LinkedHashMap<>();

    @Override
    public void index(SourceRecord record) {
        IomObject obj = record.sourceObject();
        if (obj == null) return;
        String sourceClass = record.sourceClass();

        Map<String, Map<CanonicalValue, List<SourceRecord>>> classIndex =
                index.computeIfAbsent(sourceClass, k -> new LinkedHashMap<>());

        for (int i = 0; i < obj.getattrcount(); i++) {
            String attrName = obj.getattrname(i);
            // 1. Scalar value
            String attrValue = obj.getattrvalue(attrName);
            if (attrValue != null) {
                CanonicalValue cv = new CanonicalValue("text", attrValue, true);
                classIndex
                        .computeIfAbsent(attrName, k -> new LinkedHashMap<>())
                        .computeIfAbsent(cv, k -> new ArrayList<>())
                        .add(record);
                continue;
            }
            // 2. Reference object (e.g. ILI1 -> LFP3 or ILI2 REFERENCE TO)
            if (obj.getattrvaluecount(attrName) > 0) {
                IomObject refObj = obj.getattrobj(attrName, 0);
                if (refObj != null && refObj.getobjectrefoid() != null) {
                    String refOid = refObj.getobjectrefoid();
                    CanonicalValue cv = new CanonicalValue("text", refOid, true);
                    classIndex
                            .computeIfAbsent(attrName, k -> new LinkedHashMap<>())
                            .computeIfAbsent(cv, k -> new ArrayList<>())
                            .add(record);
                }
            }
        }
    }

    @Override
    public List<SourceRecord> lookup(LookupKey key) {
        String sourceClass = key.sourceClass();
        Map<String, Map<CanonicalValue, List<SourceRecord>>> classIndex = index.get(sourceClass);
        if (classIndex == null) return List.of();

        Map<CanonicalValue, List<SourceRecord>> attrIndex = classIndex.get(key.attribute());
        if (attrIndex == null) return List.of();

        // Match by canonicalText and defined status
        for (var entry : attrIndex.entrySet()) {
            CanonicalValue indexedCv = entry.getKey();
            if (Objects.equals(key.value().canonicalText(), indexedCv.canonicalText())
                    && key.value().defined() == indexedCv.defined()) {
                // Filter by inputId if specified
                List<SourceRecord> records = entry.getValue();
                if (key.inputId() == null || key.inputId().isBlank()) {
                    return records;
                }
                return records.stream()
                        .filter(r -> key.inputId().equals(r.sourceFileId()))
                        .toList();
            }
        }
        return List.of();
    }
}
