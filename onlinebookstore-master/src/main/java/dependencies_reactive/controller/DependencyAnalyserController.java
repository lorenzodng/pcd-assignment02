package dependencies_reactive.controller;

import dependencies_reactive.model.DependencyAnalyser;
import dependencies_reactive.model.ProjectDepsReport;
import dependencies_reactive.view.DependencyAnalyserGUI;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyAnalyserController {

    private final AtomicInteger classCount = new AtomicInteger(0);
    private final AtomicInteger depCount = new AtomicInteger(0);

    public void start(File rootFolder, DependencyAnalyserGUI dependencyAnalyserGUI){
        DependencyAnalyser dependencyAnalyser = new DependencyAnalyser();
        dependencyAnalyser.analyse(rootFolder, dependencyAnalyserGUI);
    }

    public void updateDependencies(ProjectDepsReport info, DependencyAnalyserGUI view){
        classCount.incrementAndGet();
        for(String ignored: info.getDependencies()){
            depCount.incrementAndGet();
        }
        view.updateGUI(info, classCount, depCount);
    }
}
