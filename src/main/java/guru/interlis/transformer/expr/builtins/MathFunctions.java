package guru.interlis.transformer.expr.builtins;

import guru.interlis.transformer.expr.EvalContext;
import guru.interlis.transformer.expr.FunctionDef;
import guru.interlis.transformer.expr.FunctionRegistry;
import guru.interlis.transformer.expr.NullValue;
import guru.interlis.transformer.expr.NumberValue;
import guru.interlis.transformer.expr.TextValue;
import guru.interlis.transformer.expr.Value;
import guru.interlis.transformer.mapping.plan.TypeInfo;
import java.util.List;

public final class MathFunctions {

    private MathFunctions() {}

    public static void registerAll(FunctionRegistry registry) {
        registry.register("div", TypeInfo.NUMERIC,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN),
                        new FunctionDef.FunctionParam("divisor", TypeInfo.NUMERIC)),
                MathFunctions::div);

        registry.register("mul", TypeInfo.NUMERIC,
                List.of(new FunctionDef.FunctionParam("value", TypeInfo.UNKNOWN),
                        new FunctionDef.FunctionParam("factor", TypeInfo.NUMERIC)),
                MathFunctions::mul);
    }

    static Value mul(List<Value> args, EvalContext ctx) {
        if (args.size() < 2 || !args.get(0).isDefined()) return NullValue.INSTANCE;
        double value = toDouble(args.get(0));
        double factor = toDouble(args.get(1));
        return new NumberValue(value * factor);
    }

    static Value div(List<Value> args, EvalContext ctx) {
        if (args.size() < 2 || !args.get(0).isDefined()) return NullValue.INSTANCE;
        double value = toDouble(args.get(0));
        double divisor = toDouble(args.get(1));
        if (divisor == 0.0) return NullValue.INSTANCE;
        return new NumberValue(value / divisor);
    }

    private static double toDouble(Value v) {
        if (v instanceof NumberValue nv) return nv.value();
        if (v instanceof TextValue tv) {
            try {
                return Double.parseDouble(tv.value());
            } catch (NumberFormatException ignored) {
            }
        }
        return v.asNumber();
    }
}
