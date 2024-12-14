package com.gyamjoDechen.view;

import com.gyamjoDechen.controller.DataManager;
import com.gyamjoDechen.model.RandomForest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Map;

public class StartUI extends Application {
    private RandomForest randomForest;
    private List<Map<String, Object>> dataset;
    private String filePath = "C:\\Users\\sherp\\OneDrive\\Desktop\\FRapp\\src\\main\\java\\com\\gyamjoDechen\\controller\\data.csv"; // Default file path

    @Override
    public void start(Stage primaryStage) {
        // Create the main layout for the UI with vertical alignment and background color
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f0f8ff;");

        // Title Label
        Label titleLabel = new Label("Financial Risk Prediction");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Status Label to show model training progress
        Label statusLabel = new Label();

        // TextField to display or input the file path
        TextField filePathField = new TextField(filePath); // Initialize with default file path
        filePathField.setStyle("-fx-font-size: 14px;");
        filePathField.setPrefWidth(300); // Set width for the text field

        // Browse Button to select CSV file
        Button browseButton = new Button("Browse...");
        browseButton.setStyle("-fx-background-color: #00008B; -fx-text-fill: white; -fx-font-size: 16px;");
        browseButton.setOnAction(event -> {
            // Open file chooser and update file path field with selected file
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        // Start Button to begin model training process
        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #00008B; -fx-text-fill: white; -fx-font-size: 16px;");
        startButton.setOnAction(event -> {
            filePath = filePathField.getText(); // Get file path from text field
            statusLabel.setText("Training model... Please wait!"); // Update status label

            // Run the model training process in a separate thread to avoid blocking the UI
            new Thread(() -> {
                try {
                    // Load and clean the dataset, then train the model
                    DataManager dataManager = new DataManager();
                    dataset = dataManager.loadData(filePath); // Load dataset from file
                    dataset = dataManager.cleanData(dataset); // Clean the data
                    randomForest = new RandomForest(10); // Initialize RandomForest with 10 trees
                    randomForest.train(dataset, "Risk Rating"); // Train the model

                    // Update status label and navigate to the next UI after training
                    Platform.runLater(() -> {
                        statusLabel.setText("Training complete!"); // Update training status
                        navigateToInputUI(primaryStage); // Navigate to the next screen
                    });
                } catch (Exception e) {
                    // Display error message if training fails
                    Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                }
            }).start();
        });

        // Add components to the layout
        layout.getChildren().addAll(titleLabel, filePathField, browseButton, startButton, statusLabel);

        // Create the scene and set it on the primary stage
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Financial Risk Predictor");
        primaryStage.show();
    }

    // Method to navigate to the next UI after training the model
    private void navigateToInputUI(Stage primaryStage) {
        // Pass the trained model and dataset to the next UI screen
        new InputUI(randomForest, dataset).start(primaryStage); // Start the next UI window
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
