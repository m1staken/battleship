package com.battleship;

import javax.swing.*;
import java.awt.*;

public class SetupDialog extends JDialog {
    private boolean autoSetup;

    public SetupDialog(JFrame parent) {
        super(parent, "Настройка игры", true);
        setLayout(new BorderLayout());
        setSize(300, 150);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel questionLabel = new JLabel("Как вы хотите расставить корабли?", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JButton autoButton = new JButton("Автоматически");
        JButton manualButton = new JButton("Вручную");

        autoButton.addActionListener(e -> {
            autoSetup = true;
            dispose();
        });

        manualButton.addActionListener(e -> {
            autoSetup = false;
            dispose();
        });

        buttonPanel.add(autoButton);
        buttonPanel.add(manualButton);

        mainPanel.add(questionLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public boolean isAutoSetup() {
        return autoSetup;
    }
} 