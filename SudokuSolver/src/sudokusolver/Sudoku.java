package sudokusolver;

import java.util.*;
import java.util.stream.IntStream;

// For 81-digit sample strings, check: http://forum.enjoysudoku.com/patterns-game-results-t6291.html

public class Sudoku {
    
    public static int SIZE = 9; // Must be a perfect square (4, 9, 16...)
    
    // 0s are blanks
    // Blank
    /*static int[][] originalTemplate = new int[][] {
        {0, 0, 0, 0, 0, 0, 0, 0, 0}, 
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };*/
    static int[][] originalTemplate;
    
    int[][] values;
    
    // Genetic algorithm variables
    public static double MUTATION_CHANCE = 0.01;
    int fitness = 0;
    
    // Backtracking variables
    ArrayList<EmptyCell> emptyCells;
    public static class EmptyCell {
        int x, y;
        EmptyCell(int x, int y) {this.x = x; this.y = y;};

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final EmptyCell other = (EmptyCell) obj;
            return (this.x == other.x && this.y == other.y);
        }

        @Override
        public int hashCode() {
            return this.x + 100 * this.y;
        }
    }
    enum UnitType {ROW, COL, BOX};
    
    public Sudoku() {
        this(false);
    }
    
    /**
     * @param quickInit If true, no values are calculated (cells remain empty).
     */
    public Sudoku(boolean quickInit) {
        originalTemplate = parseSudokuString();
        values = getDeepCopyOf(originalTemplate);
        if (quickInit) return;
        
        switch (SudokuSolver.solvingAlgorithm) {
            case GENETIC:
                fillAtRandom();
                break;
            case BACKTRACKING:
            case CONS_PROP:
                getAllEmptyCells();
                break;
        }
    }
    
    final void fillAtRandom() {
        for (int i=0; i < SIZE; i++) {
            for (int j=0; j < SIZE; j++) {
                if (originalTemplate[i][j] == 0) values[i][j] = getRandomValue();
            }
        }
    }
    
    /**
     * Returns a random int between 1 and SIZE (normally 1~9).
     */
    int getRandomValue() {
        return (int)Math.floor(Math.random() * SIZE) + 1;
    }
    
    /**
     * Used only for genetic algorithm. Calculates fitness function.
     */
    public void calculateFitness() {
        fitness = getNDuplicatesInRows() + getNDuplicatesInCols() + getNDuplicatesInBoxes();
    }
    
    /**
     * Randomly mutates every non-fixed cell in the Sudoku with a chance of MUTATION_CHANCE per cell.
     */
    public void mutate() {
        for (int i=0; i < SIZE; i++) {
            for (int j=0; j < SIZE; j++) {
                if (originalTemplate[i][j] == 0) { // Can be changed (is a blank)
                    if (Math.random() < MUTATION_CHANCE) {
                        mutateValue(i, j);
                    }
                }
            }
        }
    }
    
    /**
     * Randomly changes the value of cell at position (i,j).
     */
    void mutateValue(int i, int j) {
        int originalValue = values[i][j];
        while (values[i][j] == originalValue) { // Ensure it's different
            values[i][j] = getRandomValue();
        }
    }
    
    public void solveByBacktracking() {
        int index = 0;
        while (emptyCells.size() > 0 && index < emptyCells.size()) {
            if (Math.random() < 0.0000001) SudokuSolver.gui.update();
            EmptyCell cell = emptyCells.get(index);
            if (values[cell.x][cell.y] < SIZE) { // Increment value by one if there are still available values
                values[cell.x][cell.y]++;
                if (valueIsValid(cell)) { // If valid, move on to next cell
                    index++;
                }
                // Else, try current cell again with a new value
            } 
            else { // Backtrack
                values[cell.x][cell.y] = 0; // Set to empty again, then check previous cell again
                index--;
            }
        }
    }
    
    final void getAllEmptyCells() {
        emptyCells = new ArrayList<>();
        for (int i=0; i < SIZE; i++) {
            for (int j=0; j < SIZE; j++) {
                if (originalTemplate[i][j] == 0) {
                    emptyCells.add(new EmptyCell(i, j));
                }
            }
        }
    }
    
    public void solveByPropagation() {
        while (true) {
            int filledCells = 0;
            filledCells += checkAllCellValues(); // Check for cells with only one valid value
            filledCells += checkAllUnitValues(); // Checks rows/cols/boxes for values with only one valid position
            if (filledCells == 0) // If nothing added this loop, stop
                break;
        }
    }
    
    /**
     * For a given cell, return an array with every allowed value.
     */
    int[] getAllPossibleValuesOfCell(EmptyCell cell) {
        ArrayList<Integer> possibleValuesList = new ArrayList<>();
        for (int i=1; i <= SIZE; i++) {
            values[cell.x][cell.y] = i;
            if (valueIsValid(cell)) {
                possibleValuesList.add(i);
            }
        }
        
        // List to array
        int[] possibleValues = new int[possibleValuesList.size()];
        for (int i=0; i < possibleValuesList.size(); i++) {
            possibleValues[i] = possibleValuesList.get(i);
        }
        
        values[cell.x][cell.y] = 0; // Empty cell again
        return possibleValues;
    }
    
    /**
     * Check for any cells that have only one valid value.
     * @return number of filled cells.
     */
    int checkAllCellValues() {
        int index = 0;
        int filledCells = 0;
        while (index < emptyCells.size()) {
            EmptyCell cell = emptyCells.get(index);

            // Test possible values
            int[] possibleValues = getAllPossibleValuesOfCell(cell);
            if (possibleValues.length == 1) { // If only one possible value, fill it
                values[cell.x][cell.y] = possibleValues[0];
                filledCells++;
                emptyCells.remove(index);
            }
            else { // Otherwise, move to next cell
                index++;
            }
        }
        return filledCells;
    }
    
    /**
     * Check every row/col/box for any value that can only go in a particular cell.
     * @return number of filled cells.
     */
    int checkAllUnitValues() {
        int filledCells = 0;
        int index = 0;
        while (index < emptyCells.size()) {
            EmptyCell cell = emptyCells.get(index);
            
            // Check possible values
            int[] possibleValues = getAllPossibleValuesOfCell(cell);
            
            // For each unit type (row/column/box), check if there is any value that can't go anywhere
            // else in that unit. If so, fill this cell with that value
            UnitType[] units = new UnitType[] {UnitType.ROW, UnitType.COL, UnitType.BOX};
            for (UnitType unit : units) {
                if (fillIfValueNotAdmittedInRestOfUnit(cell, possibleValues, index, unit)) {
                    filledCells++;
                    break;
                }
            }
            
            // If cell didn't fill (still 0), move to next index
            // If it was filled, old cell was removed from array, so index should not advance
            if (values[cell.x][cell.y] == 0)
                index++;
        }
        return filledCells;
    }
    
    /**
     * Check if there's any value that can't go anywhere else in this row/col/box, and if so, fill it in.
     * @return True if the cell was filled.
     */
    boolean fillIfValueNotAdmittedInRestOfUnit(EmptyCell cell, int[] possibleValues, int index, UnitType unit) {
        for (int n : possibleValues) {
            boolean valueCanOnlyGoHere = true;
            for (EmptyCell c : emptyCells) {
                if (inSameUnit(cell, c, unit)) { // For each empty cell in same row/col/box (except self)
                    int[] possibleValuesOfC = getAllPossibleValuesOfCell(c);
                    // If any other cell can hold that number, stop checking
                    if (IntStream.of(possibleValuesOfC).anyMatch(x -> x == n)) {
                        valueCanOnlyGoHere = false;
                        break;
                    }
                }
            }
            if (valueCanOnlyGoHere) { // If it found no other rows that can hold that number, fill it in
                return fixInValueInCell(cell, n, index);
            }
        }
        return false;
    }
    
    boolean fixInValueInCell(EmptyCell cell, int n, int index) {
            values[cell.x][cell.y] = n;
            emptyCells.remove(index);
            return true;
    }
    
    /**
     * Checks if two cells are in the same unit (row, column, box).
     */
    boolean inSameUnit(EmptyCell c1, EmptyCell c2, UnitType unit) {
        if (c1.equals(c2)) return false; // If self, ignore
        switch (unit) {
            case ROW: // Same row if they share x
                return c1.x == c2.x;
            case COL: // Same column if they share y
                return c1.y == c2.y;
            case BOX: // Same box if, when checking all cells in c1, one of them is c2
                return (getEmptyCellsInBox(c1).stream().anyMatch((c) -> (c.equals(c2))));
            default:
                return false;
        }
    }
    
    boolean valueIsValid(EmptyCell cell) {
        return (valueIsValidInRow(cell) && valueIsValidInColumn(cell) && valueIsValidInBox(cell));
    }
    
    boolean valueIsValidInRow(EmptyCell cell) {
        return (getNDuplicatesInArray(values[cell.x]) == 0);
    }
    
    boolean valueIsValidInColumn(EmptyCell cell) {
        int[] column = getColumn(cell.y);
        return (getNDuplicatesInArray(column) == 0);
    }
    
    boolean valueIsValidInBox(EmptyCell cell) {
        int[] box = getBox(cell);
        return (getNDuplicatesInArray(box) == 0);
    }
    
    int[] getColumn(int x) {
        int[] column = new int[SIZE];
        for (int j=0; j < SIZE; j++) {
            column[j] = values[j][x];
        }
        return column;
    }
    
    int[] getBox(EmptyCell cell) {
        int[] box = new int[SIZE];
        int boxSize = (int)Math.sqrt(SIZE);
        // Values i, j are the location of upper-left cell of this box
        int i = Math.floorDiv(cell.x, boxSize) * boxSize;
        int j = Math.floorDiv(cell.y, boxSize) * boxSize;
        for (int x=i, k=0; x < i+boxSize; x++) {
            for (int y=j; y < j+boxSize; y++) { // For each cell inside the inner square
                box[k] = values[x][y];
                k++;
            }
        }
        return box;
    }
    
    ArrayList<EmptyCell> getEmptyCellsInBox(EmptyCell cell) {
        ArrayList<EmptyCell> boxCells = new ArrayList<>();
        int boxSize = (int)Math.sqrt(SIZE);
        // Values i, j are the location of upper-left cell of this box
        int i = Math.floorDiv(cell.x, boxSize) * boxSize;
        int j = Math.floorDiv(cell.y, boxSize) * boxSize;
        for (int x=i; x < i+boxSize; x++) {
            for (int y=j; y < j+boxSize; y++) { // For each cell inside the inner square
                if (values[x][y] == 0) { // If empty, add it
                    boxCells.add(new EmptyCell(x, y));
                }
            }
        }
        return boxCells;
    }
    
    public boolean isSolved() {
        if (fitness == 0) calculateFitness(); // Check again (for tests)
        return (fitness == 0);
    }
    
    int getNDuplicatesInArray(int[] array) {
        int[] rowToEvaluate = Arrays.copyOf(array, array.length);
        Arrays.sort(rowToEvaluate);
        return countDuplicatesInSortedArray(rowToEvaluate);
    }
    
    int getNDuplicatesInRows() {
        int nDupes = 0;
        for (int[] row : values) {
            nDupes += getNDuplicatesInArray(row);
        }
        return nDupes;
    }
    
    int getNDuplicatesInCols() {
        int nDupes = 0;
        for (int i=0; i < SIZE; i++) {
            int[] column = getColumn(i);
            nDupes += getNDuplicatesInArray(column);
        }
        return nDupes;
    }
    
    int getNDuplicatesInBoxes() {
        int nDupes = 0;
        int boxSize = (int)Math.sqrt(SIZE);
        for (int i=0; i < SIZE; i += boxSize) {
            for (int j=0; j < SIZE; j += boxSize) { // For each big inner square (3x3)
                int[] squareToEvaluate = new int[SIZE];
                int k = 0;
                for (int x=i; x < i+boxSize; x++) {
                    for (int y=j; y < j+boxSize; y++) { // For each cell inside the inner square
                        squareToEvaluate[k] = values[x][y];
                        k++;
                    }
                }
                
                Arrays.sort(squareToEvaluate);
                nDupes += countDuplicatesInSortedArray(squareToEvaluate);
            }
        }
        return nDupes;
    }
    
    int countDuplicatesInSortedArray(int[] a) { // Ignore 0s (they are empty cells)
        int nDupes = 0;
        for (int i=1; i < a.length; i++) {
            if (a[i] == a [i-1] && a[i] != 0) nDupes++;
        }
        return nDupes;
    }
    
    public String HTMLPrint() {
        String padding, fontSize;
        switch (SudokuSolver.solvingAlgorithm) {
            case GENETIC:
                if (SIZE <= 9) {
                    padding = "0 5px";
                    fontSize = "100%";
                }
                else {
                    padding = "0 2px";
                    fontSize = "95%";
                }
                break;
            default:
            case BACKTRACKING:
            case CONS_PROP:
                if (SIZE <= 9) {
                    padding = "10px 20px";
                    fontSize = "140%";
                }
                else if (SIZE <= 16) {
                    padding = "5px 10px";
                    fontSize = "120%";
                }
                else {
                    padding = "2px 5px";
                    fontSize = "110%";
                }
                break;
        }
        
        String HTMLText = "<table style='border-spacing: 0;'>";
        for (int i=0; i < SIZE; i++) {
            HTMLText += "<tr>";
            
            for (int j=0; j < SIZE; j++) {
                // Table data tag
                HTMLText += "<td style='border:1px solid black; padding: " + padding + ";";
                HTMLText += (isColoredSquare(i, j)) ? "background-color:#bbbbbb;" : "background-color:#dddddd;";
                if (originalTemplate[i][j] == 0) HTMLText += "color: #bb5555;";
                HTMLText += "font-size: " + fontSize + ";";
                HTMLText += "'>";
                // Table data content
                HTMLText += getSymbolFromValue(values[i][j]);
                HTMLText += "</td>";
            }
            
            HTMLText += "</tr>";
        }
        HTMLText += "</table>";
        return HTMLText;
    }
    
    /**
     * Given a numerical value, get its representation. 0s are blanks, 1~9 are displayed as is,
     * and 10+ are displayed as letters (0 -> "", 5 -> "5", 10 -> "a").
     */
    String getSymbolFromValue(int a) {
        if (a == 0) { // 0 doesn't print (is an empty cell)
            return "";
        }
        else if (a >= 10) { // 10 prints as a, 11 as b, 12 as c...
            int ASCIICode = 97 + (a - 10); // a = 97 in ASCII
            return (char)ASCIICode + "";
        }
        else { // 1~9 prints as 1~9
            return a + "";
        }
    }
    
    /**
     * The opposite of "getSymbolFromValue".
     */
    int getValueFromSymbol(char c) {
        if (Character.isDigit(c)) {
            return Character.getNumericValue(c);
        }
        else {
            int ASCIICode = (int)c;
            int num = (ASCIICode - 97) + 10; // a = 97 in ASCII
            return num;
        }
    }
    
    /**
     * Inner boxes (such as 3x3 boxes in a 9x9 Sudoku) should be colored in alternating order.
     * Checks whether the cell at (x,y) falls inside a colored box.
     */
    boolean isColoredSquare(int x, int y) {
        int boxSize = (int)Math.sqrt(SIZE);
        int doubleBoxSize = boxSize * 2;
        // For every two boxes, the first one will be colored and the second uncolored
        int xMod = x % doubleBoxSize; int yMod = y % doubleBoxSize;
        // ^ = XOR
        return ((xMod >= 0 && xMod < boxSize) ^ (yMod >= 0 && yMod < boxSize));
    }
    
    /**
     * Creates a deep copy of an array (changes to the new array will not affect the old one).
     */
    final int[][] getDeepCopyOf(int[][] a) {
        int[][] b = new int[a.length][];
        for (int i=0; i < a.length; i++) {
            b[i] = Arrays.copyOf(a[i], a[i].length);
        }
        return b;
    }
    
    /**
     * Turns a string of numbers into an ordered 2d-array (to be used as Sudoku).
     */
    final int[][] parseSudokuString() {
        if (SudokuSolver.currentSudokuString == null) return new int[0][];
        
        int stringIndex = 0;
        int[][] values = new int[SIZE][];
        for (int i=0; i < SIZE; i++) {
            values[i] = new int[SIZE];
            for (int j=0; j < SIZE; j++) {
                values[i][j] = getValueFromSymbol(SudokuSolver.currentSudokuString.charAt(stringIndex++));
            }
        }
        return values;
    }
    
    /**
     * Returns a dummy string of 0s (empty cells).
     */
    public static String getEmptyString() {
        String s = "";
        for (int i=0; i < SIZE*SIZE; i++) s+= "0";
        return s;
    }
    
    /**
     * Creates a deep copy of the Sudoku (changes to the new one will not reflect on the original).
     */
    Sudoku createClone() {
        Sudoku clone = new Sudoku();
        clone.values = getDeepCopyOf(values);
        return clone;
    }
    
    /**
     * If comparing two Sudokus, the one will the lower fitness value will be considered better.
     */
    public static class FitnessComparator implements Comparator<Sudoku> {
    @Override
    public int compare(Sudoku a, Sudoku b) {
        return Integer.compare(a.fitness, b.fitness);
        }
    }
    
}
