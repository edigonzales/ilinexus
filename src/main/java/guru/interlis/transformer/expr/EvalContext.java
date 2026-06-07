package guru.interlis.transformer.expr;

import ch.interlis.iom.IomObject;
import guru.interlis.transformer.diag.DiagnosticCollector;
import java.util.Map;

public record EvalContext(Map<String, IomObject> sources, DiagnosticCollector diagnostics, String ruleId) {
}
