package com.gyamjoDechen.view;

import com.gyamjoDechen.controller.RecommendationManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultUI {
    private final String prediction;
    private final Map<String, Object> userInput;
    private final List<Map<String, Object>> dataset;

    public ResultUI(String prediction, Map<String, Object> userInput, List<Map<String, Object>> dataset) {
        this.prediction = prediction;
        this.userInput = userInput;
        this.dataset = dataset;
    }

    public void start(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #ffe4c4;");

        // Displays the prediction message indicating whether the user is at risk
        String riskMessage = "Low".equalsIgnoreCase(prediction) ? "Not At Risk" : "At Risk";
        Label predictionLabel = new Label("Prediction: " + riskMessage);
        predictionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Instructions for the user to click to get recommendations
        Label recommendationsLabel = new Label("Click on 'Show Recommendations' to get personalized suggestions.");
        recommendationsLabel.setWrapText(true);
        recommendationsLabel.setStyle("-fx-font-size: 14px;");

        // Adds the recommendations label to a scrollable pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(recommendationsLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Button that generates recommendations when clicked
        Button suggestionButton = new Button("Show Recommendations");
        suggestionButton.setStyle("-fx-background-color: #00008B; -fx-text-fill: white;");
        suggestionButton.setOnAction(event -> {
            // Filters low-risk profiles to find role models for recommendations
            List<Map<String, Object>> lowRiskProfiles = dataset.stream()
                    .filter(entry -> "Low".equalsIgnoreCase((String) entry.get("Risk Rating")))
                    .collect(Collectors.toList());

            // Uses RecommendationManager to generate recommendations based on user input
            RecommendationManager recommendationManager = new RecommendationManager(lowRiskProfiles);
            String recommendations = recommendationManager.getRecommendations(prediction, userInput);

            // Updates the label with the generated recommendations
            recommendationsLabel.setText(recommendations);

            // Adjusts the window size based on the content
            primaryStage.sizeToScene();
        });

        layout.getChildren().addAll(predictionLabel, suggestionButton, scrollPane);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
    }
}
