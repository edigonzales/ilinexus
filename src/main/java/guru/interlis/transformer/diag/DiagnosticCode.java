package guru.interlis.transformer.diag;

public final class DiagnosticCode {
    private DiagnosticCode() {}

    // Mapping/Compiler validation
    public static final String MAP_VERSION = "ILITRF-MAP-VERSION";
    public static final String MAP_MISSING_ID = "ILITRF-MAP-MISSING-ID";
    public static final String MAP_DUPLICATE_ID = "ILITRF-MAP-DUPLICATE-ID";
    public static final String MAP_MISSING_TARGET_CLASS = "ILITRF-MAP-MISSING-TARGET-CLASS";
    public static final String MAP_UNKNOWN_OUTPUT = "ILITRF-MAP-UNKNOWN-OUTPUT";
    public static final String MAP_MISSING_SOURCE_CLASS = "ILITRF-MAP-MISSING-SOURCE-CLASS";
    public static final String MAP_MISSING_ALIAS = "ILITRF-MAP-MISSING-ALIAS";
    public static final String MAP_DUPLICATE_ALIAS = "ILITRF-MAP-DUPLICATE-ALIAS";
    public static final String MAP_UNKNOWN_INPUT = "ILITRF-MAP-UNKNOWN-INPUT";
    public static final String MAP_MISSING_INPUT = "ILITRF-MAP-MISSING-INPUT";

    // Model
    public static final String MODEL_COMPILE_FAILED = "ILITRF-MODEL-COMPILE-FAILED";

    // Runtime
    public static final String RUN_REF_UNRESOLVED = "ILITRF-RUN-REF-UNRESOLVED";
    public static final String RUN_REF_AMBIGUOUS = "ILITRF-RUN-REF-AMBIGUOUS";
}
