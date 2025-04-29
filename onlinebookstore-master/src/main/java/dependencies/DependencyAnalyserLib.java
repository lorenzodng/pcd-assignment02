package dependencies;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import static io.vertx.core.Vertx.vertx;
import io.vertx.core.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

public class DependencyAnalyserLib extends AbstractVerticle{

    private final Vertx vertx;

    public DependencyAnalyserLib(Vertx vertx) {
        this.vertx = vertx;

    }

    public void getClassDependencies(File classSrcFile) {

        Future<ClassDepsReport> res = vertx().executeBlocking(() -> {

            CompilationUnit cu = StaticJavaParser.parse(classSrcFile); //creo l'oggetto che mi consente di ottenere le dipendenze presenti nella classe

            HashMap<String, String> imports = new HashMap<>();
            cu.getImports().forEach(imp -> { //ricavo tutti gli import presenti nella classe
                String fullImport = imp.getNameAsString();
                String importName = fullImport.substring(fullImport.lastIndexOf('.') + 1);
                imports.put(fullImport, importName); //li aggiungo a una lista
            });

            HashSet<String> dependencies = new HashSet<>(imports.values()); //aggiungo solo i nomi degli import in un'altra lista
            cu.accept(new VoidVisitorAdapter<>() { //ricavo tutte le dipendenze presenti dalla classe in giù (esclusi import)
                @Override
                public void visit(ClassOrInterfaceType n, HashSet<String> collector) {
                    super.visit(n, collector);
                    String typeName = n.getNameAsString();
                    if (!dependencies.contains(typeName) && !isJavaLangType(typeName)) {
                        collector.add(typeName); //considero solo le dipendenze non presenti nell'import (cu.accept prende anche implements, extends ecc. che potrebbero già essere stati recuperati come import)
                    }
                }
            }, dependencies); //aggiungo tutto nella lista dependencies

            return new ClassDepsReport(classSrcFile.getName(), dependencies); //restituisco il risultato
        });

       res.onSuccess(System.out::println).onFailure(Throwable::printStackTrace);
    }

    public void getPackageDependencies(File packageSrcFolder){

        Future<PackageDepsReport> res = vertx.executeBlocking(() -> {
            HashMap<String, HashSet<String>> classToDependencies = new HashMap<>(); //utilizzo una hashmap per associare ogni classe(key) alle sue dipendenze
            try{
                Files.walk(packageSrcFolder.toPath()).filter(path -> path.toString().endsWith(".java")).forEach(path -> { //navigo all'interno del package ed esamino tutti i file .java
                    File classFile = path.toFile(); //estrapolo ogni classe presente nel package e la tratto come un file separato
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(classFile);

                        HashMap<String, String> imports = new HashMap<>();
                        cu.getImports().forEach(imp -> {
                            String fullImport = imp.getNameAsString();
                            String importName = fullImport.substring(fullImport.lastIndexOf('.') + 1);
                            imports.put(fullImport, importName);
                        });

                        HashSet<String> dependencies = new HashSet<>(imports.values()); //questa conterrà solo le dipendenze trovate per la classe
                        cu.accept(new VoidVisitorAdapter<>() {
                            @Override
                            public void visit(ClassOrInterfaceType n, HashSet<String> collector) {
                                super.visit(n, collector);
                                String typeName = n.getNameAsString();
                                if (!dependencies.contains(typeName) && !isJavaLangType(typeName)) {
                                    collector.add(typeName);
                                }
                            }
                        }, dependencies);

                        classToDependencies.put(classFile.getName(), dependencies); //qui aggiungo di volta in volta tutte le classi del package con le rispettive dipendenze

                    } catch (Exception e) {
                        System.err.println("Error while parsing '" + classFile.getName() + "'");
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("Error while visiting directory '" + packageSrcFolder.getName() + "'");
                e.printStackTrace();
            }
            return new PackageDepsReport(packageSrcFolder.getName(), classToDependencies);
        });

        res.onSuccess(System.out::println).onFailure(Throwable::printStackTrace);
    }

    public void getProjectDependencies(File projectSrcFolder) {

        Future<ProjectDepsReport> res = vertx.executeBlocking(() -> {
            HashSet<String> projectDependencies = new HashSet<>();
            try {
                Files.walk(projectSrcFolder.toPath()).filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                    File classFile = path.toFile();
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(classFile);
                        HashMap<String, String> imports = new HashMap<>();

                        cu.getImports().forEach(imp -> {
                            String fullImport = imp.getNameAsString();
                            String importName = fullImport.substring(fullImport.lastIndexOf('.') + 1);
                            imports.put(fullImport, importName);
                        });

                        HashSet<String> dependencies = new HashSet<>(imports.values());
                        cu.accept(new VoidVisitorAdapter<>() {
                            @Override
                            public void visit(ClassOrInterfaceType n, HashSet<String> collector) {
                                super.visit(n, collector);
                                String typeName = n.getNameAsString();
                                if (!collector.contains(typeName) && !isJavaLangType(typeName)) {
                                    collector.add(typeName);
                                }
                            }
                        }, dependencies);

                        projectDependencies.addAll(dependencies);

                    } catch (Exception e) {
                        System.err.println("Error while parsing '" + classFile.getName() + "'");
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("Error while walking through project folder: " + projectSrcFolder.getName());
                e.printStackTrace();
            }

            return new ProjectDepsReport(projectSrcFolder.getName(), projectDependencies);
        });

        res.onSuccess(System.out::println).onFailure(Throwable::printStackTrace);
    }


    private boolean isJavaLangType(String typeName) { //per verificare se la dipendenza fa parte di java.lang (escludo queste dipendenze)
        try {
            return Class.forName("java.lang." + typeName).getPackageName().equals("java.lang");
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
