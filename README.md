# 💸 Financial Status Prediction Application

Welcome to our Financial Risk Prediction tool! This Java-based desktop application is designed to help users understand their financial standing — whether they might be at **risk of financial distress** or not. It’s powered by a custom-built **Random Forest model** and follows the **MVC design pattern** to keep the code clean and organized.

---

## 📍 What It Does

Our application takes in basic financial information (like income, credit score, employment status, etc.) and uses machine learning to predict whether a person is financially "At Risk" or "Not At Risk." If the user is at risk, the app also gives **personalized recommendations** based on role models who are financially stable.

It’s especially helpful for students, young professionals, or anyone looking to gain insight into their financial health and take proactive steps to improve it.

---

## 🧱 Project Architecture

We’ve used the **Model-View-Controller (MVC)** structure for this project:

### ✅ Model
- **User**: Represents user financial data.
- **PredictionModel**: Uses a Random Forest algorithm to make predictions.
- **DecisionTreeBuilder** / **RandomForestBuilder**: Custom-built tree training logic.
- **DataImputation**: Cleans and fills missing values in the dataset.
- **Recommendation**: Generates financial advice based on the predicted result.

### 🎨 View
- **UserInputForm**: A form where users answer simple yes/no financial questions.
- **FinancialRiskView**: Shows whether the user is at financial risk.
- **RecommendationView**: Displays helpful suggestions to improve financial status.

### 🧠 Controller
- **PredictionController**: Connects user input to the prediction logic.
- **RecommendationController**: Handles the logic for showing suggestions.
- **DataController**: Loads and prepares the dataset for training and testing.

---

## 🗃️ Dataset Info

We’re using publicly available datasets from **Kaggle**:
- Real-world financial profiles (students, workers, etc.)
- CSV files with columns like:
  - Income
  - Employment status
  - Credit score
  - Debt-to-Income ratio
  - Loan history

---

## 🔁 How It Works (Prediction Flow)

1. **User fills out** the yes/no form with their financial details.
2. **Missing values** in the dataset (if any) are handled automatically.
3. The Random Forest model is trained using bootstrapped samples.
4. Each decision tree votes on the user's financial status.
5. A final result is shown: "At Risk" or "Not At Risk."
6. If "At Risk", the app compares the user with financially stable role models and shows personalized recommendations.

---

## 💻 How to Run It

### Prerequisites
- Java 8 or higher
- JavaFX (for the UI)
- IDE like IntelliJ IDEA or Eclipse (or just command line)
- Dataset CSV file

### Clone and Run

```bash
git clone https://github.com/luckyguam/FinancialStatusPredictionApplication.git
cd FinancialStatusPredictionApplication
javac -d bin src/**/*.java
java -cp bin Main
