package dependencies;

import java.util.Set;

public class ClassDepsReport {

    private String className;
    private Set<String> dependencies;

    public ClassDepsReport(String className, Set<String> dependencies) {
        this.className = className;
        this.dependencies = dependencies;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "ClassDepsReport{" + "className: '" + className + '\'' + ", dependencies: " + dependencies + '}';
    }
}
