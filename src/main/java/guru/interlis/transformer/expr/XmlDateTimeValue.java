package guru.interlis.transformer.expr;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record XmlDateTimeValue(ZonedDateTime value) implements Value {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public Object toNative() {
        return value.format(FORMATTER);
    }

    @Override
    public String toString() {
        return value.format(FORMATTER);
    }
}
