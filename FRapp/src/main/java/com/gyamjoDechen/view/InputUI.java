package com.gyamjoDechen.view;

import com.gyamjoDechen.model.RandomForest;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputUI {

    private final RandomForest randomForest;
    private final List<Map<String, Object>> dataset;

    // Constructor updated to accept the dataset
    public InputUI(RandomForest randomForest, List<Map<String, Object>> dataset) {
        this.randomForest = randomForest;
        this.dataset = dataset;
    }

    public void start(Stage primaryStage) {
        // Form layout
        VBox formLayout = new VBox(20);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setStyle("-fx-padding: 20; -fx-background-color: #dfffd6;"); // Light green background

        Label titleLabel = new Label("Enter User Details");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Create form input fields
        HBox genderBox = createQuestionAnswerPair("Gender:", createChoiceBox("Male", "Female", "Non-binary"));
        HBox educationBox = createQuestionAnswerPair("Education Level:", createChoiceBox("PhD", "Bachelor's", "Master's", "High School"));
        HBox maritalStatusBox = createQuestionAnswerPair("Marital Status:", createChoiceBox("Single", "Married", "Divorced", "Widowed"));
        HBox employmentBox = createQuestionAnswerPair("Employment Status:", createChoiceBox("Employed", "Unemployed", "Self-employed", "Student"));
        TextField incomeField = new TextField();
        incomeField.setTooltip(new Tooltip("Income must be a positive number or >= 0"));
        HBox incomeBox = createQuestionAnswerPair("Income (0 or greater):", incomeField);
        HBox creditScoreBox = createQuestionAnswerPair("Credit Score (0-850):", new TextField());
        HBox debtRatioBox = createQuestionAnswerPair("Debt-to-Income Ratio (0-1):", new TextField());

        Button predictButton = new Button("Predict");
        predictButton.setStyle("-fx-background-color: #00008B; -fx-text-fill: white;");
        predictButton.setDisable(true); // Disable Predict button until all fields are valid

        // Field validations and enabling predict button
        Map<String, Control> fieldMap = new HashMap<>();
        fieldMap.put("Gender", (Control) genderBox.getChildren().get(1));
        fieldMap.put("Education Level", (Control) educationBox.getChildren().get(1));
        fieldMap.put("Marital Status", (Control) maritalStatusBox.getChildren().get(1));
        fieldMap.put("Employment Status", (Control) employmentBox.getChildren().get(1));
        fieldMap.put("Income", (Control) incomeBox.getChildren().get(1));
        fieldMap.put("Credit Score", (Control) creditScoreBox.getChildren().get(1));
        fieldMap.put("Debt-to-Income Ratio", (Control) debtRatioBox.getChildren().get(1));

        fieldMap.values().forEach(control -> {
            if (control instanceof TextField) {
                ((TextField) control).textProperty().addListener((observable, oldValue, newValue) -> enablePredictButton(fieldMap, predictButton));
            } else if (control instanceof ChoiceBox) {
                ((ChoiceBox<?>) control).valueProperty().addListener((observable, oldValue, newValue) -> enablePredictButton(fieldMap, predictButton));
            }
        });

        predictButton.setOnAction(event -> {
            // Collect user input
            Map<String, Object> userInput = new HashMap<>();
            userInput.put("Gender", ((ChoiceBox<String>) fieldMap.get("Gender")).getValue());
            userInput.put("Education Level", ((ChoiceBox<String>) fieldMap.get("Education Level")).getValue());
            userInput.put("Marital Status", ((ChoiceBox<String>) fieldMap.get("Marital Status")).getValue());
            userInput.put("Employment Status", ((ChoiceBox<String>) fieldMap.get("Employment Status")).getValue());
            try {
                userInput.put("Income", Double.parseDouble(((TextField) fieldMap.get("Income")).getText()));
            } catch (NumberFormatException e) {
                showAlert("Income must be a valid positive number!");
                return;
            }
            try {
                int creditScore = Integer.parseInt(((TextField) fieldMap.get("Credit Score")).getText());
                if (creditScore < 0 || creditScore > 850) {
                    showAlert("Credit Score must be between 0 and 850!");
                    return;
                }
                userInput.put("Credit Score", creditScore);
            } catch (NumberFormatException e) {
                showAlert("Credit Score must be a valid number!");
                return;
            }
            try {
                double debtRatio = Double.parseDouble(((TextField) fieldMap.get("Debt-to-Income Ratio")).getText());
                if (debtRatio < 0) {
                    showAlert("Debt-to-Income Ratio must be a non-negative number!");
                    return;
                }
                userInput.put("Debt-to-Income Ratio", debtRatio);
            } catch (NumberFormatException e) {
                showAlert("Debt-to-Income Ratio must be a valid number!");
                return;
            }

            // Predict risk using RandomForest
            String prediction = randomForest.predict(userInput);
            navigateToResultUI(primaryStage, prediction, userInput);
        });

        formLayout.getChildren().addAll(
                titleLabel, genderBox, educationBox, maritalStatusBox, employmentBox, incomeBox, creditScoreBox, debtRatioBox, predictButton
        );

        Scene scene = new Scene(formLayout, 600, 800);
        primaryStage.setScene(scene);
    }

    private HBox createQuestionAnswerPair(String question, Control answer) {
        Label questionLabel = new Label(question);
        questionLabel.setPrefWidth(200);
        answer.setPrefWidth(250);

        HBox hBox = new HBox(10, questionLabel, answer);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private ChoiceBox<String> createChoiceBox(String... values) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(values);
        return choiceBox;
    }

    private void enablePredictButton(Map<String, Control> fieldMap, Button predictButton) {
        boolean allFieldsFilled = fieldMap.entrySet().stream().allMatch(entry -> {
            Control control = entry.getValue();
            if (control instanceof TextField) {
                String text = ((TextField) control).getText();
                if (entry.getKey().equals("Income") || entry.getKey().equals("Credit Score") || entry.getKey().equals("Debt-to-Income Ratio")) {
                    // Check if it's numeric and within valid ranges
                    try {
                        if (entry.getKey().equals("Credit Score")) {
                            int value = Integer.parseInt(text);
                            return value >= 0 && value <= 850;
                        }
                        return Double.parseDouble(text) >= 0;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                } else {
                    return text != null && !text.trim().isEmpty();
                }
            } else if (control instanceof ChoiceBox) {
                return ((ChoiceBox<?>) control).getValue() != null;
            }
            return false;
        });

        predictButton.setDisable(!allFieldsFilled);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to navigate to the result UI with prediction and user input
    private void navigateToResultUI(Stage primaryStage, String prediction, Map<String, Object> userInput) {
        new ResultUI(prediction, userInput, dataset).start(primaryStage); // Pass the dataset to ResultUI for further use
    }
}
