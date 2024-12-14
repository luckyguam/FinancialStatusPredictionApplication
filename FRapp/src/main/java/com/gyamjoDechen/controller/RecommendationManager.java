package com.gyamjoDechen.controller;

import java.util.List;
import java.util.Map;

public class RecommendationManager {

    private List<Map<String, Object>> roleModels;

    // Constructor to initialize the role models (low-risk profiles)
    public RecommendationManager(List<Map<String, Object>> roleModels) {
        this.roleModels = roleModels;
    }

    // Offers personalized recommendations based on risk prediction and user input
    public void offerPersonalizedRecommendations(String riskPrediction, Map<String, Object> userInput) {
        System.out.println("\n--- Personalized Recommendations ---");

        // Provide general advice based on the risk prediction
        if ("Low".equalsIgnoreCase(riskPrediction)) {
            provideGeneralLowRiskAdvice();
        } else {
            provideGeneralAtRiskAdvice();
        }

        // Assess the user's financial profile against role models
        System.out.println("\n--- Recommendations Based on Role Models ---");
        assessAgainstRoleModels(userInput, riskPrediction);

        // Offer multi-factor recommendations
        System.out.println("\n--- Multi-factor Recommendations ---");
        multiFactorRecommendations(userInput);

        System.out.println("\nMake informed decisions based on your specific circumstances.");
    }

    // Generates a string of recommendations based on risk prediction and user input
    public String getRecommendations(String riskPrediction, Map<String, Object> userInput) {
        StringBuilder recommendations = new StringBuilder();

        // General recommendations based on the user's risk status
        if ("Low".equalsIgnoreCase(riskPrediction)) {
            recommendations.append("You are classified as 'Not at Risk'.\n");
            recommendations.append("1. Continue maintaining your financial stability.\n");
            recommendations.append("2. Focus on building savings or diversifying investments.\n");
            recommendations.append("3. Maintain your credit score by avoiding missed payments.\n");
        } else {
            recommendations.append("You are classified as 'At Risk'.\n");
            recommendations.append("1. Pay off high-interest debts to reduce financial strain.\n");
            recommendations.append("2. Work on improving your credit score by ensuring timely payments.\n");
            recommendations.append("3. Seek financial counseling to create a better budget plan.\n");
        }

        // Compare user input to role models
        recommendations.append("\n--- Recommendations Based on Role Models ---\n");
        double averageIncome = calculateAverage("Income");
        double averageCreditScore = calculateAverage("Credit Score");
        double averageDebtToIncomeRatio = calculateAverage("Debt-to-Income Ratio");

        double userIncome = (double) userInput.get("Income");
        if (userIncome < averageIncome) {
            recommendations.append("- Your income is below the role model average of $")
                    .append(averageIncome)
                    .append(". Consider seeking additional sources of income or negotiating a salary increase.\n");
        } else {
            recommendations.append("- Your income matches the benchmarks of 'Low Risk' individuals.\n");
        }

        double userCreditScore = ((Number) userInput.get("Credit Score")).doubleValue();
        if (userCreditScore < averageCreditScore) {
            recommendations.append("- Your credit score (")
                    .append(userCreditScore)
                    .append(") is below the role model average of ")
                    .append(averageCreditScore)
                    .append(". Focus on improving credit through consistent payments.\n");
        } else {
            recommendations.append("- Your credit score is aligned with the 'Low Risk' role model benchmarks.\n");
        }

        double userDebtToIncome = (double) userInput.get("Debt-to-Income Ratio");
        if (userDebtToIncome > averageDebtToIncomeRatio) {
            recommendations.append("- Your debt-to-income ratio (")
                    .append(userDebtToIncome)
                    .append(") is higher than the role model average of ")
                    .append(averageDebtToIncomeRatio)
                    .append(". Work on reducing debt or increasing your income.\n");
        } else {
            recommendations.append("- Your debt-to-income ratio is within the healthy range based on role model data.\n");
        }

        // Multi-factor recommendations based on user input
        recommendations.append("\n--- Multi-factor Recommendations ---\n");
        if ("Unemployed".equalsIgnoreCase((String) userInput.get("Employment Status"))) {
            recommendations.append("* Consider seeking professional skill development to increase your chances of employment.\n");
        }

        if ("Single".equalsIgnoreCase((String) userInput.get("Marital Status"))) {
            recommendations.append("* Focus on building an emergency fund to cover at least 6-9 months of living expenses.\n");
        } else if ("Married".equalsIgnoreCase((String) userInput.get("Marital Status"))) {
            recommendations.append("* Ensure you and your partner have cohesive financial goals and shared savings plans.\n");
        }

        if ("High School".equalsIgnoreCase((String) userInput.get("Education Level"))) {
            recommendations.append("* Explore upskilling or obtaining further certifications to potentially secure higher-paying roles.\n");
        } else {
            recommendations.append("* Your education level suggests a strong earning potential. Leverage this in your career field.\n");
        }

        return recommendations.toString();
    }

    // Provides general advice for individuals classified as 'Low Risk'
    private void provideGeneralLowRiskAdvice() {
        System.out.println("You are classified as 'Not at Risk'.");
        System.out.println("1. Continue maintaining your financial stability.");
        System.out.println("2. Focus on building savings or diversifying investments.");
        System.out.println("3. Maintain your credit score by avoiding missed payments.");
    }

    // Provides general advice for individuals classified as 'At Risk'
    private void provideGeneralAtRiskAdvice() {
        System.out.println("You are classified as 'At Risk'.");
        System.out.println("1. Pay off high-interest debts to reduce financial strain.");
        System.out.println("2. Work on improving your credit score by ensuring timely payments.");
        System.out.println("3. Seek financial counseling to create a better budget plan.");
    }

    // Assesses user input against role models to provide additional recommendations
    private void assessAgainstRoleModels(Map<String, Object> userInput, String riskPrediction) {
        double averageIncome = calculateAverage("Income");
        double averageCreditScore = calculateAverage("Credit Score");
        double averageDebtToIncomeRatio = calculateAverage("Debt-to-Income Ratio");

        System.out.println("Comparing your profile against successful 'Low Risk' role models:");

        double userIncome = (double) userInput.get("Income");
        if (userIncome < averageIncome) {
            System.out.println("- Your income is below the role model average of $" + averageIncome + ". Consider seeking additional sources of income or negotiating a salary increase.");
        } else {
            System.out.println("- Your income matches the benchmarks of 'Low Risk' individuals.");
        }

        double userCreditScore = ((Number) userInput.get("Credit Score")).doubleValue();
        if (userCreditScore < averageCreditScore) {
            System.out.println("- Your credit score (" + userCreditScore + ") is below the role model average of " + averageCreditScore + ". Focus on improving credit through consistent payments.");
        } else {
            System.out.println("- Your credit score is aligned with the 'Low Risk' role model benchmarks.");
        }

        double userDebtToIncome = (double) userInput.get("Debt-to-Income Ratio");
        if (userDebtToIncome > averageDebtToIncomeRatio) {
            System.out.println("- Your debt-to-income ratio (" + userDebtToIncome + ") is higher than the role model average (" + averageDebtToIncomeRatio + "). Work on reducing debt or increasing your income.");
        } else {
            System.out.println("- Your debt-to-income ratio is within the healthy range based on role model data.");
        }
    }

    // Calculates the average value of a given key (e.g., "Income", "Credit Score", etc.) across role models
    private double calculateAverage(String key) {
        double sum = 0.0;
        int count = 0;

        // Iterate through the role models and calculate the sum of values for the given key
        for (Map<String, Object> roleModel : roleModels) {
            if (roleModel.get(key) != null) {
                sum += (double) roleModel.get(key);
                count++;
            }
        }

        // Return the average or 0 if no values are available
        return count > 0 ? sum / count : 0.0;
    }

    // Provides multi-factor recommendations based on additional user input such as employment status, marital status, etc.
    private void multiFactorRecommendations(Map<String, Object> userInput) {
        if ("Unemployed".equalsIgnoreCase((String) userInput.get("Employment Status"))) {
            System.out.println("* Consider seeking professional skill development to increase your chances of employment.");
        }
        if ("Single".equalsIgnoreCase((String) userInput.get("Marital Status"))) {
            System.out.println("* Focus on building an emergency fund to cover at least 6-9 months of living expenses.");
        }
        if ("High School".equalsIgnoreCase((String) userInput.get("Education Level"))) {
            System.out.println("* Explore upskilling or obtaining further certifications to potentially secure higher-paying roles.");
        }
    }
}
