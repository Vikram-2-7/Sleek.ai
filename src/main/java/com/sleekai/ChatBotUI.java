package com.sleekai;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.sleekai.core.OllamaClient;

public class ChatBotUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private boolean darkMode = false;

    private final Color LIGHT_BG = new Color(245, 245, 245);
    private final Color DARK_BG = new Color(30, 30, 30);
    private final Color LIGHT_TEXT = Color.BLACK;
    private final Color DARK_TEXT = new Color(220, 220, 220);
    private final Font FONT = new Font("Segoe UI Emoji", Font.PLAIN, 16);

    public ChatBotUI() {
        setTitle("💬 Sleek.ai Chatbot");
        setSize(700, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(FONT);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatArea.setBackground(LIGHT_BG);
        chatArea.setForeground(LIGHT_TEXT);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        inputField = new JTextField();
        inputField.setFont(FONT);
        inputField.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputField.setBackground(LIGHT_BG);
        inputField.setForeground(LIGHT_TEXT);
        inputField.addActionListener(this::sendMessage);
        inputField.setEnabled(false); // disabled until warmup

        JToggleButton darkToggle = new JToggleButton("🌙");
        darkToggle.setFocusable(false);
        darkToggle.addActionListener(e -> toggleDarkMode());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(darkToggle, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        appendBotMessage("🧠 Booting up Sleek.ai backend... hang tight...");

        SwingUtilities.invokeLater(() -> {
            inputField.requestFocusInWindow();
            new Thread(() -> {
                // OllamaClient.warmUpModel(this); // ensure Ollama is live
                appendBotMessage("👋 Yo! Ask me anything. Type 'exit' to quit.");
                inputField.setEnabled(true);
            }).start();
        });
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;

        Color bg = darkMode ? DARK_BG : LIGHT_BG;
        Color fg = darkMode ? DARK_TEXT : LIGHT_TEXT;

        chatArea.setBackground(bg);
        chatArea.setForeground(fg);
        inputField.setBackground(bg);
        inputField.setForeground(fg);
    }

    private void sendMessage(ActionEvent e) {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty())
            return;

        appendUserMessage(userInput);
        inputField.setText("");

        if (userInput.equalsIgnoreCase("exit")) {
            appendBotMessage("👋 Catch you later!");
            System.exit(0);
        }

        new Thread(() -> {
            String response = OllamaClient.askMistral(userInput, this);
            appendBotMessage(response);
        }).start();
    }

    private void appendUserMessage(String msg) {
        chatArea.append("🧑 [You]: " + msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void appendBotMessage(String msg) {
        chatArea.append("🤖 [Sleekai]: " + msg + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        // Ensure colored emoji output
        System.setOut(new java.io.PrintStream(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8));
        SwingUtilities.invokeLater(() -> new ChatBotUI().setVisible(true));
    }
}
