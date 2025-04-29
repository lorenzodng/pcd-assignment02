package dependencies;

import java.util.HashSet;

public class ClassDepsReport extends DepsReport {

    private final HashSet<String> dependencies;

    public ClassDepsReport(String name, HashSet<String> dependencies) {
        super(name);
        this.dependencies= dependencies;
    }

    @Override
    public String toString() {
        return "Class: " + name + "\n" + "Dependencies: " + dependencies + "\n";
    }
}
