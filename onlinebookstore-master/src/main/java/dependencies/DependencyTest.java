package dependencies;

import io.vertx.core.Vertx;

import java.io.File;

public class DependencyTest {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        DependencyAnalyserLib analyser = new DependencyAnalyserLib(vertx);

        File classSrcFile = new File("C:/Users/matti/IdeaProjects/pcd-assignment02/onlinebookstore-master/src/main/java/com/bittercode/model/Cart.java");

        analyser.getClassDependencies(classSrcFile);
    }
}
