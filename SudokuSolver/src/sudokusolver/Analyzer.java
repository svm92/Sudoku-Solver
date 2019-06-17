package sudokusolver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Analyzer {
    
    public static void start(Connection con) {
        for (SudokuExamples.SudokuSample sudoku : SudokuExamples.samples) {
            System.out.println("Checking Sudoku " + (sudoku.id+1) + "/" + SudokuExamples.samples.length);
            String s = sudoku.s;
            SudokuSolver.currentSudokuString = s;
            // Solve for pure backtracking method
            SudokuSolver.solvingAlgorithm = SudokuSolver.SolvingAlgorithm.BACKTRACKING;
            long backtrackTime = SudokuSolver.startSolving();
             // Solve for constant propagation + backtracking
            SudokuSolver.solvingAlgorithm = SudokuSolver.SolvingAlgorithm.CONS_PROP;
            long consPropTime = SudokuSolver.startSolving();
            DB.addDataToTable(con, s, backtrackTime, consPropTime);
        }
    }
    
    public static DB.DBDataSet analyzeDBInfo() {
        DB.DBDataSet dataSet = DB.connectAndGetData();
        // Stop in case of error
        if (dataSet == null || dataSet.backtrackTimes.isEmpty()) {
            System.out.println("Couldn't get data from database.");
            return null;
        }
        if (dataSet.backtrackTimes.size() != dataSet.consPropTimes.size()) {
            System.out.println("Mismatching sizes");
            return null;
        }
        
        // Gather and show data
        long totalTimeB = 0, totalTimeC = 0;
        for (int i=0; i < dataSet.backtrackTimes.size(); i++) {
            totalTimeB += dataSet.backtrackTimes.get(i);
            totalTimeC += dataSet.consPropTimes.get(i);
        }
        double avgTimeB = (totalTimeB / (double)dataSet.backtrackTimes.size()) / 1000000000d;
        double avgTimeC = (totalTimeC / (double)dataSet.consPropTimes.size()) / 1000000000d;
        double medianTimeB = getMedian(dataSet.backtrackTimes) / 1000000000d;
        double medianTimeC = getMedian(dataSet.consPropTimes) / 1000000000d;
        double minTimeB = Collections.min(dataSet.backtrackTimes) / 1000000000d;
        double minTimeC = Collections.min(dataSet.consPropTimes) / 1000000000d;
        double maxTimeB = Collections.max(dataSet.backtrackTimes) / 1000000000d;
        double maxTimeC = Collections.max(dataSet.consPropTimes) / 1000000000d;
        
        System.out.println("Data for Backtracking - Constraint Propagation:");
        System.out.format("Average time: \t %.3fs \t %.3fs%n", avgTimeB, avgTimeC);
        System.out.format("Median time: \t %.3fs \t %.3fs%n", medianTimeB, medianTimeC);
        System.out.format("Min~Max time: \t %.3f~%.3fs \t %.3f~%.3fs%n", minTimeB, maxTimeB, minTimeC, maxTimeC);
        
        
        //System.out.println(dataSet.consPropTimes.indexOf(Collections.max(dataSet.consPropTimes)));
        
        return dataSet;
    }
    
    static long getMedian(ArrayList<Long> list) {
        long[] array = new long[list.size()];
        for (int i=0; i < list.size(); i++) array[i] = list.get(i);
        Arrays.sort(array);
        long median;
        if (array.length % 2 == 0)
            median = ((long)array[array.length/2] + (long)array[array.length/2 - 1])/2;
        else
            median = (long) array[array.length/2];
        return median;
    }
    
}
