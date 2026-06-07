package guru.interlis.transformer.expr;

public record CoordValue(double x, double y) implements Value {
    @Override
    public Object toNative() {
        return x + " " + y;
    }
}
