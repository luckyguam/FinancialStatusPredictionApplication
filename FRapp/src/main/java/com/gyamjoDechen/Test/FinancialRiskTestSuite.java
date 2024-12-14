package com.gyamjoDechen.Test;

import com.gyamjoDechen.model.DecisionTree;
import com.gyamjoDechen.model.RandomForest;
import com.gyamjoDechen.controller.DataManager;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class FinancialRiskTestSuite {

    private DataManager dataManager; // Object for handling data operations
    private List<Map<String, Object>> dataset; // List to hold dataset rows as maps
    private RandomForest randomForest; // Object for RandomForest model

    @Before
    public void setUp() {
        // Initialize the test environment, loading data and cleaning it
        System.out.println("Setting up the test environment...");

        // Initialize DataManager and load the dataset
        dataManager = new DataManager();
        dataset = dataManager.loadData("C:\\Users\\sherp\\OneDrive\\Desktop\\FRapp\\src\\main\\java\\com\\gyamjoDechen\\controller\\test.csv"); // Update the path as needed
        System.out.println("Dataset loaded with " + dataset.size() + " rows.");

        // Clean the data (Handle missing values)
        dataset = dataManager.cleanData(dataset);
        System.out.println("Dataset cleaned. Total rows after cleaning: " + dataset.size());

        // Initialize RandomForest with 10 trees
        randomForest = new RandomForest(10);
    }

    @Test
    public void testHandleMissingData() {
        // Test that no missing values are left in the dataset after cleaning
        System.out.println("Running testHandleMissingData...");

        long missingValuesBefore = dataset.stream()
                .flatMap(map -> map.values().stream())
                .filter(Objects::isNull)
                .count();
        // Assert that no missing values are present
        assertEquals("Missing values detected after cleaning!", 0, missingValuesBefore);

        // Verify all required attributes are present in each data point
        for (Map<String, Object> dataPoint : dataset) {
            assertNotNull("Income field is missing!", dataPoint.get("Income"));
            assertNotNull("Credit Score field is missing!", dataPoint.get("Credit Score"));
            assertNotNull("Debt-to-Income Ratio field is missing!", dataPoint.get("Debt-to-Income Ratio"));
            assertNotNull("Gender field is missing!", dataPoint.get("Gender"));
            assertNotNull("Marital Status field is missing!", dataPoint.get("Marital Status"));
        }
    }

    @Test
    public void testDecisionTreeBuild() {
        // Test that a decision tree can be trained and used for predictions
        System.out.println("Running testDecisionTreeBuild...");

        DecisionTree decisionTree = new DecisionTree(); // Create a new DecisionTree
        decisionTree.train(dataset, "Risk Rating"); // Train the tree with the dataset

        // Ensure the decision tree is built successfully and provides a prediction
        String prediction = decisionTree.predict(dataset.get(0)); // Predict for the first available record
        assertNotNull("Decision tree failed to provide a prediction!", prediction);

        System.out.println("Decision tree built successfully and predicts: " + prediction);
    }

    @Test
    public void testRandomForestTrainAndPredict() {
        // Test RandomForest training and prediction functionality
        System.out.println("Running testRandomForestTrainAndPredict...");

        randomForest.train(dataset, "Risk Rating"); // Train the random forest with the dataset

        // Assert that all 10 trees are trained
        assertEquals("RandomForest does not have the correct number of trees!", 10, randomForest.trees.size());

        // Prepare mock input data for prediction
        Map<String, Object> mockInput = Map.of(
                "Gender", "Female",
                "Income", 80000.0,
                "Credit Score", 750,
                "Debt-to-Income Ratio", 0.2,
                "Marital Status", "Married",
                "Education Level", "Bachelor's",
                "Employment Status", "Employed"
        );

        // Predict using the RandomForest model and validate the result
        String prediction = randomForest.predict(mockInput);
        assertNotNull("Prediction should not be null!", prediction);
        assertTrue("Prediction is not valid!", Arrays.asList("High", "Low").contains(prediction));

        // Output the prediction result
        assertTrue("RandomForest failed to provide a valid prediction!", prediction.equals("High") || prediction.equals("Low"));
        System.out.println("RandomForest Prediction: " + (prediction.equals("High") ? "At Risk" : "Not at Risk"));
    }

    @Test
    public void testModelAccuracy() {
        // Test the accuracy of the trained RandomForest model
        System.out.println("Running testModelAccuracy...");

        randomForest.train(dataset, "Risk Rating"); // Train the random forest with the dataset

        // Evaluate the model on a set of hardcoded test cases
        int correctPredictions = 0;
        int totalPredictions = dataset.size(); // Using the full dataset for predictions

        // Hardcoded test data with expected risk levels
        List<Map<String, Object>> testCases = Arrays.asList(
                // Best features (Expected: Low risk -> Not At Risk)
                Map.of(
                        "Gender", "Female",
                        "Income", 120000.0,
                        "Credit Score", 800,
                        "Debt-to-Income Ratio", 0.1,
                        "Marital Status", "Married",
                        "Education Level", "Master's",
                        "Employment Status", "Employed"
                ),
                // Worst features (Expected: High risk -> At Risk)
                Map.of(
                        "Gender", "Male",
                        "Income", 20000.0,
                        "Credit Score", 400,
                        "Debt-to-Income Ratio", 0.9,
                        "Marital Status", "Single",
                        "Education Level", "High School",
                        "Employment Status", "Unemployed"
                ),
                // Some good features (Expected: Low risk -> Not At Risk)
                Map.of(
                        "Gender", "Female",
                        "Income", 80000.0,
                        "Credit Score", 740,
                        "Debt-to-Income Ratio", 0.25,
                        "Marital Status", "Married",
                        "Education Level", "Bachelor's",
                        "Employment Status", "Employed"
                ),
                // Some bad features (Expected: High risk -> At Risk)
                Map.of(
                        "Gender", "Male",
                        "Income", 30000.0,
                        "Credit Score", 500,
                        "Debt-to-Income Ratio", 0.8,
                        "Marital Status", "Divorced",
                        "Education Level", "Some College",
                        "Employment Status", "Employed"
                ),
                // Balanced features (Expected: Low risk -> Not At Risk)
                Map.of(
                        "Gender", "Female",
                        "Income", 50000.0,
                        "Credit Score", 700,
                        "Debt-to-Income Ratio", 0.3,
                        "Marital Status", "Single",
                        "Education Level", "Bachelor's",
                        "Employment Status", "Employed"
                ),
                // Mixed features (Expected: High risk -> At Risk)
                Map.of(
                        "Gender", "Male",
                        "Income", 40000.0,
                        "Credit Score", 550,
                        "Debt-to-Income Ratio", 0.7,
                        "Marital Status", "Single",
                        "Education Level", "High School",
                        "Employment Status", "Unemployed"
                )
        );

        // Evaluate model on each test case
        for (Map<String, Object> testCase : testCases) {
            String actualRiskRating = testCase.containsKey("Risk Rating") ? (String) testCase.get("Risk Rating") : "Unknown";  // Replace with actual if available
            String predictedRisk = randomForest.predict(testCase);

            // Check if prediction matches actual risk level
            if ((actualRiskRating.equalsIgnoreCase("Low") && predictedRisk.equalsIgnoreCase("Low")) ||
                    (!actualRiskRating.equalsIgnoreCase("Low") && predictedRisk.equalsIgnoreCase("High"))) {
                correctPredictions++;
            }
        }

        // Calculate and assert the model's accuracy
        double accuracy = (double) correctPredictions / testCases.size();
        assertTrue("Model accuracy is below acceptable threshold!", accuracy >= 0.8);
        System.out.println("Model Accuracy: " + accuracy);
    }

    @Test
    public void testModelPerformance() {
        // Test the performance (speed) of model predictions
        System.out.println("Running testModelPerformance...");

        randomForest.train(dataset, "Risk Rating"); // Train the model with the dataset

        // Reduced number of samples for faster testing
        int numSamples = 500;
        Map<String, Object> testInput = Map.of(
                "Gender", "Male",
                "Income", 60000.0,
                "Credit Score", 720,
                "Debt-to-Income Ratio", 0.2,
                "Marital Status", "Single",
                "Education Level", "High School",
                "Employment Status", "Self-employed"
        );

        // Track the time taken for predictions
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numSamples; i++) {
            randomForest.predict(testInput); // Predict multiple times
        }
        long endTime = System.currentTimeMillis();

        // Calculate the total time and assert if it's within acceptable bounds
        long totalTime = endTime - startTime;
        System.out.println("Total time taken for " + numSamples + " predictions: " + totalTime + " ms");
        assertTrue("Model performance is too slow!", totalTime < 1000); // Should take less than 1 second for 500 predictions
    }
}
