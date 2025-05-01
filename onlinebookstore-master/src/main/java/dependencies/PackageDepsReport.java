package dependencies;

import java.util.HashSet;

public class PackageDepsReport extends DepsReport {

    public PackageDepsReport(String name, HashSet<String> dependencies) {
        super(name, dependencies);
    }

    @Override
    public String toString() {
        return "Package: \"" + name + "\" \n" + "Dependencies: \n" + printDependencies() + "\n";
    }
}
