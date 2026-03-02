package guru.interlis.ilinexus.app;

import guru.interlis.ilinexus.diag.Diagnostic;
import java.nio.file.Path;

public final class CliMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: ilinexus <mapping.yaml> [--modeldir <path>]");
            return;
        }
        Path mapping = Path.of(args[0]);
        String modelDir = null;
        for (int i = 1; i < args.length; i++) {
            if ("--modeldir".equals(args[i]) && i + 1 < args.length) {
                modelDir = args[i + 1];
                i++;
            }
        }

        var diagnostics = new JobRunner().run(mapping, modelDir);
        for (Diagnostic d : diagnostics.all()) {
            System.out.printf("[%s] %s: %s (%s)%n", d.severity(), d.code(), d.message(), d.sourcePath());
        }
    }
}
