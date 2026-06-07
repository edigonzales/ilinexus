package guru.interlis.transformer.expr.builtins;

import guru.interlis.transformer.expr.BooleanValue;
import guru.interlis.transformer.expr.EvalContext;
import guru.interlis.transformer.expr.FunctionDef;
import guru.interlis.transformer.expr.FunctionRegistry;
import guru.interlis.transformer.expr.NullValue;
import guru.interlis.transformer.expr.Value;
import guru.interlis.transformer.mapping.plan.TypeInfo;
import java.util.List;

public final class BasicFunctions {

    private BasicFunctions() {}

    public static void registerAll(FunctionRegistry registry) {
        registry.register(new FunctionDef("coalesce", TypeInfo.TEXT,
                List.of(new FunctionDef.FunctionParam("values", TypeInfo.UNKNOWN)),
                false, BasicFunctions::coalesce));

        registry.register(new FunctionDef("defined", TypeInfo.BOOLEAN,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN)),
                false, BasicFunctions::defined));

        registry.register(new FunctionDef("notDefined", TypeInfo.BOOLEAN,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN)),
                false, BasicFunctions::notDefined));

        registry.register(new FunctionDef("isNull", TypeInfo.BOOLEAN,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN)),
                false, BasicFunctions::isNull));

        registry.register(new FunctionDef("default", TypeInfo.TEXT,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN),
                        new FunctionDef.FunctionParam("fallback", TypeInfo.UNKNOWN)),
                false, BasicFunctions::withDefault));

        registry.register(new FunctionDef("null", TypeInfo.UNKNOWN,
                List.of(), false, BasicFunctions::nullFn));
    }

    static Value coalesce(List<Value> args, EvalContext ctx) {
        for (Value arg : args) {
            if (arg.isDefined()) {
                return arg;
            }
        }
        return NullValue.INSTANCE;
    }

    static Value defined(List<Value> args, EvalContext ctx) {
        if (args.isEmpty()) return BooleanValue.FALSE;
        return BooleanValue.of(args.get(0).isDefined());
    }

    static Value notDefined(List<Value> args, EvalContext ctx) {
        if (args.isEmpty()) return BooleanValue.TRUE;
        return BooleanValue.of(!args.get(0).isDefined());
    }

    static Value isNull(List<Value> args, EvalContext ctx) {
        if (args.isEmpty()) return BooleanValue.TRUE;
        return BooleanValue.of(args.get(0).isNull());
    }

    static Value withDefault(List<Value> args, EvalContext ctx) {
        if (args.size() < 2) return NullValue.INSTANCE;
        Value val = args.get(0);
        return val.isDefined() ? val : args.get(1);
    }

    static Value nullFn(List<Value> args, EvalContext ctx) {
        return NullValue.INSTANCE;
    }
}
