package guru.interlis.ilinexus.diag;

public record Diagnostic(String code, Severity severity, String message, String sourcePath, String suggestion) {
}
