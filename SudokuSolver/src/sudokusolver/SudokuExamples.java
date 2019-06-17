package sudokusolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Generates some default Sudokus from text files (of sizes 3x3, 4x4, 5x5).
 */
public class SudokuExamples {
    
    static final HashMap<Integer, String> TEXTFILE = new HashMap<>();
    static
    {
        TEXTFILE.put(9, "SudokuSampleList.txt");
        TEXTFILE.put(16, "SudokuSampleList4.txt");
        TEXTFILE.put(25, "SudokuSampleList5.txt");
    }
    
    public static SudokuSample[] samples;
    public static class SudokuSample {
        public String s;
        public int id;
        public SudokuSample(String s, int id) {this.s = s; this.id = id;}
        
        @Override
        public String toString() {
            return id + "";
        }
    }
    
    public static void readAllSamples() {
        if (TEXTFILE.containsKey(Sudoku.SIZE))
            readAllSamplesFromTextFile();
        else
            createDefaultEmptySample();
    }
    
    public static void readAllSamplesFromTextFile() {
        ArrayList<String> samplesList = new ArrayList<>();
        Scanner s = null;
        
        try {
            String textFileName = TEXTFILE.get(Sudoku.SIZE);
            s = new Scanner(new BufferedReader(new FileReader(textFileName)));
            
            while (s.hasNextLine()) {
                String nextLine = s.nextLine();
                nextLine = nextLine.substring(0, Sudoku.SIZE * Sudoku.SIZE);
                samplesList.add(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (s != null) {
                s.close();
            }
        }
        
        samples = new SudokuSample[samplesList.size()];
        for (int i=0; i < samples.length; i++) {
            samples[i] = new SudokuSample(samplesList.get(i), i);
        }
    }
    
    public static void createDefaultEmptySample() {
        samples = new SudokuSample[1];
        samples[0] = new SudokuSample(Sudoku.getEmptyString(), 0);
    }
    
}
