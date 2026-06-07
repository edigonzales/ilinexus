package guru.interlis.transformer.expr;

import java.util.List;

@FunctionalInterface
public interface FunctionImplementation {
    Value apply(List<Value> args, EvalContext ctx);
}
