package dependencies;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.vertx.core.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DependencyAnalyserLib {

    public Future<ClassDepsReport> getClassDependencies(File classSrcFile) {
        Promise<ClassDepsReport> promise = Promise.promise();

        try {
            CompilationUnit cu= StaticJavaParser.parse(classSrcFile);

            Set<String> dependencies = new HashSet<>();

            cu.getImports().forEach(declaration -> {
                dependencies.add(declaration.getNameAsString());
            });

            cu.accept(new VoidVisitorAdapter<>() {
                @Override
                public void visit(com.github.javaparser.ast.type.ClassOrInterfaceType n, Set<String> collector) {
                    super.visit(n, collector);
                    collector.add(n.getNameAsString());
                }
            }, dependencies);

            ClassDepsReport report= new ClassDepsReport(classSrcFile.getName(), dependencies);

            promise.complete(report);

        } catch (Exception e) {
            promise.fail(e);
        }

        return promise.future();
    }
}
