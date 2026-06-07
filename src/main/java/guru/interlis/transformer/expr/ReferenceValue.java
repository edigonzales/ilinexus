package guru.interlis.transformer.expr;

public record ReferenceValue(String targetClass, String oid) implements Value {
    @Override
    public Object toNative() {
        return oid;
    }
}
