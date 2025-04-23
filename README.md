# 💸 Financial Status Prediction Application

Welcome to our Financial Risk Prediction tool! This Java-based desktop application is designed to help users understand their financial standing — whether they might be at **risk of financial distress** or not. It’s powered by a custom-built **Random Forest model** and follows the **MVC design pattern** to keep the code clean and organized.

---

## 🚀 What This App Does

It takes in financial data like income, credit score, debt ratio, and employment status, and uses a Random Forest model we built from scratch to predict financial risk. If the user is at risk, it gives advice by comparing them with people who are financially stable.

We created this tool to raise awareness and help people make smarter financial decisions.

---

## 🧱 Project Structure

This project follows the **MVC architecture** and is written in **pure Java**, no external ML libraries.

📁 Folder: `FRapp/src/main/java/com/gyamjoDechen/`  
Here’s what each part of the app does:

### ✅ Model Classes
- `User.java`: Holds user input like income, credit score, etc.
- `PredictionModel.java`: Central ML logic using a custom Random Forest.
- `RandomForestBuilder.java`: Builds a forest of decision trees.
- `DecisionTreeBuilder.java`: Builds each individual tree.
- `DataImputation.java`: Handles missing values.
- `Recommendation.java`: Suggests personalized actions for improvement.

### 🎨 View Classes
- `UserInputForm.java`: Collects financial info through a basic UI.
- `FinancialRiskView.java`: Shows whether the user is at risk or not.
- `RecommendationView.java`: Displays helpful suggestions.

### 🧠 Controller Classes
- `PredictionController.java`: Manages the prediction process.
- `RecommendationController.java`: Gets advice based on the result.
- `DataController.java`: Loads CSV data and handles preprocessing.

---

## 📦 Dataset

- 📂 Location: CSV file in project directory (you provide it)
- 🔗 Source: Real-world data from Kaggle
- 🏷️ Example Columns:
  - `income`
  - `credit_score`
  - `employment_status`
  - `debt_to_income_ratio`
  - `loan_default`

---

## 🔁 How the App Works (Step-by-Step)

1. User answers some yes/no financial questions in the app.
2. Missing values in the data are cleaned up automatically.
3. The `RandomForestBuilder` trains trees using bootstrapped samples.
4. Trees vote to predict "At Risk" or "Not At Risk".
5. If at risk, the app compares the user to "role model" users and suggests personalized improvements.

---

## 🧪 Example Questions

- Are you currently unemployed?
- Is your credit score under 600?
- Are you spending more than 40% of your income on debt?
- Have you missed a payment in the last 6 months?

---

## 🗣️ Sample Recommendations

If someone is marked "At Risk", the system might say:
- “Consider finding steady employment.”
- “Your debt ratio is high. Try to pay down existing loans.”
- “Users with similar income but lower debt tend to be more financially stable.”

---

## 🛠️ How to Run the App

### Prerequisites
- Java 8 or above
- JavaFX installed
- A CSV dataset file with financial data

### Run Locally

```bash
git clone https://github.com/luckyguam/FinancialStatusPredictionApplication.git
cd FinancialStatusPredictionApplication/FRapp
javac -d bin src/main/java/com/gyamjoDechen/*.java
java -cp bin com.gyamjoDechen.Main
