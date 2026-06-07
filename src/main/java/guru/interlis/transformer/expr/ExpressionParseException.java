package guru.interlis.transformer.expr;

public final class ExpressionParseException extends RuntimeException {
    private final String expression;
    private final int position;

    public ExpressionParseException(String message, String expression, int position) {
        super(message);
        this.expression = expression;
        this.position = position;
    }

    public String expression() {
        return expression;
    }

    public int position() {
        return position;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at position " + position + " in '" + expression + "'";
    }
}
