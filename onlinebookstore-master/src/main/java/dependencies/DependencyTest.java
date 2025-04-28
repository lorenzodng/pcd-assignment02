package dependencies;

import java.io.File;

public class DependencyTest {

    public static void main(String[] args) {

        DependencyAnalyserLib analyser = new DependencyAnalyserLib();

        File classSrcFile = new File("C:/Users/matti/IdeaProjects/pcd-assignment02/onlinebookstore-master/src/main/java/com/bittercode/model/Cart.java");

        analyser.getClassDependencies(classSrcFile).onSuccess(report -> {
            System.out.println("Dependencies found: " + report);
        }).onFailure(Throwable::printStackTrace);
    }
}
