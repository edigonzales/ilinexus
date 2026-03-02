package guru.interlis.ilinexus.state;

public record DeferredRef(
        String ownerTargetClass,
        String ownerTargetOid,
        String ownerAttribute,
        String sourceClass,
        String sourceReferencedOid,
        String sourceFileId,
        String sourceBasketId,
        String expectedTargetClass
) {
}
