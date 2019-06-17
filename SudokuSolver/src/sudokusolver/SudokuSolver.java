package sudokusolver;

public class SudokuSolver {
    
    public static final int N_CYCLES = 1000000000;
    
    public static Sudoku currentSudoku;
    public static String currentSudokuString;
    
    public enum SolvingAlgorithm {GENETIC, BACKTRACKING, CONS_PROP;
        @Override
        public String toString() {
            switch (this) {
                case GENETIC: return "Genetic Algorithm";
                case BACKTRACKING: return "Backtracking Algorithm";
                default: case CONS_PROP: return "Constraint Propagation";
            }
        }
    };
    public static SolvingAlgorithm solvingAlgorithm = SolvingAlgorithm.CONS_PROP;
    
    public static GUI gui;
    
    public static void main(String[] args) {
        SudokuExamples.readAllSamples();
        gui = new GUI();
        gui.start();
    }
    
    static long startSolving() {
        long startTime = System.nanoTime();
        switch (solvingAlgorithm) {
            case GENETIC:
                // Genetic algorithm (VERY slow, might not find solution)
                // Generation 0
                Population.initialize();
                gui.update();

                // Next generations
                for (; Population.generation < N_CYCLES; Population.generation++) {
                    Population.populate();
                    if (Population.generation % 10000 == 0) gui.update();
                    if (Population.solvedSudokuFound()) {
                        break;
                    }
                }
                gui.update();
                break;
            case BACKTRACKING:
                // Backtracking algorithm (fast, will always find solution)
                currentSudoku = new Sudoku();
                gui.update();
                currentSudoku.solveByBacktracking();
                gui.update();
                break;
            case CONS_PROP:
                // Constraint propagation + Backtracking (fastest, will always find solution)
                currentSudoku = new Sudoku();
                gui.update();
                currentSudoku.solveByPropagation();
                gui.update();
                currentSudoku.solveByBacktracking();
                gui.update();
                break;
        }
        long elapsedTime = System.nanoTime() - startTime;
        if (!GUI.dontUpdate) gui.enableModifierButtons(true);
        return elapsedTime;
    }

}
