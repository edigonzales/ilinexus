package guru.interlis.transformer.expr;

import guru.interlis.transformer.mapping.plan.TypeInfo;
import java.util.List;

public record FunctionDef(String name, TypeInfo returnType, List<FunctionParam> params,
                          boolean nonDeterministic, FunctionImplementation impl) {

    public record FunctionParam(String name, TypeInfo type) {}
}
