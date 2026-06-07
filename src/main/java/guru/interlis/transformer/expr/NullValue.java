package guru.interlis.transformer.expr;

public final class NullValue implements Value {
    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {}

    @Override
    public Object toNative() {
        return null;
    }

    @Override
    public String toString() {
        return "null";
    }
}
