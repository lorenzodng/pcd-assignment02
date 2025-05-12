package dependencies.main;

import dependencies.service.DependencyAnalyserLib;
import io.vertx.core.Vertx;

import java.io.File;

public class DependencyTest {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        DependencyAnalyserLib analyser = new DependencyAnalyserLib(vertx);

        File classSrcFile = new File("C:/Users/loren/IdeaProjects/pcd-assignment02/onlinebookstore-master/src/main/java/dependencies/service/DependencyAnalyserLib.java");
        System.out.println("");
        analyser.findClassDependencies(classSrcFile);
        File packageSrcFile = new File("C:/Users/loren/IdeaProjects/pcd-assignment02/onlinebookstore-master/src/main/java/com/bittercode/model");
        //analyser.findPackageDependencies(packageSrcFile);
        System.out.println("");
        File projectSrcFile = new File("C:/Users/loren/IdeaProjects/pcd-assignment02");
        //analyser.findProjectDependencies(projectSrcFile);

    }
}
