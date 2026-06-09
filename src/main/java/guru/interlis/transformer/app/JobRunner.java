package guru.interlis.transformer.app;

import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.IoxWriter;
import guru.interlis.transformer.diag.Diagnostic;
import guru.interlis.transformer.diag.DiagnosticCollector;
import guru.interlis.transformer.engine.TransformResult;
import guru.interlis.transformer.engine.TransformationEngine;
import guru.interlis.transformer.expr.ExpressionEngine;
import guru.interlis.transformer.interlis.InterlisIoFactory;
import guru.interlis.transformer.mapping.compiler.CompilerReport;
import guru.interlis.transformer.mapping.compiler.MappingCompiler;
import guru.interlis.transformer.mapping.compiler.MappingCompiler.CompileResult;
import guru.interlis.transformer.mapping.model.JobConfig;
import guru.interlis.transformer.mapping.model.MappingLoader;
import guru.interlis.transformer.mapping.plan.OutputBinding;
import guru.interlis.transformer.mapping.plan.TransformPlan;
import guru.interlis.transformer.model.ModelRegistry;
import guru.interlis.transformer.state.InMemoryStateStore;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class JobRunner {

    public CompileResult validateMapping(Path configPath) throws Exception {
        MappingLoader loader = new MappingLoader();
        JobConfig config = loader.load(configPath);
        return new MappingCompiler().compile(config);
    }

    public PreparedJob prepare(Path mappingFile, RunOptions options) throws Exception {
        Path baseDirectory = mappingFile.toAbsolutePath().getParent();
        MappingLoader loader = new MappingLoader();
        JobConfig config = loader.load(mappingFile);

        ModelRegistry modelRegistry = ModelRegistry.builder()
                .config(config)
                .modelDirs(config.job.modeldir)
                .modelDirs(options.modelDirs)
                .baseDirectory(baseDirectory)
                .build();

        TransformPlan plan = new MappingCompiler().compileTyped(config, modelRegistry);

        return new PreparedJob(config, plan, modelRegistry, baseDirectory);
    }

    public DiagnosticCollector run(Path configPath, String modelDir) throws Exception {
        RunOptions options = new RunOptions();
        if (modelDir != null && !modelDir.isBlank()) {
            options.modelDirs.add(modelDir);
        }
        PreparedJob prepared = prepare(configPath, options);

        // Print compiler diagnostics
        if (!prepared.plan().diagnostics().all().isEmpty()) {
            System.out.println("--- Compiler Diagnostics ---");
            for (Diagnostic d : prepared.plan().diagnostics().all()) {
                System.out.printf("[%s] %s: %s (rule: %s)%n",
                        d.severity(), d.code(), d.message(),
                        d.sourcePath() != null ? d.sourcePath() : "");
                if (d.suggestion() != null) {
                    System.out.printf("  Suggestion: %s%n", d.suggestion());
                }
            }
            System.out.println();
        }

        if (prepared.plan().diagnostics().hasErrors()) {
            System.err.println("Compilation failed with errors. Aborting.");
            return prepared.plan().diagnostics();
        }

        // Create I/O readers and writers from bindings
        DiagnosticCollector engineDiag = new DiagnosticCollector();
        InterlisIoFactory ioFactory = new InterlisIoFactory();
        Map<String, IoxWriter> writersByOutputId = new HashMap<>();
        for (var entry : prepared.plan().outputsById().entrySet()) {
            String outputId = entry.getKey();
            OutputBinding binding = entry.getValue();
            writersByOutputId.put(outputId,
                    ioFactory.createWriter(binding.path(), binding.transferDescription(), engineDiag));
        }

        Map<String, IoxReader> readerByInputId = new HashMap<>();
        for (var entry : prepared.plan().inputsById().entrySet()) {
            String inputId = entry.getKey();
            var binding = entry.getValue();
            readerByInputId.put(inputId,
                    ioFactory.createReader(binding.path(), binding.transferDescription()));
        }

        TransformationEngine engine = new TransformationEngine(new ExpressionEngine(),
                new InMemoryStateStore(), engineDiag);
        TransformResult result = engine.runTyped(prepared.plan(), readerByInputId::get, writersByOutputId);

        for (Diagnostic d : engineDiag.all()) {
            prepared.plan().diagnostics().add(d);
        }

        System.out.println(result.summary());

        return prepared.plan().diagnostics();
    }
}
