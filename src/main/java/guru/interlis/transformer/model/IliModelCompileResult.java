package guru.interlis.transformer.model;

import guru.interlis.transformer.diag.Diagnostic;
import guru.interlis.transformer.diag.DiagnosticCode;
import guru.interlis.transformer.diag.DiagnosticCollector;
import guru.interlis.transformer.diag.Severity;

public record IliModelCompileResult(
        ch.interlis.ili2c.metamodel.TransferDescription transferDescription,
        DiagnosticCollector diagnostics
) {
    public boolean hasErrors() {
        return diagnostics.hasErrors();
    }
}
