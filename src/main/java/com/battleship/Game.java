package com.battleship;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame implements GamePanel.ShootListener {
    private final Board playerBoard;
    private final Board computerBoard;
    private final GamePanel playerPanel;
    private final GamePanel computerPanel;
    private final JLabel statusLabel;
    private boolean isPlayerTurn;

    public Game() {
        // Устанавливаем кодировку для всех текстовых компонентов
        System.setProperty("file.encoding", "UTF-8");
        
        setTitle("Морской бой");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Создаем игровые поля
        playerBoard = new Board();
        computerBoard = new Board();
        computerBoard.placeShips(); // Компьютер всегда размещает корабли автоматически

        // Создаем панели для отображения полей
        playerPanel = new GamePanel(playerBoard, true);
        computerPanel = new GamePanel(computerBoard, false);

        // Добавляем слушатели для стрельбы
        computerPanel.addShootListener(this);

        // Создаем панели с заголовками
        JPanel playerBoardPanel = createBoardPanel(playerPanel, "Ваше поле");
        JPanel computerBoardPanel = createBoardPanel(computerPanel, "Поле противника");

        // Создаем панель для полей с равными отступами
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boardsPanel.setBackground(Color.WHITE);
        boardsPanel.add(playerBoardPanel);
        boardsPanel.add(computerBoardPanel);

        // Создаем метку статуса
        statusLabel = new JLabel("Ваш ход. Выберите клетку на поле противника.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Создаем основную панель с отступами
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(boardsPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Добавляем основную панель на форму
        add(mainPanel);

        pack();
        setLocationRelativeTo(null);
        isPlayerTurn = true;
    }

    @Override
    public void playerShoot(int row, int col) {
        if (!isPlayerTurn) return;

        if (computerBoard.shoot(row, col)) {
            if (computerBoard.getCell(row, col).hasShip()) {
                statusLabel.setText("Попадание! Стреляйте снова.");
                if (computerBoard.isGameOver()) {
                    gameOver(true);
                }
            } else {
                statusLabel.setText("Промах! Ход компьютера.");
                isPlayerTurn = false;
                computerShoot();
            }
            repaint();
        }
    }

    public void startGame() {
        // Обновляем отображение обоих полей
        playerPanel.repaint();
        computerPanel.repaint();
        
        // Пересобираем окно
        revalidate();
        repaint();
        
        // Делаем окно видимым
        setVisible(true);
        
        // Принудительно обновляем отображение
        SwingUtilities.invokeLater(() -> {
            playerPanel.repaint();
            computerPanel.repaint();
        });
    }

    private void setupPlayerShips() {
        SetupDialog setupDialog = new SetupDialog(this);
        setupDialog.setVisible(true);

        if (setupDialog.isAutoSetup()) {
            playerBoard.placeShips();
            startGame();
        } else {
            ManualShipPlacer shipPlacer = new ManualShipPlacer(this, playerBoard, playerPanel);
            setVisible(false); // Скрываем основное окно
            shipPlacer.startPlacement();
        }
    }

    private JPanel createBoardPanel(GamePanel gamePanel, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Увеличиваем шрифт заголовков
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(gamePanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void computerShoot() {
        int row, col;
        do {
            row = (int) (Math.random() * playerBoard.getSize());
            col = (int) (Math.random() * playerBoard.getSize());
        } while (playerBoard.getCell(row, col).isHit());

        if (playerBoard.shoot(row, col)) {
            if (playerBoard.getCell(row, col).hasShip()) {
                statusLabel.setText("Компьютер попал! Его ход продолжается.");
                if (playerBoard.isGameOver()) {
                    gameOver(false);
                } else {
                    computerShoot();
                }
            } else {
                statusLabel.setText("Компьютер промахнулся! Ваш ход.");
                isPlayerTurn = true;
            }
            repaint();
        }
    }

    private void gameOver(boolean playerWon) {
        isPlayerTurn = false;
        String message = playerWon ? "Поздравляем! Вы победили!" : "Игра окончена! Победил компьютер.";
        
        // Создаем диалоговое окно
        JDialog gameOverDialog = new JDialog(this, "Игра окончена", true);
        gameOverDialog.setLayout(new BorderLayout());
        
        // Создаем панель с сообщением
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messagePanel.add(messageLabel);
        
        // Создаем панель с кнопками
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton playAgainButton = new JButton("Играть снова");
        playAgainButton.addActionListener(e -> {
            gameOverDialog.dispose();
            dispose();
            newGame();
        });
        
        JButton exitButton = new JButton("Закрыть игру");
        exitButton.addActionListener(e -> {
            gameOverDialog.dispose();
            System.exit(0);
        });
        
        buttonPanel.add(playAgainButton);
        buttonPanel.add(exitButton);
        
        // Добавляем компоненты на диалог
        gameOverDialog.add(messagePanel, BorderLayout.CENTER);
        gameOverDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Устанавливаем размер и позицию диалога
        gameOverDialog.pack();
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setVisible(true);
    }

    private void newGame() {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.setupPlayerShips();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.setupPlayerShips();
        });
    }
} 