package com.gyamjoDechen.model;

import java.util.*;

public class DecisionTree {
    private TreeNode root;

    // Train the DecisionTree on the provided data
    public void train(List<Map<String, Object>> data, String targetFeature) {
        root = buildTree(data, targetFeature);  // Start building the tree from root
    }

    // Predict the label for the given input
    public String predict(Map<String, Object> input) {
        return predict(root, input);  // Start prediction from the root node
    }

    // Recursive prediction based on node traversal
    private String predict(TreeNode node, Map<String, Object> input) {
        if (node.isLeaf()) {
            return node.getLabel();  // If it's a leaf, return the label
        } else {
            Object value = input.get(node.getFeature());  // Get the feature value from the input data
            if (value == null) {
                return "Unknown";  // Return "Unknown" if the feature is missing
            }

            // If the feature is numeric, compare it to the threshold
            if (value instanceof Number) {
                double numericValue = ((Number) value).doubleValue();
                double threshold = ((Number) node.getSplitValue()).doubleValue();
                if (numericValue <= threshold) {
                    return predict(node.getLeftChild(), input);  // Traverse to left child if condition is met
                } else {
                    return predict(node.getRightChild(), input);  // Traverse to right child otherwise
                }
            } else {
                // If the feature is categorical, compare it to the threshold
                if (value.equals(node.getSplitValue())) {
                    return predict(node.getLeftChild(), input);  // Traverse to left child if values match
                } else {
                    return predict(node.getRightChild(), input);  // Traverse to right child if values do not match
                }
            }
        }
    }

    // Build the decision tree recursively
    private TreeNode buildTree(List<Map<String, Object>> data, String targetFeature) {
        if (allSameLabel(data, targetFeature)) {
            return new TreeNode(data.get(0).get(targetFeature).toString());  // If all labels are the same, create a leaf node
        } else if (data.isEmpty()) {
            return new TreeNode(getMajorityLabel(data, targetFeature));  // If no data is left, return majority label
        }

        // Find the best feature to split the data on
        SplitResult bestSplit = findBestSplit(data, targetFeature);
        if (bestSplit.feature == null) {
            return new TreeNode(getMajorityLabel(data, targetFeature));  // If no valid split found, return majority label
        }

        // Partition the data into two subsets based on the best split
        Map<Boolean, List<Map<String, Object>>> partitions = partitionData(data, bestSplit.feature, bestSplit.threshold);
        TreeNode leftNode = buildTree(partitions.get(true), targetFeature);  // Recursively build the left subtree
        TreeNode rightNode = buildTree(partitions.get(false), targetFeature);  // Recursively build the right subtree

        // Create and return a tree node with the best split and child nodes
        return new TreeNode(bestSplit.feature, bestSplit.threshold, leftNode, rightNode);
    }

    // Check if all labels in the data are the same
    private boolean allSameLabel(List<Map<String, Object>> data, String targetFeature) {
        return data.stream().map(point -> point.get(targetFeature)).distinct().count() == 1;  // Returns true if only one unique label exists
    }

    // Get the majority label in the data
    private String getMajorityLabel(List<Map<String, Object>> data, String targetFeature) {
        Map<Object, Integer> labelCounts = new HashMap<>();
        data.forEach(point -> labelCounts.merge(point.get(targetFeature), 1, Integer::sum));  // Count occurrences of each label
        return Collections.max(labelCounts.entrySet(), Map.Entry.comparingByValue()).getKey().toString();  // Return the label with the highest count
    }

    // Find the best feature to split on by calculating information gain
    private SplitResult findBestSplit(List<Map<String, Object>> data, String targetFeature) {
        double bestGain = Double.NEGATIVE_INFINITY;
        String bestFeature = null;
        Object bestThreshold = null;

        // Iterate through each feature in the data to find the one with the best information gain
        for (String feature : data.get(0).keySet()) {
            if (feature.equals(targetFeature)) continue;  // Skip the target feature

            Set<Object> uniqueValues = new HashSet<>();
            data.forEach(point -> uniqueValues.add(point.get(feature)));  // Collect unique values for the feature

            // Evaluate each unique value of the feature as a possible split threshold
            for (Object value : uniqueValues) {
                double gain = calculateInformationGain(data, targetFeature, feature, value);  // Calculate information gain for this split
                if (gain > bestGain) {
                    bestGain = gain;  // Update the best gain if this split is better
                    bestFeature = feature;  // Set the feature with the best gain
                    bestThreshold = value;  // Set the threshold for the best split
                }
            }
        }
        return new SplitResult(bestFeature, bestThreshold);  // Return the best feature and threshold to split on
    }

    // Calculate the information gain for a specific split
    private double calculateInformationGain(List<Map<String, Object>> data, String targetFeature, String splitFeature, Object threshold) {
        double totalEntropy = calculateEntropy(data, targetFeature);  // Calculate the entropy before the split
        Map<Boolean, List<Map<String, Object>>> partitions = partitionData(data, splitFeature, threshold);  // Partition the data based on the split
        double weightedEntropy = partitions.values().stream()
                .mapToDouble(subset -> ((double) subset.size() / data.size()) * calculateEntropy(subset, targetFeature))  // Calculate the weighted entropy for each partition
                .sum();
        return totalEntropy - weightedEntropy;  // Information gain is the reduction in entropy
    }

    // Partition the data based on the value of a feature
    private Map<Boolean, List<Map<String, Object>>> partitionData(List<Map<String, Object>> data, String feature, Object threshold) {
        Map<Boolean, List<Map<String, Object>>> partitions = new HashMap<>();
        partitions.put(true, new ArrayList<>());
        partitions.put(false, new ArrayList<>());

        // Split the data into two partitions based on the feature value
        for (Map<String, Object> point : data) {
            Object value = point.get(feature);
            if (value != null) {
                if (value instanceof Number && threshold instanceof Number) {
                    double numericValue = ((Number) value).doubleValue();
                    double numericThreshold = ((Number) threshold).doubleValue();
                    if (numericValue <= numericThreshold) {
                        partitions.get(true).add(point);  // Add to left partition if value is less than or equal to the threshold
                    } else {
                        partitions.get(false).add(point);  // Add to right partition otherwise
                    }
                } else if (value.equals(threshold)) {
                    partitions.get(true).add(point);  // Add to left partition if value equals the threshold
                } else {
                    partitions.get(false).add(point);  // Add to right partition otherwise
                }
            }
        }
        return partitions;  // Return the two partitions
    }

    // Calculate the entropy for a given set of data
    private double calculateEntropy(List<Map<String, Object>> data, String targetFeature) {
        Map<Object, Integer> labelCounts = new HashMap<>();
        data.forEach(point -> labelCounts.merge(point.get(targetFeature), 1, Integer::sum));  // Count occurrences of each label

        double entropy = 0.0;
        int size = data.size();
        for (int count : labelCounts.values()) {
            if (count > 0) {
                double proportion = (double) count / size;
                entropy -= proportion * Math.log(proportion) / Math.log(2);  // Calculate entropy using the formula
            }
        }
        return entropy;  // Return the calculated entropy
    }

    // Inner class representing the result of a split (feature and threshold)
    private static class SplitResult {
        String feature;
        Object threshold;

        SplitResult(String feature, Object threshold) {
            this.feature = feature;
            this.threshold = threshold;
        }
    }
}

// Class representing a node in the decision tree
class TreeNode {
    private String feature;
    private Object splitValue;
    private TreeNode leftChild;
    private TreeNode rightChild;
    private String label;
    private boolean isLeaf;

    // Constructor for leaf node with a label
    public TreeNode(String label) {
        this.label = label;
        this.isLeaf = true;  // A leaf node contains a label
    }

    // Constructor for non-leaf node with feature and split value
    public TreeNode(String feature, Object splitValue, TreeNode leftChild, TreeNode rightChild) {
        this.feature = feature;
        this.splitValue = splitValue;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.isLeaf = false;  // Non-leaf nodes contain a feature and split value
    }

    // Check if the node is a leaf
    public boolean isLeaf() {
        return isLeaf;
    }

    public String getFeature() {
        return feature;
    }

    public Object getSplitValue() {
        return splitValue;
    }

    public TreeNode getLeftChild() {
        return leftChild;
    }

    public TreeNode getRightChild() {
        return rightChild;
    }

    public String getLabel() {
        return label;
    }
}
