package guru.interlis.ilinexus.state;

import ch.interlis.iom.IomObject;

public record SourceRecord(
        String sourceFileId,
        String sourceBasketId,
        String sourceClass,
        IomObject sourceObject
) {
}
