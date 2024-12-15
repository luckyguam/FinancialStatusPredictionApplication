package com.gyamjoDechen;

import com.gyamjoDechen.controller.DataManager;
import com.gyamjoDechen.controller.RecommendationManager;
import com.gyamjoDechen.model.RandomForest;
import com.gyamjoDechen.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        // Start the application
        System.out.println("Starting the application...");

        // Initialize the DataManager to handle dataset loading and cleaning
        DataManager dataManager = new DataManager();

        // Load dataset from file
        System.out.println("Loading dataset...");
        List<Map<String, Object>> dataset = dataManager.loadData("C:\\Users\\sherp\\OneDrive\\Desktop\\FRapp\\src\\main\\java\\com\\gyamjoDechen\\controller\\test.csv");
        System.out.println("Dataset loaded with " + dataset.size() + " records.");

        // Clean the dataset
        System.out.println("Cleaning dataset...");
        dataset = dataManager.cleanData(dataset);
        System.out.println("Dataset cleaned. Total records after cleaning: " + dataset.size());

        // Initialize RandomForest with 10 trees and start the training process
        RandomForest randomForest = new RandomForest(10);
        System.out.println("Starting training process...");
        randomForest.train(dataset, "Risk Rating");

        // Create a User object to collect user input
        User user = new User();
        Map<String, Object> userInput = user.getUserInput();

        // Predict the risk rating based on the user input
        System.out.println("Predicting risk rating...");
        String prediction = randomForest.predict(userInput);
        System.out.println("Predicted Risk Rating: " + prediction);

        // Filter Low Risk profiles from the dataset for role models
        List<Map<String, Object>> lowRiskProfiles = dataset.stream()
                .filter(entry -> "Low".equalsIgnoreCase((String) entry.get("Risk Rating")))
                .collect(Collectors.toList());

        // Generate and offer personalized recommendations based on the prediction
        RecommendationManager recommendationManager = new RecommendationManager(lowRiskProfiles);
        recommendationManager.offerPersonalizedRecommendations(prediction, userInput);
    }
}
