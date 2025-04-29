package dependencies;

import java.util.HashSet;

public class ProjectDepsReport extends DepsReport{

    private final HashSet<String> dependencies;

    public ProjectDepsReport(String name, HashSet<String> dependencies) {
        super(name);
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "Project: '" + name + "'\nDependencies: " + dependencies + "\n";
    }
}
