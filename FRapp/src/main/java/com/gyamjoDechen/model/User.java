package com.gyamjoDechen.model;

import java.util.*;

public class User {

    // Method to get user input for financial risk assessment
    public Map<String, Object> getUserInput() {
        Scanner scanner = new Scanner(System.in);  // Scanner to read input from the console
        Map<String, Object> userInput = new HashMap<>();  // Map to store the user's input

        System.out.println("Enter your information for financial risk assessment:");

        // Gender input validation
        while (true) {
            System.out.print("Gender (Male/Female/Non-binary): ");
            String gender = scanner.nextLine();
            if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("Non-binary")) {
                userInput.put("Gender", gender);  // Add gender to userInput map
                break;
            } else {
                System.out.println("Invalid gender. Please enter Male, Female, or Non-binary.");
            }
        }

        // Education Level input validation
        while (true) {
            System.out.print("Education Level (PhD, Bachelor's, Master's, High School): ");
            String educationLevel = scanner.nextLine();
            if (educationLevel.equalsIgnoreCase("PhD") || educationLevel.equalsIgnoreCase("Bachelor's") ||
                    educationLevel.equalsIgnoreCase("Master's") || educationLevel.equalsIgnoreCase("High School")) {
                userInput.put("Education Level", educationLevel);  // Add education level to userInput map
                break;
            } else {
                System.out.println("Invalid education level. Please enter one of the following: PhD, Bachelor's, Master's, High School.");
            }
        }

        // Marital Status input validation
        while (true) {
            System.out.print("Marital Status (Single/Married/Divorced/Widowed): ");
            String maritalStatus = scanner.nextLine();
            if (maritalStatus.equalsIgnoreCase("Single") || maritalStatus.equalsIgnoreCase("Married") ||
                    maritalStatus.equalsIgnoreCase("Divorced") || maritalStatus.equalsIgnoreCase("Widowed")) {
                userInput.put("Marital Status", maritalStatus);  // Add marital status to userInput map
                break;
            } else {
                System.out.println("Invalid marital status. Please enter Single, Married, Divorced, or Widowed.");
            }
        }

        // Employment Status input validation
        while (true) {
            System.out.print("Employment Status (Employed/Unemployed/Self-employed/Student): ");
            String employmentStatus = scanner.nextLine();
            if (employmentStatus.equalsIgnoreCase("Employed") || employmentStatus.equalsIgnoreCase("Unemployed") ||
                    employmentStatus.equalsIgnoreCase("Self-employed") || employmentStatus.equalsIgnoreCase("Student")) {
                userInput.put("Employment Status", employmentStatus);  // Add employment status to userInput map
                break;
            } else {
                System.out.println("Invalid employment status. Please enter one of the following: Employed, Unemployed, Self-employed, Student.");
            }
        }

        // Income input validation
        while (true) {
            try {
                System.out.print("Income: ");
                double income = scanner.nextDouble();
                if (income > 0) {
                    userInput.put("Income", income);  // Add income to userInput map
                    scanner.nextLine(); // Clear buffer
                    break;
                } else {
                    System.out.println("Income must be a positive number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number for income.");
                scanner.nextLine(); // Clear buffer on exception
            }
        }

        // Credit Score input validation
        while (true) {
            try {
                System.out.print("Credit Score (0-850): ");
                int creditScore = scanner.nextInt();
                if (creditScore >= 0 && creditScore <= 850) {
                    userInput.put("Credit Score", creditScore);  // Add credit score to userInput map
                    scanner.nextLine(); // Clear buffer
                    break;
                } else {
                    System.out.println("Credit score must be between 0 and 850.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number for credit score.");
                scanner.nextLine(); // Clear buffer on exception
            }
        }

        // Debt-to-Income Ratio input validation
        while (true) {
            System.out.print("Debt-to-Income Ratio (e.g., 0.123): ");
            String debtToIncomeRatio = scanner.nextLine();
            try {
                double ratio = Double.parseDouble(debtToIncomeRatio);
                if (ratio >= 0) {
                    userInput.put("Debt-to-Income Ratio", ratio);  // Add debt-to-income ratio to userInput map
                    break;
                } else {
                    System.out.println("Debt-to-Income Ratio must be a non-negative number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number for Debt-to-Income Ratio.");
            }
        }

        scanner.close(); // Close the scanner to prevent memory leak
        return userInput;  // Return the map containing all user inputs
    }
}
