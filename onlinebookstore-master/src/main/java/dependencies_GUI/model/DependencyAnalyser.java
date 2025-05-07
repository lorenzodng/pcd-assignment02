package dependencies_GUI.model;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dependencies_GUI.controller.DependencyAnalyserController;
import dependencies_GUI.view.DependencyAnalyserGUI;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class DependencyAnalyser {

    private final DependencyAnalyserController dependencyAnalyserController;

    public DependencyAnalyser() {
        dependencyAnalyserController = new DependencyAnalyserController();
    }

    public void analyse(File rootFolder, DependencyAnalyserGUI view) {
        System.out.println("Analysing " + rootFolder.getAbsoluteFile());

        Observable.fromIterable(getAllJavaFiles(rootFolder))
                .subscribeOn(Schedulers.io())  //eseguo l'analisi su un pool di thread
                .map(file -> analyseClass(file))
                .filter(result -> !result.getType().equals("Skipped"))//analizzo ogni file
                .subscribe(info -> {
                    dependencyAnalyserController.updateStats(info, view);
                });
    }

    private List<File> getAllJavaFiles(File dir) {
        List<File> files = new ArrayList<>();
        try {
            Files.walk(dir.toPath()).filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                File classFile = path.toFile();
                files.add(classFile);
            });
        } catch (IOException e) {
            System.err.println("Error while visiting project '" + dir.getName() + "'");
            e.printStackTrace();
        }
        return files;
    }

    private ProjectDepsReport analyseClass(File file) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(file);
        HashMap<String, String> imports = searchImport(cu);
        HashSet<String> usedTypes = searchUsedType(imports, cu);
        String type = "Unknown";
        if (!cu.getTypes().isEmpty()) {
            var typeDecl = cu.getType(0);
            if (!typeDecl.isClassOrInterfaceDeclaration()) {
                return new ProjectDepsReport("Skipped", file.getName(), new HashSet<>());
            }
            type = typeDecl.asClassOrInterfaceDeclaration().isInterface() ? "Interface" : "Class";
        }

        Thread.sleep(1000);

        return new ProjectDepsReport(type, file.getName(), usedTypes);
    }

    private HashMap<String, String> searchImport(CompilationUnit cu) {
        HashMap<String, String> imports = new HashMap<>();
        cu.getImports().forEach(imp -> {
            String fullImport = imp.getNameAsString();
            String simpleName = fullImport.substring(fullImport.lastIndexOf('.') + 1);
            imports.put(simpleName, fullImport);
        });
        return imports;
    }

    private HashSet<String> searchUsedType(HashMap<String, String> imports, CompilationUnit cu){
        HashSet<String> usedTypes = new HashSet<>();
        cu.accept(new VoidVisitorAdapter<>() {
            @Override
            public void visit(ClassOrInterfaceType n, HashSet<String> collector) {
                super.visit(n, collector);
                String typeName = n.getNameAsString();
                if (!imports.containsKey(typeName) && isJavaLangType(typeName)) {
                    usedTypes.add(typeName);
                } else if (isJavaLangType(typeName)){
                    usedTypes.add(imports.get(typeName));
                }
            }
        }, usedTypes);
        return usedTypes;
    }

    private boolean isJavaLangType(String typeName) {
        try {
            return !Class.forName("java.lang." + typeName).getPackageName().equals("java.lang");
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

}
