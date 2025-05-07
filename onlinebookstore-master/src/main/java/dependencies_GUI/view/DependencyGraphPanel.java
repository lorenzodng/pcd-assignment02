package dependencies_GUI.view;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class DependencyGraphPanel extends JPanel {
    private final mxGraph graph;
    private final Object root;
    private final mxGraphComponent graphComponent;
    private final HashMap<String, Object> vertexMap;
    private double zoomFactor = 1.2;


    public DependencyGraphPanel() {
        super(new BorderLayout());
        graph = new mxGraph();
        root = graph.getDefaultParent(); //è un "contenitore" del grafo usato per organizzare i nodi e gli archi
        graphComponent = new mxGraphComponent(graph);
        vertexMap = new HashMap<>();
        add(graphComponent, BorderLayout.CENTER);
        graphComponent.setPreferredSize(new Dimension(500, 500));
    }

    public void addClassWithDependencies(String className, HashSet<String> dependencies) {
        graph.getModel().beginUpdate(); //inizio la costruzione del grafo
        try {
            Object classVertex = vertexMap.computeIfAbsent(className, name -> //se la classe non è presente nel grafo
                    graph.insertVertex(root, null, name, 0, 0, 300, 50)); //creo un nuovo nodo (rettangolo 100x50). Questo rettangolo è quello della classe che contiene le dipendenze

            for (String dep : dependencies) {
                Object depVertex = vertexMap.computeIfAbsent(dep, name ->
                        graph.insertVertex(root, null, name, 0, 0, 300, 50)); //per ogni dipendenza in "dependencies" creo un rettangolo tra la classe e le dipendenze
                graph.insertEdge(root, null, "", classVertex, depVertex); //e associo una freccia tra la classe che contiene le dipendenze e le dipendenze stesse
            }
            mxCircleLayout layout = new mxCircleLayout(graph); //rappresento il grafo come un cerchio
            layout.execute(root); //aggiorno il grafo con i nodi e gli archi calcolati
            graphComponent.zoom(0.05);
        } finally {
            graph.getModel().endUpdate(); //termino la costruzione del grafo
        }
    }

    public void zoomIn() {
        double maxScale = 10.0;
        double currentScale = graph.getView().getScale();
        double newScale = Math.min(currentScale * zoomFactor, maxScale);
        graphComponent.zoomTo(newScale, false);
    }

    public void zoomOut() {
        double minScale = 0.01;
        double currentScale = graph.getView().getScale();
        double newScale = Math.max(currentScale / zoomFactor, minScale);
        graphComponent.zoomTo(newScale, false);
    }
}
