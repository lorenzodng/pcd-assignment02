package dependencies_async.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import static io.vertx.core.Vertx.vertx;
import dependencies_async.model.ClassDepsReport;
import dependencies_async.model.PackageDepsReport;
import dependencies_async.model.ProjectDepsReport;
import io.vertx.core.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

public class DependencyAnalyserLib extends AbstractVerticle{

    private final Vertx vertx;

    public DependencyAnalyserLib(Vertx vertx) {
        this.vertx = vertx;
    }

    public void findClassDependencies(File classSrcFile){
        Future<ClassDepsReport> fut = getClassDependencies(classSrcFile);
        fut.onSuccess((res) -> {
            System.out.println(res.toString());
            vertx.close(ar -> {
                if (ar.succeeded()) {
                    System.exit(0);
                } else {
                    ar.cause().printStackTrace();
                    System.exit(1);
                }
            });
        }).onFailure(Throwable::printStackTrace);    }

    public void findPackageDependencies(File packageSrcFile){
        Future<PackageDepsReport> fut = getPackageDependencies(packageSrcFile);
        fut.onSuccess((res) -> {
            System.out.println(res.toString());
            vertx.close(ar -> {
                if (ar.succeeded()) {
                    System.exit(0);
                } else {
                    ar.cause().printStackTrace();
                    System.exit(1);
                }
            });
        }).onFailure(Throwable::printStackTrace);    }

    public void findProjectDependencies(File projectSrcFile){
        Future<ProjectDepsReport> fut = getProjectDependencies(projectSrcFile);
        fut.onSuccess((res) -> {
            System.out.println(res.toString());
            vertx.close(ar -> {
                if (ar.succeeded()) {
                    System.exit(0);
                } else {
                    ar.cause().printStackTrace();
                    System.exit(1);
                }
            });
        }).onFailure(Throwable::printStackTrace);    }

    private Future<ClassDepsReport> getClassDependencies(File classSrcFile) {
        Promise<ClassDepsReport> mainPromise = Promise.promise();
        HashMap<String, String> imports = new HashMap<>();
        HashSet<String> usedTypes = new HashSet<>();

        vertx().executeBlocking(promise -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(classSrcFile);
                HashMap<String, String> importResults = searchImport(cu);
                imports.putAll(importResults);
                HashSet<String> referencesResults = searchReferences(cu);
                usedTypes.addAll(referencesResults);
                promise.complete();
            } catch (FileNotFoundException e) {
                System.err.println("Error while parsing '" + classSrcFile.getName() + "'");
                e.printStackTrace();
            }
        }).onSuccess((res) -> {
            HashSet<String> dependencies = mergeDependencies(imports, usedTypes);
            ClassDepsReport classDepsReport = new ClassDepsReport(classSrcFile.getName(), dependencies);
            mainPromise.complete(classDepsReport);
        });
        return mainPromise.future();
    }

    private Future<PackageDepsReport> getPackageDependencies(File packageSrcFolder){
        Promise<PackageDepsReport> mainPromise = Promise.promise();
        HashMap<String, String> imports = new HashMap<>();
        HashSet<String> usedTypes = new HashSet<>();

        vertx.executeBlocking(promise -> {
            try {
                Files.walk(packageSrcFolder.toPath()).filter(path -> path.toString().endsWith(".java")).forEach(path -> { //navigo all'interno del package ed esamino tutti i file .java
                    File classFile = path.toFile(); //estrapolo ogni classe presente nel package e la tratto come un file separato
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(classFile);
                        if (cu.getTypes().isEmpty() || !cu.getType(0).isClassOrInterfaceDeclaration()) {
                            return;
                        }
                        HashMap<String, String> importResults = searchImport(cu);
                        imports.putAll(importResults);
                        HashSet<String> referencesResults = searchReferences(cu);
                        usedTypes.addAll(referencesResults);
                    } catch (Exception e) {
                        System.err.println("Error while parsing '" + classFile.getName() + "'");
                        e.printStackTrace();
                    }
                });
                promise.complete();
            } catch (IOException e) {
                System.err.println("Error while visiting directory '" + packageSrcFolder.getName() + "'");
                e.printStackTrace();
            }
        }).onSuccess((res) -> {
            HashSet<String> dependencies = mergeDependencies(imports, usedTypes);
            PackageDepsReport packageDepsReport = new PackageDepsReport(packageSrcFolder.getName(), dependencies);
            mainPromise.complete(packageDepsReport);
        });
        return mainPromise.future();
    }

    private Future<ProjectDepsReport> getProjectDependencies(File projectSrcFolder) {
        Promise<ProjectDepsReport> mainPromise = Promise.promise();
        HashMap<String, String> imports = new HashMap<>();
        HashSet<String> usedTypes = new HashSet<>();
        Future<Void> importFuture = vertx.executeBlocking(promise -> {
            try {
                Files.walk(projectSrcFolder.toPath()).filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                    File classFile = path.toFile();
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(classFile);
                        if (cu.getTypes().isEmpty() || !cu.getType(0).isClassOrInterfaceDeclaration()) {
                            return;
                        }
                        HashMap<String, String> result = searchImport(cu);
                        imports.putAll(result);
                    } catch (Exception e) {
                        System.err.println("Error while parsing '" + classFile.getName() + "'");
                        e.printStackTrace();
                    }
                });
                promise.complete();
            } catch (Exception e) {
                System.err.println("Error while visiting project '" + projectSrcFolder.getName() + "'");
                promise.fail(e);
            }
        });

        Future<Void> referencesFuture = vertx.executeBlocking(promise -> {
            try {
                Files.walk(projectSrcFolder.toPath()).filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                    File classFile = path.toFile();
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(classFile);
                        if (cu.getTypes().isEmpty() || !cu.getType(0).isClassOrInterfaceDeclaration()) {
                            return;
                        }
                        HashSet<String> results = searchReferences(cu);
                        usedTypes.addAll(results);
                    } catch (Exception e) {
                        System.err.println("Error while parsing '" + classFile.getName() + "'");
                        promise.fail(e);
                    }
                });
                promise.complete();
            } catch (IOException e) {
                System.err.println("Error while visiting project '" + projectSrcFolder.getName() + "'");
                promise.fail(e);
            }
        });

        Future.all(importFuture, referencesFuture).onSuccess( res -> {
            vertx.executeBlocking(promise -> {
                HashSet<String> dependencies = mergeDependencies(imports, usedTypes);
                ProjectDepsReport projectDepsReport = new ProjectDepsReport(projectSrcFolder.getName(), dependencies);
                mainPromise.complete(projectDepsReport);
            });
        });

        return mainPromise.future();
    }

    private HashMap<String, String> searchImport(CompilationUnit cu) throws FileNotFoundException {
        HashMap<String, String> imports = new HashMap<>();

        cu.getImports().forEach(imp -> {
            String fullImport = imp.getNameAsString();
            String simpleName = fullImport.substring(fullImport.lastIndexOf('.') + 1);
            imports.put(simpleName, fullImport);
        });
        return imports;
    }

    private HashSet<String> searchReferences(CompilationUnit cu){
        HashSet<String> references = new HashSet<>();

        cu.accept(new VoidVisitorAdapter<>() {
            @Override
            public void visit(ClassOrInterfaceType n, HashSet<String> collector) {
                super.visit(n, collector);
                String typeName = n.getNameAsString();
                if (!isJavaLangType(typeName)) {
                    collector.add(typeName);
                }
            }
        }, references);
        return references;
    }

    private HashSet<String> mergeDependencies(HashMap<String, String> imports, HashSet<String> references) {
        HashSet<String> dependencies = new HashSet<>();

        for (HashMap.Entry<String, String> entry : imports.entrySet()) { //scorro negli import ricavati
            String simpleName = entry.getKey();
            String fullName = entry.getValue();
            if (references.contains(simpleName)) { //se un import è usato
                dependencies.add(simpleName); //metto il nome semplice
            } else {
                dependencies.add(fullName); //altrimenti metto il nome completo
            }
        }

        for (String type : references) {  //scorro nei tipi ricavati
            if (!imports.containsKey(type)) { //aggiungo gli altri tipi usati che non sono tra gli import
                dependencies.add(type);
            }
        }
        return dependencies;
    }

    private boolean isJavaLangType(String typeName) { //per verificare se la dipendenza fa parte di java.lang (escludo queste dipendenze)
        try {
            return Class.forName("java.lang." + typeName).getPackageName().equals("java.lang");
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
