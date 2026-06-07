package guru.interlis.transformer.expr;

import ch.interlis.iom.IomObject;
import guru.interlis.transformer.diag.Diagnostic;
import guru.interlis.transformer.diag.DiagnosticCode;
import guru.interlis.transformer.diag.DiagnosticCollector;
import guru.interlis.transformer.diag.Severity;
import guru.interlis.transformer.expr.builtins.BasicFunctions;
import guru.interlis.transformer.expr.builtins.DateFunctions;
import guru.interlis.transformer.expr.builtins.EnumFunctions;
import guru.interlis.transformer.expr.builtins.MathFunctions;
import guru.interlis.transformer.expr.builtins.RefFunctions;
import guru.interlis.transformer.expr.builtins.StringFunctions;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ExpressionEngine {

    private final FunctionRegistry functionRegistry;

    public ExpressionEngine() {
        this.functionRegistry = new FunctionRegistry();
        registerBuiltins();
    }

    public ExpressionEngine(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    private void registerBuiltins() {
        BasicFunctions.registerAll(functionRegistry);
        StringFunctions.registerAll(functionRegistry);
        DateFunctions.registerAll(functionRegistry);
        EnumFunctions.registerAll(functionRegistry);
        RefFunctions.registerAll(functionRegistry);
        MathFunctions.registerAll(functionRegistry);
    }

    public FunctionRegistry functionRegistry() {
        return functionRegistry;
    }

    public Value evaluate(String expression, Map<String, IomObject> sources) {
        EvalContext ctx = new EvalContext(sources, null, null);
        return evaluate(expression, ctx);
    }

    public Value evaluate(String expression, EvalContext ctx) {
        if (expression == null || expression.isBlank()) {
            return NullValue.INSTANCE;
        }
        String trimmed = expression.trim();

        // Legacy quick path: simple string literal
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\""))
                || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return new TextValue(trimmed.substring(1, trimmed.length() - 1));
        }

        // Legacy quick path: simple path reference ${alias.attr}
        if (trimmed.startsWith("${") && trimmed.endsWith("}") && !trimmed.contains("(")) {
            String path = trimmed.substring(2, trimmed.length() - 1);
            return resolveSourcePath(path, ctx);
        }

        // Full parsing
        Expression ast;
        try {
            ast = ExpressionParser.parse(trimmed);
        } catch (ExpressionParseException e) {
            if (ctx.diagnostics() != null) {
                ctx.diagnostics().add(new Diagnostic(
                        DiagnosticCode.EXPR_SYNTAX, Severity.ERROR,
                        "Expression parse error: " + e.getMessage(),
                        ctx.ruleId(), expression));
            }
            return NullValue.INSTANCE;
        }

        try {
            return evaluateAst(ast, ctx);
        } catch (Exception e) {
            if (ctx.diagnostics() != null) {
                ctx.diagnostics().add(new Diagnostic(
                        DiagnosticCode.EXPR_TYPE, Severity.ERROR,
                        "Expression evaluation error: " + e.getMessage(),
                        ctx.ruleId(), expression));
            }
            return NullValue.INSTANCE;
        }
    }

    // -- AST evaluation -----------------------------------------------

    private Value evaluateAst(Expression ast, EvalContext ctx) {
        return switch (ast) {
            case LiteralExpr l -> l.value();
            case PathExpr p -> resolveSourcePath(p.alias() + "." + p.attributeName(), ctx);
            case FunctionCallExpr f -> evaluateFunctionCall(f, ctx);
            case ConditionalExpr c -> evaluateConditional(c, ctx);
        };
    }

    private Value evaluateFunctionCall(FunctionCallExpr call, EvalContext ctx) {
        Optional<FunctionDef> defOpt = functionRegistry.resolve(call.functionName());
        if (defOpt.isEmpty()) {
            if (ctx.diagnostics() != null) {
                ctx.diagnostics().add(new Diagnostic(
                        DiagnosticCode.EXPR_UNKNOWN_FUNC, Severity.ERROR,
                        "Unknown function: " + call.functionName(),
                        ctx.ruleId(), "Check function name or ensure it is registered"));
            }
            return NullValue.INSTANCE;
        }
        FunctionDef def = defOpt.get();

        if (def.nonDeterministic() && ctx.diagnostics() != null) {
            ctx.diagnostics().add(new Diagnostic(
                    DiagnosticCode.EXPR_NON_DETERMINISTIC, Severity.WARNING,
                    "Non-deterministic function used: " + def.name(),
                    ctx.ruleId(), "Results may vary between runs"));
        }

        List<Value> evalArgs = call.arguments().stream()
                .map(arg -> evaluateAst(arg, ctx))
                .toList();
        return def.impl().apply(evalArgs, ctx);
    }

    private Value evaluateConditional(ConditionalExpr cond, EvalContext ctx) {
        Value conditionVal = evaluateAst(cond.condition(), ctx);
        boolean truthy = isTruthy(conditionVal);
        return evaluateAst(truthy ? cond.thenExpr() : cond.elseExpr(), ctx);
    }

    private boolean isTruthy(Value value) {
        if (!value.isDefined()) return false;
        if (value instanceof BooleanValue bv) return bv.value();
        if (value instanceof TextValue tv) return !tv.value().isEmpty();
        if (value instanceof NumberValue nv) return nv.value() != 0;
        return true;
    }

    // -- Source path resolution ---------------------------------------

    private Value resolveSourcePath(String path, EvalContext ctx) {
        String[] parts = path.split("\\.", 2);
        if (parts.length < 2) {
            return NullValue.INSTANCE;
        }
        IomObject source = ctx.sources().get(parts[0]);
        if (source == null) {
            return NullValue.INSTANCE;
        }
        String attrValue = source.getattrvalue(parts[1]);
        if (attrValue == null) {
            return NullValue.INSTANCE;
        }
        return new TextValue(attrValue);
    }

    // -- Legacy compatibility -----------------------------------------

    @Deprecated
    public Object evaluateLegacy(String expression, Map<String, IomObject> sources) {
        Value result = evaluate(expression, sources);
        return result.toNative();
    }
}
