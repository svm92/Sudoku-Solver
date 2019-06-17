package sudokusolver;

import java.sql.*;
import java.util.ArrayList;


public class DB {
    
    // To run server, from ordinary CLI: sc start mysql80
    // To create a new user:
    /* 
        CREATE USER 'sudokuUser'@'localhost' IDENTIFIED BY 'sudokuPass';
        GRANT ALL ON sudokuDB.* TO 'sudokuUser'@'localhost';
    */
    
    // Assuming database 'sudokuDB' with table 'sudoku'. Table created as:
    /*
        CREATE TABLE sudoku (
        id INT NOT NULL AUTO_INCREMENT,
        Backtrack BIGINT UNSIGNED NOT NULL, # Time to solve the sudoku using backtracking
        ConsProp BIGINT UNSIGNED NOT NULL, # Time to solve the sudoku using constraint propagation+backtracking
        Sudoku VARCHAR(81) NOT NULL, # Sudoku expressed as its 81 numbers (blanks as 0s)
        PRIMARY KEY(id));
    */
    // INSERT INTO sudoku (Sudoku, Backtrack, ConsProp) VALUES ('4234423', 424, 32);
    
    static void connectAndAnalyze() {
        String url = "jdbc:mysql://localhost:3306/sudokuDB?serverTimezone=Europe/Madrid";
        String username = "sudokuUser";
        String password = "sudokuPass";
        
        GUI.dontUpdate = true;
        String currentSudokuString = SudokuSolver.currentSudokuString;
        SudokuSolver.SolvingAlgorithm currentSolvingAlgorithm = SudokuSolver.solvingAlgorithm;
        long startTime = System.nanoTime();
        
        try (Connection con = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
            deleteData(con);
            Analyzer.start(con);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to database.", e);
        }
        
        double elapsedTime = (System.nanoTime() - startTime) / 1000000000d;
        System.out.println("Finished after " + elapsedTime + "s.");
        GUI.dontUpdate = false;
        SudokuSolver.currentSudokuString = currentSudokuString;
        SudokuSolver.solvingAlgorithm = currentSolvingAlgorithm;
        SudokuSolver.gui.enableModifierButtons(true);
    }
    
    static DBDataSet connectAndGetData() {
        String url = "jdbc:mysql://localhost:3306/sudokuDB?serverTimezone=Europe/Madrid";
        String username = "sudokuUser";
        String password = "sudokuPass";
        
        try (Connection con = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
            return getDataFromTable(con);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to database.", e);
        }
    }
    
    static void deleteData(Connection con) {
        try (Statement stmt = con.createStatement()) {
            String sqlQuery = "DELETE FROM sudoku";
            stmt.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void addDataToTable(Connection con, String sudokuString, long timeBacktrack, long timeConsProp) {
        try (Statement stmt = con.createStatement()) {
            String sqlQuery = "INSERT INTO sudoku (Sudoku, Backtrack, ConsProp) "
                    + "VALUES ('" + sudokuString + "'," + timeBacktrack + "," + timeConsProp + ")";
            stmt.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static DBDataSet getDataFromTable(Connection con) {
        ArrayList<Long> backtrackTimes = new ArrayList<>();
        ArrayList<Long> consPropTimes = new ArrayList<>();
        
        try (Statement stmt = con.createStatement()) {
            String sqlQuery = "SELECT backtrack, consprop FROM sudoku";
            // Fetch results
            ResultSet rs = stmt.executeQuery(sqlQuery);
            // Gather results
            while (rs.next()) {
                backtrackTimes.add(rs.getLong("backtrack"));
                consPropTimes.add(rs.getLong("consprop"));
            }
            DBDataSet dataSet = new DBDataSet(backtrackTimes, consPropTimes);
            return dataSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static class DBDataSet {
        ArrayList<Long> backtrackTimes;
        ArrayList<Long> consPropTimes;

        public DBDataSet(ArrayList<Long> backtrackTimes, ArrayList<Long> consPropTimes) {
            this.backtrackTimes = backtrackTimes;
            this.consPropTimes = consPropTimes;
        }
    }
    
}
