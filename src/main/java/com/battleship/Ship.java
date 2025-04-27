package com.battleship;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final int size;
    private int row;
    private int col;
    private boolean horizontal;
    private final List<Cell> cells;
    private boolean isSunk;

    public Ship(int size) {
        this.size = size;
        this.cells = new ArrayList<>();
        this.isSunk = false;
    }

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public boolean isHit() {
        return cells.stream().allMatch(Cell::isHit);
    }

    public void checkSunk() {
        isSunk = isHit();
    }

    public boolean isSunk() {
        return isSunk;
    }

    public int getSize() {
        return size;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setPosition(int row, int col, boolean horizontal) {
        this.row = row;
        this.col = col;
        this.horizontal = horizontal;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isHorizontal() {
        return horizontal;
    }
} 