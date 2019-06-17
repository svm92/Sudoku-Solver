package sudokusolver;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import sudokusolver.Sudoku.EmptyCell;


public class SudokuTest {
    
    public SudokuTest() {
    }

    @Test
    public void testIsSolved() {
        Sudoku sudoku = new Sudoku();
        sudoku.values = sudoku.originalTemplate = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6}, 
            {3, 1, 2, 5, 9, 4, 6, 7, 8},
            {5, 6, 4, 2, 7, 8, 3, 9, 1},
            {8, 9, 7, 6, 3, 1, 2, 4, 5},
            {6, 4, 5, 8, 1, 7, 9, 3, 2},
            {9, 7, 8, 3, 6, 2, 5, 1, 4},
            {2, 3, 1, 9, 4, 5, 8, 6, 7}
        };
        assertEquals(true, sudoku.isSolved());
        
        // Repeated row
        sudoku.values = sudoku.originalTemplate = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6}, 
            {3, 1, 2, 5, 9, 4, 3, 7, 8},
            {5, 6, 4, 2, 7, 8, 3, 9, 1},
            {8, 9, 7, 6, 3, 1, 2, 4, 5},
            {6, 4, 5, 8, 1, 7, 9, 3, 2},
            {9, 7, 8, 3, 6, 2, 5, 1, 4},
            {2, 3, 1, 9, 4, 5, 8, 6, 7}
        };
        assertEquals("Repeated row", false, sudoku.isSolved());
        
        // Repeated column
        sudoku.values = sudoku.originalTemplate = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6}, 
            {3, 1, 2, 5, 9, 4, 6, 7, 8},
            {5, 6, 4, 2, 7, 8, 3, 9, 1},
            {8, 9, 7, 6, 3, 1, 2, 4, 5},
            {6, 4, 5, 8, 1, 7, 9, 5, 2},
            {9, 7, 8, 3, 6, 2, 5, 1, 4},
            {2, 3, 1, 9, 4, 5, 8, 6, 7}
        };
        assertEquals("Repeated column", false, sudoku.isSolved());
        
        // Repeated inner
        sudoku.values = sudoku.originalTemplate = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {2, 3, 4, 5, 6, 7, 8, 9, 1},
            {3, 4, 5, 6, 7, 8, 9, 1, 2}, 
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {5, 6, 7, 8, 9, 1, 2, 3, 4},
            {6, 7, 8, 9, 1, 2, 3, 4, 5},
            {7, 8, 9, 1, 2, 3, 4, 5, 6},
            {8, 9, 1, 2, 3, 4, 5, 6, 7},
            {9, 1, 2, 3, 4, 5, 6, 7, 8}
        };
        assertEquals("Repeated inner square", false, sudoku.isSolved());
    }
    
    @Test
    public void testGetBox() {
        Sudoku sudoku = new Sudoku();
        sudoku.values = sudoku.originalTemplate = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6}, 
            {3, 1, 2, 5, 9, 4, 6, 7, 8},
            {5, 6, 4, 2, 7, 8, 3, 9, 1},
            {8, 9, 7, 6, 3, 1, 2, 4, 5},
            {6, 4, 5, 8, 1, 7, 9, 3, 2},
            {9, 7, 8, 3, 6, 2, 5, 1, 4},
            {2, 3, 1, 9, 4, 5, 8, 6, 7}
        };
        assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9}, sudoku.getBox(new Sudoku.EmptyCell(1, 2)));
        assertArrayEquals(new int[] {4, 5, 6, 7, 8, 9, 1, 2, 3}, sudoku.getBox(new Sudoku.EmptyCell(0, 3)));
        assertArrayEquals(new int[] {5, 9, 4, 2, 7, 8, 6, 3, 1}, sudoku.getBox(new Sudoku.EmptyCell(5, 5)));
        assertArrayEquals(new int[] {9, 3, 2, 5, 1, 4, 8, 6, 7}, sudoku.getBox(new Sudoku.EmptyCell(8, 8)));
    }
    
    @Test
    public void testGetEmptyCellsInBox() {
        Sudoku sudoku = new Sudoku();
        sudoku.values = sudoku.originalTemplate = new int[][] {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {0, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 0, 9, 1, 2, 3, 4, 5, 6}, 
            {3, 1, 2, 5, 9, 4, 6, 7, 8},
            {5, 6, 4, 2, 7, 8, 3, 9, 1},
            {8, 9, 7, 6, 3, 1, 2, 4, 5},
            {6, 4, 5, 0, 0, 7, 9, 3, 2},
            {9, 7, 8, 3, 6, 2, 5, 1, 4},
            {2, 3, 1, 0, 4, 5, 8, 6, 7}
        };
        ArrayList<EmptyCell> box1 = sudoku.getEmptyCellsInBox(new Sudoku.EmptyCell(1, 2));
        assertEquals(2, box1.size());
        assertEquals(1, box1.get(0).x);
        assertEquals(0, box1.get(0).y);
        assertEquals(2, box1.get(1).x);
        assertEquals(1, box1.get(1).y);
        ArrayList<EmptyCell> box2 = sudoku.getEmptyCellsInBox(new Sudoku.EmptyCell(8, 5));
        assertEquals(3, box2.size());
        assertEquals(6, box2.get(0).x);
        assertEquals(3, box2.get(0).y);
        assertEquals(6, box2.get(1).x);
        assertEquals(4, box2.get(1).y);
        assertEquals(8, box2.get(2).x);
        assertEquals(3, box2.get(2).y);
    }
    
    @Test
    public void testCellEquality() {
        EmptyCell c1 = new EmptyCell(2, 4);
        EmptyCell c2 = new EmptyCell(2, 4);
        EmptyCell c3 = new EmptyCell(5, 7);
        EmptyCell c4 = new EmptyCell(4, 2);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, c4);
    }
    
    @Test
    public void testInSameUnit() {
        Sudoku sudoku = new Sudoku();
        sudoku.values = sudoku.originalTemplate = new int[][] {
            {0, 0, 0, 0, 0, 0, 0, 0, 0}, 
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        EmptyCell c1 = new EmptyCell(2, 4);
        EmptyCell c2 = new EmptyCell(2, 4);
        assertEquals("Same, should return false", false, sudoku.inSameUnit(c1, c2, Sudoku.UnitType.ROW));
        assertEquals("Same, should return false", false, sudoku.inSameUnit(c1, c2, Sudoku.UnitType.COL));
        assertEquals("Same, should return false", false, sudoku.inSameUnit(c1, c2, Sudoku.UnitType.BOX));
        
        EmptyCell c3 = new EmptyCell(2, 8);
        assertEquals(true, sudoku.inSameUnit(c1, c3, Sudoku.UnitType.ROW));
        assertEquals(false, sudoku.inSameUnit(c1, c3, Sudoku.UnitType.COL));
        assertEquals(false, sudoku.inSameUnit(c1, c3, Sudoku.UnitType.BOX));
        
        EmptyCell c4 = new EmptyCell(6, 4);
        assertEquals(false, sudoku.inSameUnit(c1, c4, Sudoku.UnitType.ROW));
        assertEquals(true, sudoku.inSameUnit(c1, c4, Sudoku.UnitType.COL));
        assertEquals(false, sudoku.inSameUnit(c1, c4, Sudoku.UnitType.BOX));
        
        EmptyCell c5 = new EmptyCell(1, 5);
        assertEquals(false, sudoku.inSameUnit(c1, c5, Sudoku.UnitType.ROW));
        assertEquals(false, sudoku.inSameUnit(c1, c5, Sudoku.UnitType.COL));
        assertEquals(true, sudoku.inSameUnit(c1, c5, Sudoku.UnitType.BOX));
    }
}
