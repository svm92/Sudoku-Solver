package sudokusolver;

import java.util.*;

/**
 * Represents a population of Sudokus for genetic algorithms.
 */
public class Population {
    public static final int POPULATION_SIZE = 10; // Should be even
    
    public static int generation = 0;
    public static Sudoku[] solutions;
    
    static ArrayList<Integer> bestFitnessPerGeneration = new ArrayList<>();
    
    public static void initialize() {
        initialize(false);
    }
    
    public static void initialize(boolean quickInit) {
        solutions = new Sudoku[POPULATION_SIZE];
        for (int i=0; i < POPULATION_SIZE; i++) {
            solutions[i] = new Sudoku(quickInit);
        }
        if (quickInit) return;
        calculateFitness();
        sortByFitness();
        bestFitnessPerGeneration.add(solutions[0].fitness);
    }
        
    public static void populate() {
        // If it doesn't improve in many generations, restart
        /*if (generation > 100000 && 
                bestFitnessPerGeneration.get(generation - 1).intValue() == 
                bestFitnessPerGeneration.get(generation - 100000).intValue()) {
            initialize();
            return;
        }*/
        
        // Select half of the sudoku with best fitness -> Generate 2 children each. Discard the rest
        int nBestSolutionsToTake = POPULATION_SIZE / 2; // Enough to fully repopulate
        ArrayList<Sudoku> survivingSolutions = new ArrayList<>();
        
        for (int i=0; i < nBestSolutionsToTake; i++) { // Fill array with best solutions (lowest fitness)
            survivingSolutions.add(solutions[i]);
        }
        
        for (int i=0; i < survivingSolutions.size(); i++) { // Create two children for each
            solutions[2*i] = survivingSolutions.get(i).createClone();
            solutions[2*i + 1] = survivingSolutions.get(i).createClone();
            solutions[2*i].mutate();
            solutions[2*i + 1].mutate();
        }
        
        calculateFitness();
        sortByFitness();
        bestFitnessPerGeneration.add(solutions[0].fitness);
    }
    
    public static void calculateFitness() {
        for (Sudoku sudoku : solutions) {
            sudoku.calculateFitness();
        }
    }
    
    static void sortByFitness() {
        Arrays.sort(solutions, new Sudoku.FitnessComparator());
    }
    
    public static boolean solvedSudokuFound() {
        for (Sudoku sudoku : solutions) {
            if (sudoku.isSolved()) return true;
        }
        return false;
    }
    
}
