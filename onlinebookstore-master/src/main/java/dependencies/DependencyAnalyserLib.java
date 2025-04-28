package dependencies;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.vertx.core.*;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class DependencyAnalyserLib {

    public Future<ClassDepsReport> getClassDependencies(File classSrcFile) {
        Promise<ClassDepsReport> promise = Promise.promise(); //creo una promise

        try {
            CompilationUnit cu = StaticJavaParser.parse(classSrcFile); //creo l'oggetto che mi consente di ottenere le dipendenze dal file

            HashMap<String, String> imports = new HashMap<>();
            cu.getImports().forEach(imp -> {
                String fullImport = imp.getNameAsString();
                String importName = fullImport.substring(fullImport.lastIndexOf('.') + 1);
                imports.put(fullImport, importName);
            });

            HashSet<String> dependencies = new HashSet<>();
            dependencies.addAll(imports.values());
            cu.accept(new VoidVisitorAdapter<>() {
                @Override
                public void visit(com.github.javaparser.ast.type.ClassOrInterfaceType n, HashSet<String> collector) {
                    super.visit(n, collector);
                    String typeName = n.getNameAsString();

                    if (!dependencies.contains(typeName)) {
                        collector.add(typeName);
                    }
                }
            }, dependencies);

            ClassDepsReport report = new ClassDepsReport(classSrcFile.getName(), dependencies);

            promise.complete(report);

        } catch (Exception e) {
            promise.fail(e);
        }

        return promise.future();
    }
}
