package com.soduko;

import java.util.*;

public class Game {

    private final int[][] board = new int[9][9];    // The user-facing puzzle (with blanks)
    private final int[][] solution = new int[9][9]; // The solution (no blanks)
    private static final Random random = new Random();

    public Game() {}

    public void startGame() {
        // Reset
        for (int i = 0; i < 9; i++) Arrays.fill(board[i], 0);
        for (int i = 0; i < 9; i++) Arrays.fill(solution[i], 0);

        // Build solution then build puzzle
        generateSolution();
        copySolution();
        generatePuzzle();
    }

    private void generateSolution() {
        // Fill diagonal boxes first to speed up generation
        fillDiagonalBoxes();
        // Fill the rest recursively (board currently contains diagonal boxes)
        solveSudokuRecursive(board);
    }

    private void fillDiagonalBoxes() {
        for (int box = 0; box < 9; box += 3) fillBox(box, box);
    }

    private void fillBox(int row, int col) {
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= 9; i++) nums.add(i);
        Collections.shuffle(nums, random);
        int idx = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[row + i][col + j] = nums.get(idx++);
    }

    private boolean solveSudokuRecursive(int[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == 0) {
                    List<Integer> nums = new ArrayList<>();
                    for (int i = 1; i <= 9; i++) nums.add(i);
                    Collections.shuffle(nums, random);
                    for (int n : nums) {
                        if (isValid(grid, r, c, n)) {
                            grid[r][c] = n;
                            if (solveSudokuRecursive(grid)) return true;
                            grid[r][c] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true; // solved
    }

    public boolean isValid(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) if (grid[row][i] == num || grid[i][col] == num) return false;
        int startRow = (row / 3) * 3, startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++)
            for (int j = startCol; j < startCol + 3; j++)
                if (grid[i][j] == num) return false;
        return true;
    }

    private void copySolution() {
        for (int i = 0; i < 9; i++)
            System.arraycopy(board[i], 0, solution[i], 0, 9);
    }

    private void generatePuzzle() {
        // Number of blanks (roughly medium-hard): between 40 and 55 blanks
        int blanks = 40 + random.nextInt(16);
        while (blanks > 0) {
            int r = random.nextInt(9), c = random.nextInt(9);
            if (board[r][c] != 0) {
                board[r][c] = 0;
                blanks--;
            }
        }
    }

    public int[][] getBoard() {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) System.arraycopy(board[i], 0, copy[i], 0, 9);
        return copy;
    }

    public int[][] getSolution() {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) System.arraycopy(solution[i], 0, copy[i], 0, 9);
        return copy;
    }
}
