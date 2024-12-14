package com.gyamjoDechen.controller;

import java.io.*;
import java.util.*;

/**
 * Handles loading and cleaning of data from CSV files.
 * Imputes missing values for numerical and categorical columns.
 */
public class DataManager {

    /**
     * Loads data from a CSV file and returns it as a list of maps.
     * Each map represents a row where the column headers are the keys.
     */
    public List<Map<String, Object>> loadData(String filePath) {
        List<Map<String, Object>> dataset = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read the header row to get column names.
            String line;
            String[] headers = br.readLine().split(",");

            // Read each row of data
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);  // Split the row into values
                Map<String, Object> dataPoint = new HashMap<>();

                // Assign values to their respective keys (columns)
                for (int i = 0; i < headers.length; i++) {
                    String key = headers[i].trim();  // Remove any spaces from the header
                    String value = values[i].trim();  // Remove spaces from the value

                    // Handle numerical columns (Income, Credit Score, Debt-to-Income Ratio)
                    if (key.equals("Income") || key.equals("Credit Score") || key.equals("Debt-to-Income Ratio")) {
                        if (!value.isEmpty()) {
                            try {
                                // If the value is valid, parse it as a double
                                dataPoint.put(key, Double.parseDouble(value));
                            } catch (NumberFormatException e) {
                                // If parsing fails, set it to 0.0
                                dataPoint.put(key, 0.0);
                            }
                        } else {
                            // If the value is empty, set it to null
                            dataPoint.put(key, null);
                        }
                    } else {
                        // For other columns, add the value as is, or null if it's empty
                        dataPoint.put(key, value.isEmpty() ? null : value);
                    }
                }
                dataset.add(dataPoint);  // Add the row to the dataset
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle any I/O exceptions
        }
        return dataset;  // Return the loaded dataset
    }

    /**
     * Cleans the data by imputing missing values for numerical and categorical columns.
     */
    public List<Map<String, Object>> cleanData(List<Map<String, Object>> data) {
        // Impute missing numerical values with the mean
        imputeNumericalWithMean(data, "Income");
        imputeNumericalWithMean(data, "Credit Score");
        imputeNumericalWithMean(data, "Debt-to-Income Ratio");

        // Impute missing categorical values with the mode (most frequent value)
        imputeCategoricalWithMode(data, "Gender");
        imputeCategoricalWithMode(data, "Education Level");
        imputeCategoricalWithMode(data, "Marital Status");
        imputeCategoricalWithMode(data, "Employment Status");

        return data;  // Return the cleaned dataset
    }

    /**
     * Imputes missing numerical values by replacing them with the mean of the column.
     */
    private void imputeNumericalWithMean(List<Map<String, Object>> data, String key) {
        double sum = 0.0;
        int count = 0;

        // Calculate the sum and count of non-null values
        for (Map<String, Object> row : data) {
            if (row.get(key) != null) {
                sum += (double) row.get(key);
                count++;
            }
        }

        double mean = count > 0 ? sum / count : 0.0;  // Calculate the mean

        // Replace null values with the mean
        for (Map<String, Object> row : data) {
            if (row.get(key) == null) {
                row.put(key, mean);
            }
        }
    }

    /**
     * Imputes missing categorical values by replacing them with the mode (most frequent value).
     */
    private void imputeCategoricalWithMode(List<Map<String, Object>> data, String key) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        // Count the frequency of each value in the column
        for (Map<String, Object> row : data) {
            String value = (String) row.get(key);
            if (value != null && !value.isEmpty()) {
                frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
            }
        }

        // Find the most frequent value (mode)
        String mode = frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new RuntimeException("No mode found"))
                .getKey();

        // Replace null values with the mode
        for (Map<String, Object> row : data) {
            if (row.get(key) == null) {
                row.put(key, mode);
            }
        }
    }
}
