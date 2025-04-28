package dependencies;

import java.util.HashSet;

public class ClassDepsReport {

    private String className;
    private HashSet<String> dependencies;

    public ClassDepsReport(String className, HashSet<String> dependencies) {
        this.className = className;
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "Class: '" + className + "'\n" + "Dependencies: " + dependencies + "\n";
    }
}
