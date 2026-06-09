package guru.interlis.transformer.mapping.plan;

import ch.interlis.ili2c.metamodel.TransferDescription;
import guru.interlis.transformer.model.TypeSystemFacade;
import java.nio.file.Path;

public record InputBinding(
        String inputId,
        Path path,
        String declaredModelName,
        TransferFormat format,
        TransferDescription transferDescription,
        TypeSystemFacade typeSystem
) {}
