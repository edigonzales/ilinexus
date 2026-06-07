package guru.interlis.transformer.expr.builtins;

import guru.interlis.transformer.diag.Diagnostic;
import guru.interlis.transformer.diag.DiagnosticCode;
import guru.interlis.transformer.diag.Severity;
import guru.interlis.transformer.expr.EnumValue;
import guru.interlis.transformer.expr.EvalContext;
import guru.interlis.transformer.expr.FunctionDef;
import guru.interlis.transformer.expr.FunctionRegistry;
import guru.interlis.transformer.expr.NullValue;
import guru.interlis.transformer.expr.Value;
import guru.interlis.transformer.mapping.plan.TypeInfo;
import java.util.List;

public final class EnumFunctions {

    private EnumFunctions() {}

    public static void registerAll(FunctionRegistry registry) {
        registry.register("enumMap", TypeInfo.ENUM,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN),
                        new FunctionDef.FunctionParam("mapName", TypeInfo.TEXT)),
                EnumFunctions::enumMap);

        registry.register("enumDefault", TypeInfo.ENUM,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN),
                        new FunctionDef.FunctionParam("fallback", TypeInfo.TEXT)),
                EnumFunctions::enumDefault);

        registry.register("enumName", TypeInfo.TEXT,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.ENUM)),
                EnumFunctions::enumName);
    }

    static Value enumMap(List<Value> args, EvalContext ctx) {
        if (args.size() < 2 || !args.get(0).isDefined()) return NullValue.INSTANCE;
        Value val = args.get(0);
        String mapName = args.get(1).asText();

        if (ctx.diagnostics() != null) {
            ctx.diagnostics().add(new Diagnostic(
                    DiagnosticCode.EXPR_UNSUPPORTED, Severity.WARNING,
                    "enumMap() is not yet fully implemented (Phase 10) – pass-through used for map '"
                            + mapName + "'",
                    ctx.ruleId(), "The value is returned unchanged; full enum mapping will be available in Phase 10"));
        }
        return val;
    }

    static Value enumDefault(List<Value> args, EvalContext ctx) {
        if (args.isEmpty()) return NullValue.INSTANCE;
        Value val = args.get(0);
        if (val.isDefined()) return val;
        if (args.size() >= 2) {
            return new EnumValue(args.get(1).asText(), null);
        }
        return NullValue.INSTANCE;
    }

    static Value enumName(List<Value> args, EvalContext ctx) {
        if (args.isEmpty() || !args.get(0).isDefined()) return NullValue.INSTANCE;
        Value val = args.get(0);
        if (val instanceof EnumValue ev) {
            return new guru.interlis.transformer.expr.TextValue(ev.name());
        }
        return new guru.interlis.transformer.expr.TextValue(val.asText());
    }
}
