package dependencies_GUI.controller;

import dependencies_GUI.model.DependencyAnalyser;
import dependencies_GUI.model.ProjectDepsReport;
import dependencies_GUI.view.DependencyAnalyserGUI;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyAnalyserController {

    private final AtomicInteger classCount = new AtomicInteger(0);
    private final AtomicInteger depCount = new AtomicInteger(0);

    public void start(File rootFolder, DependencyAnalyserGUI dependencyAnalyserGUI){
        DependencyAnalyser dependencyAnalyser = new DependencyAnalyser();
        dependencyAnalyser.analyse(rootFolder, dependencyAnalyserGUI);
    }

    public void updateStats(ProjectDepsReport info, DependencyAnalyserGUI view){
        classCount.incrementAndGet();
        for(String ignored: info.getDependencies()){
            depCount.incrementAndGet();
        }
        view.updateGUI(info, classCount, depCount);
    }
}
