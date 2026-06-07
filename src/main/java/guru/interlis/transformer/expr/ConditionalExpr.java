package guru.interlis.transformer.expr;

public record ConditionalExpr(Expression condition, Expression thenExpr, Expression elseExpr) implements Expression {
    public ConditionalExpr {
        if (condition == null) throw new IllegalArgumentException("condition must not be null");
        if (thenExpr == null) throw new IllegalArgumentException("thenExpr must not be null");
        if (elseExpr == null) throw new IllegalArgumentException("elseExpr must not be null");
    }
}
