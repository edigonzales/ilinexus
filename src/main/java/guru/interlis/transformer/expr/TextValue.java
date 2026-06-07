package guru.interlis.transformer.expr;

public record TextValue(String value) implements Value {
    @Override
    public String asText() {
        return value;
    }

    @Override
    public Object toNative() {
        return value;
    }
}
