package guru.interlis.transformer.interlis;

import ch.interlis.ili2c.Ili2cException;
import ch.interlis.ili2c.Ili2cFailure;
import ch.interlis.ili2c.Ili2cSettings;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ilirepository.IliManager;

import java.util.ArrayList;

public final class InterlisModelLoader {

    public TransferDescription compileModel(String modelName, String modelDirectories) throws Ili2cFailure {
        ArrayList<String> repos = new ArrayList<>();
        if (modelDirectories != null && !modelDirectories.isBlank()) {
            for (String part : modelDirectories.split(";")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    repos.add(trimmed);
                }
            }
        }

        IliManager manager = new IliManager();
        manager.setRepositories(repos.toArray(new String[0]));

        ArrayList<String> entries = new ArrayList<>();
        entries.add(modelName.trim());

        Configuration cfg;
        try {
            cfg = manager.getConfigWithFiles(entries, null, 0.0);
        } catch (Ili2cException e) {
            throw new Ili2cFailure(e);
        }

        if (cfg == null) {
            throw new Ili2cFailure("Failed to create configuration for model: " + modelName);
        }

        Ili2cSettings settings = new Ili2cSettings();
        ch.interlis.ili2c.Main.setDefaultIli2cPathMap(settings);
        if (modelDirectories != null && !modelDirectories.isBlank()) {
            settings.setIlidirs(modelDirectories);
        }

        return ch.interlis.ili2c.Main.runCompiler(cfg, settings);
    }
}
