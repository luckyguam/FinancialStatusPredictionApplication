package com.gyamjoDechen.model;

import java.util.*;

public class RandomForest {
    // List of DecisionTree objects that make up the RandomForest
    public List<DecisionTree> trees;

    // List to hold Out-Of-Bag (OOB) samples for each tree
    public List<List<Map<String, Object>>> oobSamples;

    // Constructor to initialize the RandomForest with a specified number of trees
    public RandomForest(int numTrees) {
        System.out.println("Initializing RandomForest with " + numTrees + " trees...");
        trees = new ArrayList<>(numTrees);

        // Create the specified number of DecisionTree objects
        for (int i = 0; i < numTrees; i++) {
            trees.add(new DecisionTree());
            System.out.println("Tree " + (i + 1) + " created.");
        }
        System.out.println("RandomForest initialized successfully!");
    }

    // Train method for the RandomForest; iterates through trees and trains them
    public void train(List<Map<String, Object>> data, String targetFeature) {
        System.out.println("\nStarting training process for RandomForest...");
        oobSamples = new ArrayList<>();

        // For each tree, create bootstrap sample, train the tree, and store OOB samples
        for (int i = 0; i < trees.size(); i++) {
            System.out.println("\nTraining tree " + (i + 1) + "...");

            // Create bootstrap and OOB samples for training
            Map<String, List<Map<String, Object>>> sampleResult = createBootstrapSampleWithOOB(data);
            List<Map<String, Object>> inBag = sampleResult.get("inBag");
            List<Map<String, Object>> oob = sampleResult.get("oob");

            System.out.println("Bootstrap sample created for tree " + (i + 1) + ": In-bag size = " + inBag.size() + ", OOB size = " + oob.size());

            // Train the current tree using the bootstrap sample (in-bag data)
            trees.get(i).train(inBag, targetFeature);
            System.out.println("Tree " + (i + 1) + " training complete.");

            // Store the OOB samples for later accuracy calculation
            oobSamples.add(oob);
        }

        System.out.println("\nTraining complete for all trees in the RandomForest.");
    }

    // Method to create bootstrap sample and separate OOB data
    private Map<String, List<Map<String, Object>>> createBootstrapSampleWithOOB(List<Map<String, Object>> data) {
        System.out.println("Creating bootstrap sample with OOB data...");

        // List to hold the in-bag data (data used for training the tree)
        List<Map<String, Object>> inBag = new ArrayList<>();
        // Set to hold the OOB data (data not selected for the bootstrap sample)
        Set<Map<String, Object>> oobSet = new HashSet<>(data);
        Random random = new Random();

        // Randomly sample from the data to create the bootstrap sample (in-bag data)
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> sampledPoint = data.get(random.nextInt(data.size()));
            inBag.add(sampledPoint);
            oobSet.remove(sampledPoint); // Remove sampled point from OOB set
        }

        System.out.println("Bootstrap sample created: In-bag size = " + inBag.size() + ", OOB size = " + oobSet.size());

        // Return a map containing both in-bag and OOB samples
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("inBag", inBag);
        result.put("oob", new ArrayList<>(oobSet));
        return result;
    }

    // Method to predict the class label for a given input using majority voting from all trees
    public String predict(Map<String, Object> input) {
        System.out.println("\nStarting prediction process...");
        Map<String, Integer> votes = new HashMap<>();

        // Get predictions from each tree and count the votes
        for (int i = 0; i < trees.size(); i++) {
            System.out.println("Getting prediction from tree " + (i + 1) + "...");
            String prediction = trees.get(i).predict(input);
            votes.put(prediction, votes.getOrDefault(prediction, 0) + 1);
        }

        // Find the class label with the most votes from the trees
        String finalPrediction = Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Final prediction: " + finalPrediction);
        return finalPrediction;
    }

    // Method to calculate the Out-Of-Bag (OOB) accuracy of the RandomForest
    public double calculateOOBAccuracy(String targetFeature) {
        System.out.println("\nCalculating OOB accuracy...");
        int correctPredictions = 0;
        int totalOOBSamples = 0;

        // Iterate over all trees and their corresponding OOB data
        for (int treeIndex = 0; treeIndex < trees.size(); treeIndex++) {
            System.out.println("Processing OOB data for tree " + (treeIndex + 1) + "...");
            DecisionTree tree = trees.get(treeIndex);
            List<Map<String, Object>> oobData = oobSamples.get(treeIndex);

            // Check the predictions for each OOB sample
            for (Map<String, Object> oobSample : oobData) {
                String trueLabel = oobSample.get(targetFeature).toString();
                String prediction = tree.predict(oobSample);

                System.out.println("OOB Sample: True Label = " + trueLabel + ", Predicted = " + prediction);
                if (prediction.equals(trueLabel)) {
                    correctPredictions++;
                }
                totalOOBSamples++;
            }
        }

        // Calculate and return the OOB accuracy
        double oobAccuracy = (double) correctPredictions / totalOOBSamples;
        System.out.println("OOB accuracy calculated: " + oobAccuracy);
        return oobAccuracy;
    }
}
