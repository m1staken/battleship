package com.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final Board board;
    private final boolean isPlayerBoard;
    private final List<ShootListener> shootListeners = new ArrayList<>();
    public static final int MIN_CELL_SIZE = 50;
    public static final int PADDING = 10;
    private static final int BORDER_SIZE = 2;
    private Ship currentShip;

    public GamePanel(Board board, boolean isPlayerBoard) {
        this.board = board;
        this.isPlayerBoard = isPlayerBoard;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER_SIZE));
        
        if (!isPlayerBoard) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int cellSize = getCellSize();
                    int row = (e.getY() - PADDING) / cellSize;
                    int col = (e.getX() - PADDING) / cellSize;
                    if (row >= 0 && row < board.getSize() && col >= 0 && col < board.getSize()) {
                        for (ShootListener listener : shootListeners) {
                            listener.playerShoot(row, col);
                        }
                    }
                }
            });
        }
    }

    public void setCurrentShip(Ship ship) {
        this.currentShip = ship;
        repaint();
    }

    private int getCellSize() {
        int width = getWidth();
        int height = getHeight();
        int availableSize = Math.min(width, height) - 2 * PADDING;
        int calculatedSize = availableSize / board.getSize();
        return Math.max(calculatedSize, MIN_CELL_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int width = getWidth();
        int height = getHeight();
        int cellSize = getCellSize();
        int totalSize = board.getSize() * cellSize;
        int startX = (width - totalSize) / 2;
        int startY = (height - totalSize) / 2;
        
        // Рисуем сетку
        g.setColor(Color.BLACK);
        for (int i = 0; i <= board.getSize(); i++) {
            // Вертикальные линии
            g.drawLine(startX + i * cellSize, startY, 
                      startX + i * cellSize, startY + totalSize);
            // Горизонтальные линии
            g.drawLine(startX, startY + i * cellSize, 
                      startX + totalSize, startY + i * cellSize);
        }
        
        // Рисуем клетки
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Cell cell = board.getCell(row, col);
                int x = startX + col * cellSize;
                int y = startY + row * cellSize;
                
                if (cell.isHit()) {
                    if (cell.hasShip()) {
                        g.setColor(Color.RED);
                        g.fillRect(x + 1, y + 1, cellSize - 1, cellSize - 1);
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(x + 1, y + 1, cellSize - 1, cellSize - 1);
                    }
                } else if (isPlayerBoard && cell.hasShip()) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x + 1, y + 1, cellSize - 1, cellSize - 1);
                }
            }
        }

        // Рисуем текущий корабль при размещении
        if (currentShip != null && isPlayerBoard) {
            g.setColor(new Color(0, 0, 255, 128)); // Полупрозрачный синий
            int x = startX + currentShip.getCol() * cellSize;
            int y = startY + currentShip.getRow() * cellSize;
            if (currentShip.isHorizontal()) {
                g.fillRect(x + 1, y + 1, cellSize * currentShip.getSize() - 1, cellSize - 1);
            } else {
                g.fillRect(x + 1, y + 1, cellSize - 1, cellSize * currentShip.getSize() - 1);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int size = board.getSize() * MIN_CELL_SIZE + 2 * PADDING;
        return new Dimension(size, size);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void addShootListener(ShootListener listener) {
        shootListeners.add(listener);
    }

    public interface ShootListener {
        void playerShoot(int row, int col);
    }
} 