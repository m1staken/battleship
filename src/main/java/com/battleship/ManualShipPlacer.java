package com.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ManualShipPlacer extends JFrame {
    private final Board board;
    private final GamePanel gamePanel;
    private final Game parentGame;
    private final int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1}; // Правильные размеры кораблей
    private int currentShipIndex = 0;
    private Ship currentShip;
    private boolean isHorizontal = true;
    private JButton rotateButton;
    private JButton confirmButton;
    private JButton startButton;

    public ManualShipPlacer(Game parentGame, Board board, GamePanel gamePanel) {
        this.parentGame = parentGame;
        this.board = board;
        this.gamePanel = gamePanel;
        
        setTitle("Размещение кораблей");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Создаем панель для размещения кораблей
        JPanel placementPanel = new JPanel(new BorderLayout());
        placementPanel.add(gamePanel, BorderLayout.CENTER);

        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Кнопка поворота корабля
        rotateButton = new JButton("Повернуть корабль");
        rotateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotateButton.addActionListener(e -> {
            if (currentShip != null) {
                isHorizontal = !isHorizontal;
                currentShip.setPosition(currentShip.getRow(), currentShip.getCol(), isHorizontal);
                gamePanel.setCurrentShip(currentShip);
                gamePanel.repaint();
            }
        });

        // Кнопка подтверждения размещения
        confirmButton = new JButton("Подтвердить размещение");
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(e -> {
            if (currentShip != null) {
                try {
                    board.placeShip(currentShip.getRow(), currentShip.getCol(), 
                            currentShip.getSize(), currentShip.isHorizontal());
                    currentShip = null;
                    currentShipIndex++;
                    
                    if (currentShipIndex < shipSizes.length) {
                        // Не создаем новый корабль автоматически
                        gamePanel.setCurrentShip(null);
                    } else {
                        confirmButton.setVisible(false);
                        rotateButton.setVisible(false);
                        startButton.setVisible(true);
                    }
                    gamePanel.repaint();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), 
                            "Ошибка размещения", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Кнопка начала игры
        startButton = new JButton("Начать игру");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setVisible(false);
        startButton.addActionListener(e -> {
            parentGame.startGame();
            dispose();
        });

        buttonPanel.add(rotateButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(startButton);

        // Добавляем компоненты на форму
        add(placementPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

        // Устанавливаем размер окна
        int fieldSize = board.getSize() * GamePanel.MIN_CELL_SIZE + 2 * GamePanel.PADDING;
        setSize(fieldSize + 200, fieldSize + 100); // Добавляем место для кнопок
        setLocationRelativeTo(null);

        // Добавляем слушатель мыши для размещения кораблей
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int width = gamePanel.getWidth();
                int height = gamePanel.getHeight();
                int cellSize = Math.min(width, height) / board.getSize();
                int startX = (width - board.getSize() * cellSize) / 2;
                int startY = (height - board.getSize() * cellSize) / 2;
                
                int col = (e.getX() - startX) / cellSize;
                int row = (e.getY() - startY) / cellSize;
                
                if (row >= 0 && row < board.getSize() && col >= 0 && col < board.getSize()) {
                    handleMouseClick(row, col);
                }
            }
        });
    }

    private void handleMouseClick(int row, int col) {
        if (currentShipIndex >= shipSizes.length) return;

        if (currentShip == null) {
            // Создаем новый корабль при первом клике
            currentShip = new Ship(shipSizes[currentShipIndex]);
            currentShip.setPosition(row, col, isHorizontal);
            gamePanel.setCurrentShip(currentShip);
            gamePanel.repaint();
        } else {
            // Обновляем позицию существующего корабля
            currentShip.setPosition(row, col, isHorizontal);
            gamePanel.setCurrentShip(currentShip);
            gamePanel.repaint();
        }
    }

    public void startPlacement() {
        setVisible(true);
    }
} 