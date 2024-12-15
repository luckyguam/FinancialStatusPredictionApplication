package com.gyamjoDechen.Test;

import com.gyamjoDechen.model.DecisionTree;
import com.gyamjoDechen.model.RandomForest;
import com.gyamjoDechen.controller.DataManager;
import com.gyamjoDechen.model.User;
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
        System.out.println("Running testModelAccuracy...");

        // Train the RandomForest model using the dataset
        randomForest.train(dataset, "Risk Rating");
        System.out.println("RandomForest model trained successfully.");

        // Hardcoded test data using Map<String, Object> for each user
        List<Map<String, Object>> testUsers = List.of(
                // High income, low debt-to-income ratio, excellent credit score
                Map.of("Gender", "Male", "Education Level", "PhD", "Marital Status", "Widowed", "Income", 200000, "Credit Score", 850, "Employment Status", "Employed", "Debt-to-Income Ratio", 0.0), // Expected: Not At Risk

                // High income, low debt-to-income ratio, good credit score
                Map.of("Gender", "Female", "Education Level", "Master's", "Marital Status", "Married", "Income", 150000, "Credit Score", 800, "Employment Status", "Employed", "Debt-to-Income Ratio", 0.2), // Expected: Not At Risk

                // Medium-high income, low debt-to-income ratio, slightly lower credit score
                Map.of("Gender", "Male", "Education Level", "Bachelor's", "Marital Status", "Single", "Income", 120000, "Credit Score", 740, "Employment Status", "Self-Employed", "Debt-to-Income Ratio", 0.3), // Expected: Not At Risk

                // Medium income, moderate debt-to-income ratio, decent credit score
                Map.of("Gender", "Female", "Education Level", "Bachelor's", "Marital Status", "Divorced", "Income", 85000, "Credit Score", 720, "Employment Status", "Employed", "Debt-to-Income Ratio", 0.4), // Expected: Not At Risk

                // Medium income, high debt-to-income ratio, lower credit score
                Map.of("Gender", "Male", "Education Level", "Bachelor's", "Marital Status", "Married", "Income", 75000, "Credit Score", 680, "Employment Status", "Unemployed", "Debt-to-Income Ratio", 0.8), // Expected: At Risk

                // Medium-low income, higher debt-to-income ratio, low credit score
                Map.of("Gender", "Female", "Education Level", "High School", "Marital Status", "Single", "Income", 60000, "Credit Score", 620, "Employment Status", "Self-Employed", "Debt-to-Income Ratio", 1.0), // Expected: At Risk

                // Low income, very high debt-to-income ratio, poor credit score
                Map.of("Gender", "Male", "Education Level", "High School", "Marital Status", "Widowed", "Income", 30000, "Credit Score", 500, "Employment Status", "Unemployed", "Debt-to-Income Ratio", 1.5), // Expected: At Risk

                // Very low income, extremely high debt-to-income ratio, lowest credit score
                Map.of("Gender", "Female", "Education Level", "High School", "Marital Status", "Widowed", "Income", 1000, "Credit Score", 400, "Employment Status", "Unemployed", "Debt-to-Income Ratio", 2.0)  // Expected: At Risk
        );

        // Hardcoded expected results
        String[] expectedOutputs = {
                "Not At Risk", // Strong profile: High income, excellent financial ratios
                "Not At Risk", // Strong profile: High income, good financial state
                "Not At Risk", // Above-average profile: Low ratio, healthy score
                "Not At Risk", // Medium profile: Manageable ratios and decent score
                "At Risk",     // Risky: High DTI, unemployed, moderate score
                "At Risk",     // Risky: Higher DTI and average credit score
                "At Risk",     // High risk: Very poor financial conditions
                "At Risk"      // Very high risk: Lowest score and severe financial imbalance
        };

        int correctPredictions = 0;

        for (int i = 0; i < testUsers.size(); i++) {
            // Predict the risk for each user using the trained RandomForest model
            String prediction = randomForest.predict(testUsers.get(i));

            // Map prediction result: "Low" -> "Not At Risk", everything else -> "At Risk"
            String result = prediction.equals("Low") ? "Not At Risk" : "At Risk";

            // Compare the actual prediction with the expected result
            if (result.equals(expectedOutputs[i])) {
                correctPredictions++;
            }

            // Print prediction and expected result
            System.out.println("User " + (i + 1) + ": Predicted = " + result + ", Expected = " + expectedOutputs[i]);
        }

        // Calculate and print the accuracy
        double accuracy = ((double) correctPredictions / 8) * 100;
        System.out.println("Model Accuracy: " + accuracy + "%");

        // Assert that accuracy meets the specified threshold (e.g., 85%)
        assertTrue("Model accuracy is below the acceptable threshold!", accuracy >= 80);
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
