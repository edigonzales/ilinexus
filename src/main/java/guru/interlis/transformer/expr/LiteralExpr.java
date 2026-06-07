package guru.interlis.transformer.expr;

public record LiteralExpr(Value value) implements Expression {
    public LiteralExpr {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
    }
}
