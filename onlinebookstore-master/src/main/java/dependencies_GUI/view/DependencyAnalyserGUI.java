package dependencies_GUI.view;

import dependencies_GUI.model.DependencyAnalyser;
import dependencies_GUI.model.ProjectDepsReport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyAnalyserGUI {

    private final JButton startButton;
    private final JButton browseButton;
    private final JLabel selectedFolderLabel;
    private final JTextArea outputArea;
    private final JLabel classCountLabel;
    private final JLabel depCountLabel;
    private final DependencyGraphPanel graphPanel;
    private final JFrame frame;

    private final JButton zoomInButton = new JButton("+");
    private final JButton zoomOutButton = new JButton("–");

    public DependencyAnalyserGUI() {
        frame = new JFrame("Dependencies");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());

        // componenti
        browseButton = new JButton("Browse Folder");
        startButton = new JButton("Start");
        selectedFolderLabel = new JLabel("No project selected");
        classCountLabel = new JLabel("Classes/Interfaces: 0");
        depCountLabel = new JLabel("Dependencies: 0");
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollPane = new JScrollPane(outputArea); //spazio sottostante dei contenuti

        JPanel browseButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        browseButtonPanel.add(browseButton);

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = chooser.getSelectedFile();
                selectedFolderLabel.setText("Selected: " + selectedFolder.getAbsolutePath());
            }
        });

        startButton.addActionListener(this::startSimulation);

        JPanel selectedLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectedLabelPanel.add(selectedFolderLabel);

        JPanel startButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButtonPanel.add(startButton);

        // unisco tutto in verticale
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.add(browseButtonPanel);
        controlPanel.add(selectedLabelPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(startButtonPanel);

        // statistiche
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsPanel.add(classCountLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(depCountLabel);

        // aggiungo i pannelli
        cp.add(BorderLayout.NORTH, controlPanel);

        // pannello del grafo
        graphPanel = new DependencyGraphPanel();

        // pannello per il controllo dello zoom
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Cambiato FlowLayout per centratura
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);

        // aggiungo il pannello dello zoom direttamente in basso al pannello del grafo
        JPanel graphWithZoomPanel = new JPanel();
        graphWithZoomPanel.setLayout(new BorderLayout());
        graphWithZoomPanel.add(graphPanel, BorderLayout.CENTER);
        graphWithZoomPanel.add(zoomPanel, BorderLayout.SOUTH);  // Posiziona il pannello di zoom in basso

        // separo il grafo e l'output area
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, graphWithZoomPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);

        cp.add(BorderLayout.CENTER, splitPane);

        // aggiungo azioni per i pulsanti di zoom
        zoomInButton.addActionListener(e -> graphPanel.zoomIn());
        zoomOutButton.addActionListener(e -> graphPanel.zoomOut());

        // aggiungo il pannello per le statistiche in basso
        cp.add(BorderLayout.SOUTH, statsPanel);

        frame.setContentPane(cp);
    }

    private void startSimulation(ActionEvent e) {
        if (selectedFolderLabel.getText().equals("No project selected")) {
            JOptionPane.showMessageDialog(frame, "Select a project first.");
        }else{
            File rootFolder = new File(selectedFolderLabel.getText().replace("Selected: ", ""));
            outputArea.append(" Finding dependencies in \"" + rootFolder.getName() + "\"...\n\n");
            DependencyAnalyser dependencyAnalyser = new DependencyAnalyser();
            dependencyAnalyser.analyse(rootFolder, this);
        }
    }

    public void updateUI(ProjectDepsReport info, AtomicInteger classCount, AtomicInteger depCount){
        SwingUtilities.invokeLater(() -> {
            classCountLabel.setText("Classes/Interfaces: " + classCount);
            depCountLabel.setText("Dependencies: " + depCount);
            outputArea.append(info.toString());
            graphPanel.addClassWithDependencies(info.getName(), info.getDependencies());
        });
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });
    }


}
