package guru.interlis.transformer.app;

import java.util.ArrayList;
import java.util.List;

public final class RunOptions {

    public List<String> modelDirs = new ArrayList<>();

    public RunOptions() {}

    public RunOptions(List<String> modelDirs) {
        this.modelDirs = modelDirs != null ? new ArrayList<>(modelDirs) : new ArrayList<>();
    }

    public RunOptions withModelDir(String modelDir) {
        this.modelDirs.add(modelDir);
        return this;
    }

    public RunOptions withModelDirs(List<String> modelDirs) {
        this.modelDirs.addAll(modelDirs);
        return this;
    }
}
