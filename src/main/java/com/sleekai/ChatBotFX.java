package com.sleekai;

import com.sleekai.core.OllamaClient;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ChatBotFX extends Application {
    private VBox messageBox;
    private TextField inputField;
    private boolean darkMode = true;
    private Scene scene;
    private BorderPane root;
    private ScrollPane scrollPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        messageBox = new VBox(12);
        messageBox.setPadding(new Insets(15));
        messageBox.setFillWidth(true); // ✅ make messages fit width

        scrollPane = new ScrollPane(messageBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // ✅ no horizontal scroll
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent;");
        scrollPane.vvalueProperty().unbind(); // ✅ no auto-scroll

        inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setFont(Font.font("Segoe UI Emoji", 15));
        inputField.setPrefHeight(60); // ✅ larger input field
        inputField.setStyle("-fx-background-radius: 15; -fx-padding: 10;");
        inputField.setOnAction(e -> handleUserInput());

        Button toggleBtn = new Button("🌓");
        toggleBtn.setPrefHeight(60);
        toggleBtn.setOnAction(e -> toggleTheme());
        toggleBtn.setStyle("-fx-background-radius: 15;");

        HBox inputArea = new HBox(inputField, toggleBtn);
        inputArea.setSpacing(10);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setPadding(new Insets(12));

        root.setCenter(scrollPane);
        root.setBottom(inputArea);

        scene = new Scene(root, 800, 600); // ✅ slightly larger window
        applySleekDarkTheme();

        stage.setTitle("Sleek.ai 🤖");
        stage.setScene(scene);
        stage.show();

        addBotMessage("🧠 Sleek.ai is here. Ask me anything!");
        new Thread(() -> {
            String greet = "Ready to rizz up some facts? 🔥";
            javafx.application.Platform.runLater(() -> addBotMessage(greet));
        }).start();
    }

    private void handleUserInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty())
            return;

        addUserMessage(input);
        inputField.clear();

        if (input.equalsIgnoreCase("exit")) {
            addBotMessage("👋 Sleek.ai signing off.");
            System.exit(0);
        }

        new Thread(() -> {
            String response = OllamaClient.askMistral(input, null);
            javafx.application.Platform.runLater(() -> addBotMessage(response));
        }).start();
    }

    private void addUserMessage(String text) {
        addMessage("🧑 You", text, Color.web("#1e072eff"), Color.WHITE, Pos.CENTER_RIGHT);
    }

    private void addBotMessage(String text) {
        addMessage("🤖 Sleekai", text, Color.web("#1f1f1f"), Color.web("#ffffff"), Pos.CENTER_LEFT);
    }

    private void addMessage(String sender, String message, Color bubbleColor, Color textColor, Pos alignment) {
        VBox bubble = new VBox();
        bubble.setPadding(new Insets(10));
        bubble.setBackground(new Background(new BackgroundFill(bubbleColor, new CornerRadii(14), Insets.EMPTY)));
        bubble.setMaxWidth(600); // ✅ widened bubble

        Text msgText = new Text(message);
        msgText.setWrappingWidth(580); // ✅ enable text wrap
        msgText.setFill(textColor);
        msgText.setFont(Font.font("Segoe UI Emoji", 15));
        bubble.getChildren().add(msgText);

        Label nameLabel = new Label(sender);
        nameLabel.setFont(Font.font("Segoe UI", 11));
        nameLabel.setTextFill(textColor.darker());

        VBox container = new VBox(nameLabel, bubble);
        container.setAlignment(alignment);
        container.setPadding(new Insets(6));

        FadeTransition fade = new FadeTransition(Duration.millis(300), container);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        messageBox.getChildren().add(container);
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        if (darkMode)
            applySleekDarkTheme();
        else
            applySleekLightTheme();
    }

    private void applySleekDarkTheme() {
        root.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0f2027")),
                new Stop(1, Color.web("#203a43"))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        inputField.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: white; -fx-background-radius: 12;");
    }

    private void applySleekLightTheme() {
        root.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffffff")),
                new Stop(1, Color.web("#ffffffff"))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        inputField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 12;");
    }
}
