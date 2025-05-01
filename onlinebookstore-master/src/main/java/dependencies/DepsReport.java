package dependencies;

import java.util.HashSet;

public abstract class DepsReport {

    protected final String name;
    protected final HashSet<String> dependencies;

    public DepsReport(String name, HashSet<String> dependencies){
        this.name= name;
        this.dependencies= dependencies;
    }

    public String printDependencies() {
        StringBuilder sb = new StringBuilder();
        for (String dependency : dependencies) {
            sb.append(dependency).append("\n");
        }
        return sb.toString();
    }

    public abstract String toString();

}
