
# Internship Exercise – Payment Optimizer

This is a command-line Java application for optimizing the way customer orders are paid using a combination of traditional payment methods and loyalty points. The goal is to maximize available discounts according to a set of business rules.

## 🔧 Technologies Used
- Java 17+
- Maven (build system)
- JUnit 5 (testing)
- Jackson (JSON parsing)

## 📁 Project Structure

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── model/                # Data models: Order, PaymentMethod
│   │   ├── data/                 # JSON loaders for input files
│   │   ├── operations/           # Business logic & entry point
│   │   └── App.java              # Main application class
│   └── resources/                # (Optional) JSON files for test input
└── test/java/org/example/        # JUnit test cases for logic
```

## ▶️ How to Run

1. **Build the fat-jar**
```bash
mvn clean package
```

2. **Run the application**
```bash
java -jar target/InternshipExercise-1.0-SNAPSHOT.jar /path/to/orders.json /path/to/paymentmethods.json
```

3. **Output format (stdout):**
```
<method_id> <amount_spent>
...
```

### ✅ Example:
```
PUNKTY 100.00
BosBankrut 190.00
mZysk 165.00
```

## 📄 Business Rules Implemented

- Full payment using a card from `promotions` → apply card discount
- Full payment using points → apply `PUNKTY` discount
- Partial payment with ≥10% in points → 10% discount on entire order
- Partial points payment excludes any card promotion
- Prioritize spending loyalty points if discount is not negatively impacted
- Ensure all orders are fully paid within available method limits

## 🧪 Running Tests

Run all unit tests using:

```bash
mvn test
```

Key tests include:
- Full payment with points
- Choosing best card discount
- Partial points discount logic
- Fallback to available method
- Enforcement of limits

## 📝 Notes

- Input files must be valid JSON.
- You may place them in the same directory where the `jar` is run.
- The application throws an error if an order cannot be fulfilled with the available funds.

---

© 2025 – Internship Recruitment Task
