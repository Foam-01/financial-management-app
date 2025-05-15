# Financial Management App

A personal finance management Android application that helps users record income and expenses, view transaction history, analyze financial data through graphs, and manage user profiles easily.

## Technologies Used

- Java: Programming language for Android development  
- Android SDK: Tools and APIs for building Android apps  
- SQLite: Internal database management system to store transaction data  
- Android Studio: IDE for development and testing  

## Key Features

- User registration and login system  
- Record income and expense transactions  
- View detailed transaction history  
- Display financial statistics through graphs  
- Manage user profile information  

## Project Structure

- `DBHelper.java` - Helper class for managing SQLite database  
- `Transaction.java` - Data model for financial transactions  
- `TransactionAdapter.java` - Adapter for displaying transaction lists  
- `LoginActivity.java` and `SignUpActivity.java` - Screens for login and sign-up  
- `MainActivity.java` - Main screen after user login  
- `RecordFragment.java` - Screen for adding new transactions  
- `GraphFragment.java` - Screen for displaying financial charts  
- `ProfileFragment.java` - Screen for managing user profiles  
- `res/` folder - Contains resources such as layouts, images, and strings  
- `AndroidManifest.xml` - App configuration and permissions  

## How to Install and Run Locally

1. Clone the repository:  
   ```bash
   git clone https://github.com/Foam-01/financial-management-app.git
2. Open the project in Android Studio

3. Let Gradle sync and download dependencies

4. Connect a physical Android device or launch an emulator

5. Run the app by clicking the Run button or pressing Shift + F10
