package guru.interlis.transformer.expr;

public record NumberValue(double value) implements Value {
    @Override
    public double asNumber() {
        return value;
    }

    @Override
    public Object toNative() {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return (long) value;
        }
        return value;
    }
}
