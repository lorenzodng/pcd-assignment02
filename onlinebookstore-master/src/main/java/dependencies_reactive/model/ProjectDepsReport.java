package dependencies_reactive.model;

import java.util.HashSet;

public class ProjectDepsReport {

    private final String type;
    private final String name;
    private final HashSet<String> dependencies;

    public ProjectDepsReport(String type, String name, HashSet<String> dependencies) {
        this.type = type;
        this.name = name;
        this.dependencies = dependencies;

    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public HashSet<String> getDependencies() {
        return dependencies;
    }

    public String printDependencies() {
        StringBuilder sb = new StringBuilder();
        for (String dependency : dependencies) {
            sb.append(" ").append(dependency).append("\n");
        }
        return sb.toString();
    }

    public String toString() {
        return " " + type + ": " + name + "\n Dependencies: \n" + printDependencies() + "\n";
    }
}
