package guru.interlis.transformer.expr;

import java.time.LocalDate;

public record DateValue(LocalDate value) implements Value {
    @Override
    public Object toNative() {
        return value.toString();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
