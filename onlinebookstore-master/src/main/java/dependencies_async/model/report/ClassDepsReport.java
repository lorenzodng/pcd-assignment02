package dependencies_async.model.report;

import dependencies_async.model.DepsReport;

import java.util.HashSet;

public class ClassDepsReport extends DepsReport {

    public ClassDepsReport(String name, HashSet<String> dependencies) {
        super(name, dependencies);
    }

    @Override
    public String toString() {
        return "Class: " + name + "\n" + "Dependencies: \n" + printDependencies() + "\n";
    }
}
