package guru.interlis.transformer.expr;

import ch.interlis.iom.IomObject;
import guru.interlis.transformer.diag.DiagnosticCollector;
import java.util.Map;

public record EvalContext(Map<String, IomObject> sources, DiagnosticCollector diagnostics, String ruleId, java.util.Map<String, java.util.Map<String, String>> enumMaps) {

    public EvalContext(Map<String, IomObject> sources, DiagnosticCollector diagnostics, String ruleId) {
        this(sources, diagnostics, ruleId, null);
    }

    public EvalContext withEnumMaps(java.util.Map<String, java.util.Map<String, String>> maps) {
        return new EvalContext(sources, diagnostics, ruleId, maps);
    }
}
