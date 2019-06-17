# Sudoku Solver

Source code for a Sudoku Solver written in Java, in the NetBeans IDE.

It can solve a Sudoku of virtually any size (3x3, 2x2, 4x4) using at least three different techniques:
* **Genetic algorithm* *(Very slow and inefficient, might not find solution)*
* **Backtracking algorithm** *(Fast, will always find solution)*
* **Constraint propagation + Backtracking** *(Fastest, will always find solution)*

It can optionally connect to a local SQL database to archive and later analyze statistical information pertaining to the efficiency of the different algorithms.

It makes use of the JFreeChart library for plotting graphs.
