package dependencies_GUI.controller;

import dependencies_GUI.model.ProjectDepsReport;
import dependencies_GUI.view.DependencyAnalyserGUI;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyAnalyserController {

    private final AtomicInteger classCount = new AtomicInteger(0);
    private final AtomicInteger depCount = new AtomicInteger(0);

    public void updateStats(ProjectDepsReport info, DependencyAnalyserGUI view){
        classCount.incrementAndGet();
        for(String ignored: info.getDependencies()){
            depCount.incrementAndGet();
        }
        view.updateUI(info, classCount, depCount);
    }
}
