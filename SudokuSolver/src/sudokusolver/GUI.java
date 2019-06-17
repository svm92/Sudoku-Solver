package sudokusolver;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.XYDataset;

public class GUI {
    static boolean dontUpdate = false;
    
    JFrame frame;
    JPanel sudokuPanel;
    JPanel buttonPanel;
    JLabel sudokuVisualPresentation;
    JComboBox solverTypeComboBox;
    JComboBox sudokuList;
    JComboBox sudokuSizes;
    JButton solverButton;
    JButton analyzeButton;
    JButton showDataButton;
    ChartPanel chartPanel;
    
    void start() {
        // Create frame
        frame = new JFrame("Sudoku Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create panels to add to the frame
        sudokuPanel = new JPanel();
        sudokuPanel.setLayout(new FlowLayout());
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        
        // Add panels to frame
        frame.getContentPane().add(BorderLayout.CENTER, sudokuPanel);
        frame.getContentPane().add(BorderLayout.WEST, buttonPanel);
        
        // Create components to add to the panels/frame
        sudokuVisualPresentation = new JLabel();
        
        sudokuList = new JComboBox();
        updateSudokuList();
        sudokuList.addActionListener(new SudokuListComboListener());
        sudokuList.setSelectedIndex(0);
        sudokuList.setMaximumSize(new Dimension(5000, 30));
        
        solverTypeComboBox = new JComboBox(new SudokuSolver.SolvingAlgorithm[] {
            SudokuSolver.SolvingAlgorithm.GENETIC, SudokuSolver.SolvingAlgorithm.BACKTRACKING,
            SudokuSolver.SolvingAlgorithm.CONS_PROP});
        solverTypeComboBox.addActionListener(new SolverTypeComboBoxListener());
        solverTypeComboBox.setSelectedIndex(2);
        solverTypeComboBox.setMaximumSize(new Dimension(5000, 30));
        
        sudokuSizes = new JComboBox(new String[] {"2x2", "3x3", "4x4", "5x5"});
        sudokuSizes.addActionListener(new SudokuSizesComboBoxListener());
        sudokuSizes.setSelectedIndex(1);
        sudokuSizes.setMaximumSize(new Dimension(5000, 20));
        
        solverButton = new JButton("Solve");
        solverButton.addActionListener(new SolverButtonListener());
        
        analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(new AnalyzeButtonListener());
        
        showDataButton = new JButton("Show Data");
        showDataButton.addActionListener(new ShowDataButtonListener());
        
        // Add components to the panels/frame
        sudokuPanel.add(sudokuVisualPresentation);
        buttonPanel.add(new JLabel("Solving algorithm"));
        buttonPanel.add(solverTypeComboBox);
        buttonPanel.add(new JLabel("Sudoku"));
        buttonPanel.add(sudokuList);
        buttonPanel.add(new JLabel("Sudoku Size"));
        buttonPanel.add(sudokuSizes);
        buttonPanel.add(solverButton);
        buttonPanel.add(showDataButton);
        //buttonPanel.add(analyzeButton);
        
        // Initialize visual proprieties
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
    
    void update() {
        if (dontUpdate) return;
        switch (SudokuSolver.solvingAlgorithm) {
            case GENETIC:
                int nSudokuToShow = Math.min(10, Population.POPULATION_SIZE);
                int nSudokuPerRow = 5;

                String sudokuString = "<html><table><tr>";
                for (int i=0; i < nSudokuToShow; i++) {
                    Sudoku sudoku = Population.solutions[i];

                    sudokuString += "<td style='text-align:center'>"
                    + sudoku.HTMLPrint()
                    + "<span style='color:" + getFitnessColor(sudoku.fitness) + "'>" + sudoku.fitness + "</span>"
                    + "</td>";
                    if (i % nSudokuPerRow == nSudokuPerRow - 1) sudokuString += "</tr><tr>";
                }
                sudokuString += "</tr></table>"
                        + "<h2 style='text-align:center'>Generation: " + Population.generation + "</h2>"
                        + "</html>";
                sudokuVisualPresentation.setText(sudokuString);
                break;
            default:
            case BACKTRACKING:
            case CONS_PROP:
                sudokuString = "<html><table><tr>";
                Sudoku sudoku = SudokuSolver.currentSudoku;
                sudokuString += "<td style='text-align:center'>" + sudoku.HTMLPrint() + "</td>";
                sudokuVisualPresentation.setText(sudokuString);
                break;
        }
    }
    
    void plotData(DB.DBDataSet dataSet) {
        // Create data
        XYDataset ds = Plotter.createDataset(dataSet);
        JFreeChart chart = ChartFactory.createXYLineChart("Sudoku Solving Times Chart",
                "Solving Method", "Time", ds);
        // Set axes
        XYPlot chartPlot = (XYPlot)chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis)chartPlot.getRangeAxis();
        rangeAxis.setRange(0, 1);
        //NumberAxis domainAxis = (NumberAxis)chartPlot.getDomainAxis();
        //domainAxis.setRange(0, 10);
        // Plot data
        chartPanel = new ChartPanel(chart);
        showPanel(chartPanel);
    }
    
    void showPanel(JPanel panel) {
        if (sudokuPanel != null) 
            frame.getContentPane().remove(sudokuPanel);
        if (chartPanel != null)
            frame.getContentPane().remove(chartPanel);
        
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.revalidate(); frame.repaint();
    }
    
    void updateSudokuList() {
        sudokuList.removeAllItems();
        for (int i=0; i < SudokuExamples.samples.length; i++)
            sudokuList.addItem(SudokuExamples.samples[i]);
    }
    
    String getFitnessColor(int fitness) { // Green for low (1), red for high (80)
        if (fitness == 0) return "#0000ff"; // Blue for solved sudoku
        
        // Normalize (from 0~80 range to 0~255 range)
        int normalizedFitness = Math.round(fitness * 255f/80f);
        if (normalizedFitness > 255) normalizedFitness = 255;
        // Get hex value
        String R = Integer.toHexString(normalizedFitness);
        String G = Integer.toHexString(255 - normalizedFitness);
        // Add padding 0s to the left
        if (R.length() < 2) R = "0" + R;
        if (G.length() < 2) G = "0" + G;
        return "#" + R + G + "00";
    }
    
    public void enableModifierButtons(boolean enable) {
        solverTypeComboBox.setEnabled(enable);
        sudokuList.setEnabled(enable);
        sudokuSizes.setEnabled(enable);
        solverButton.setEnabled(enable);
        showDataButton.setEnabled(enable);
        analyzeButton.setEnabled(enable);
    }
    
    class SolverButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showPanel(sudokuPanel);
            Runnable r = () -> {
                SudokuSolver.startSolving();
            };
            Thread t = new Thread(r);
            enableModifierButtons(false);
            t.start();
        }
    }
    
    class AnalyzeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showPanel(sudokuPanel);
            Runnable r = () -> {
                DB.connectAndAnalyze();
            };
            Thread t = new Thread(r);
            enableModifierButtons(false);
            t.start();
        }
    }
    
    class ShowDataButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            DB.DBDataSet dataSet = Analyzer.analyzeDBInfo();
            plotData(dataSet);
        }
    }
    
    class SolverTypeComboBoxListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            showPanel(sudokuPanel);
            JComboBox cb = (JComboBox)e.getSource();
            SudokuSolver.SolvingAlgorithm option = (SudokuSolver.SolvingAlgorithm)cb.getSelectedItem();
            SudokuSolver.solvingAlgorithm = option;
            update();
        }
    }
    
    class SudokuListComboListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showPanel(sudokuPanel);
            JComboBox cb = (JComboBox)e.getSource();
            SudokuExamples.SudokuSample option = (SudokuExamples.SudokuSample)cb.getSelectedItem();
            if (option == null) return;
            SudokuSolver.currentSudokuString = option.s;
            
            Population.initialize(true);
            SudokuSolver.currentSudoku = new Sudoku(true);
            update();
        }
    }
    
    class SudokuSizesComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showPanel(sudokuPanel);
            JComboBox cb = (JComboBox)e.getSource();
            String option = (String)cb.getSelectedItem();
            
            switch (option) {
                case "2x2":
                    Sudoku.SIZE = 4;
                    break;
                default:
                case "3x3":
                    Sudoku.SIZE = 9;
                    break;
                case "4x4":
                    Sudoku.SIZE = 16;
                    break;
                case "5x5":
                    Sudoku.SIZE = 25;
                    break;
            }
            SudokuExamples.readAllSamples();
            updateSudokuList();
            
            Population.initialize(true);
            SudokuSolver.currentSudoku = new Sudoku(true);
            update();
        }
    }
    
}
