package com.soduko;

public class SudokuForm {

    private int[][] board = new int[9][9];
    public SudokuForm() {}

    public int[][] getBoard() {
        return board;
    }
    public void setBoard(int[][] board) {
        this.board = board;
    }
}
