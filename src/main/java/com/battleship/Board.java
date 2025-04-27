package com.battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private static final int SIZE = 10;
    private final Cell[][] cells;
    private final List<Ship> ships;
    private final Random random;

    public Board() {
        this.cells = new Cell[SIZE][SIZE];
        this.ships = new ArrayList<>();
        this.random = new Random();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public void placeShips() {
        int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        
        for (int size : shipSizes) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int row = random.nextInt(SIZE);
                int col = random.nextInt(SIZE);
                boolean isHorizontal = random.nextBoolean();
                
                if (canPlaceShip(row, col, size, isHorizontal)) {
                    placeShip(row, col, size, isHorizontal);
                    placed = true;
                }
                attempts++;
            }
            if (!placed) {
                // Если не удалось разместить корабль, пересоздаем поле
                clearBoard();
                placeShips();
                return;
            }
        }
    }

    private void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        ships.clear();
    }

    public boolean canPlaceShip(int row, int col, int size, boolean isHorizontal) {
        if (isHorizontal) {
            if (col + size > SIZE) return false;
            for (int i = col; i < col + size; i++) {
                if (cells[row][i].hasShip()) return false;
                // Проверяем соседние клетки
                for (int r = Math.max(0, row - 1); r <= Math.min(SIZE - 1, row + 1); r++) {
                    for (int c = Math.max(0, i - 1); c <= Math.min(SIZE - 1, i + 1); c++) {
                        if (cells[r][c].hasShip()) return false;
                    }
                }
            }
        } else {
            if (row + size > SIZE) return false;
            for (int i = row; i < row + size; i++) {
                if (cells[i][col].hasShip()) return false;
                // Проверяем соседние клетки
                for (int r = Math.max(0, i - 1); r <= Math.min(SIZE - 1, i + 1); r++) {
                    for (int c = Math.max(0, col - 1); c <= Math.min(SIZE - 1, col + 1); c++) {
                        if (cells[r][c].hasShip()) return false;
                    }
                }
            }
        }
        return true;
    }

    public void placeShip(int row, int col, int size, boolean horizontal) {
        // Проверяем границы поля
        if (horizontal) {
            if (col + size > SIZE) {
                throw new IllegalArgumentException("Корабль выходит за границы поля");
            }
        } else {
            if (row + size > SIZE) {
                throw new IllegalArgumentException("Корабль выходит за границы поля");
            }
        }

        // Проверяем, что клетки свободны и нет соприкосновения с другими кораблями
        for (int i = -1; i <= size; i++) {
            for (int j = -1; j <= 1; j++) {
                int r = horizontal ? row + j : row + i;
                int c = horizontal ? col + i : col + j;
                
                // Проверяем только клетки в пределах поля
                if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
                    // Для основной части корабля проверяем, что клетка свободна
                    if (i >= 0 && i < size && j == 0) {
                        if (cells[r][c].hasShip()) {
                            throw new IllegalArgumentException("Клетка уже занята кораблем");
                        }
                    }
                    // Для окружающих клеток проверяем, что нет других кораблей
                    else if (cells[r][c].hasShip()) {
                        throw new IllegalArgumentException("Корабли не могут соприкасаться");
                    }
                }
            }
        }

        // Создаем новый корабль
        Ship ship = new Ship(size);
        ship.setPosition(row, col, horizontal);

        // Размещаем корабль
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            cells[r][c].setShip(ship);
            ship.addCell(cells[r][c]);
        }
        
        ships.add(ship);
    }

    public void removeShip(Ship ship) {
        for (Cell cell : ship.getCells()) {
            cell.removeShip();
        }
        ships.remove(ship);
    }

    public boolean shoot(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return false;
        }
        
        Cell cell = cells[row][col];
        if (cell.shoot()) {
            if (cell.hasShip()) {
                Ship ship = cell.getShip();
                if (ship.isSunk()) {
                    markSurroundingCells(ship);
                }
            }
            return true;
        }
        return false;
    }

    private void markSurroundingCells(Ship ship) {
        for (Cell cell : ship.getCells()) {
            int row = cell.getRow();
            int col = cell.getCol();
            
            // Отмечаем все соседние клетки
            for (int r = Math.max(0, row - 1); r <= Math.min(SIZE - 1, row + 1); r++) {
                for (int c = Math.max(0, col - 1); c <= Math.min(SIZE - 1, col + 1); c++) {
                    if (!cells[r][c].hasShip() && !cells[r][c].isHit()) {
                        cells[r][c].shoot();
                    }
                }
            }
        }
    }

    public boolean isGameOver() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public int getSize() {
        return SIZE;
    }
} 