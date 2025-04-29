package dependencies;

public abstract class DepsReport {

    protected final String name;

    public DepsReport(String name){
        this.name= name;
    }

    public abstract String toString();
}
