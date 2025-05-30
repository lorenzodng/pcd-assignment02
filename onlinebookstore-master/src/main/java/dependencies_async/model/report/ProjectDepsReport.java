package dependencies_async.model.report;

import dependencies_async.model.DepsReport;

import java.util.HashSet;

public class ProjectDepsReport extends DepsReport {

    public ProjectDepsReport(String name, HashSet<String> dependencies) {
        super(name, dependencies);
    }

    @Override
    public String toString() {
        return "Project: \"" + name + "\" \n" + "Dependencies: \n" + printDependencies() + "\n";
    }
}
