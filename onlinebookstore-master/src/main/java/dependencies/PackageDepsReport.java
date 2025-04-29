package dependencies;

import java.util.HashMap;
import java.util.HashSet;

public class PackageDepsReport extends DepsReport {

    private final HashMap<String, HashSet<String>> dependencies;

    public PackageDepsReport(String name, HashMap<String, HashSet<String>> dependencies) {
        super(name);
        this.dependencies= dependencies;
    }

    @Override
    public String toString() {
        return "Package: \"" + name + "\" \n" + printDependencies() + "\n";
    }

    public String printDependencies() {
        StringBuilder sb = new StringBuilder();
        for (String key : dependencies.keySet()) {
            sb.append("Class: ").append(key).append("\n").append("Dependencies: ").append(dependencies.get(key)).append("\n");
        }
        return sb.toString();
    }
}
