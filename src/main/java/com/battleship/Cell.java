package com.battleship;

public class Cell {
    private final int row;
    private final int col;
    private boolean hasShip;
    private boolean isHit;
    private Ship ship;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.hasShip = false;
        this.isHit = false;
        this.ship = null;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
        this.hasShip = true;
    }

    public void removeShip() {
        this.ship = null;
        this.hasShip = false;
    }

    public boolean hasShip() {
        return hasShip;
    }

    public boolean isHit() {
        return isHit;
    }

    public boolean shoot() {
        if (!isHit) {
            isHit = true;
            if (hasShip && ship != null) {
                ship.checkSunk();
            }
            return true;
        }
        return false;
    }

    public Ship getShip() {
        return ship;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
} 