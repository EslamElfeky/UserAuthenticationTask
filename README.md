# 🔐 Authentication App (Android)

A secure and modern Android authentication app built with **Jetpack Compose**, **Material Design 3**, and **MVVM architecture**. It provides a complete flow for **login**, **registration**, and **session management** using encrypted storage.

## ✨ Features Implemented

### 1. Login & Registration Screens
- Beautiful **Material 3 UI** built with **Jetpack Compose**
- Form validation with real-time error feedback
- Password visibility toggle for better UX

### 2. Security
- Uses **EncryptedSharedPreferences** for credential storage with **AES256-GCM** encryption
- Master key generated using **AES256 key scheme**
- Ensures secure password handling and storage

### 3. Validation
- Email format validation using Android `Patterns`
- Password strength check (minimum 6 characters)
- Confirm password matching with instant feedback
- Real-time contextual error messages for user guidance

### 4. Session Persistence
- **Jetpack DataStore** for managing session state
- Persists login state even after app restarts
- Stores logged-in user's email securely

### 5. Navigation
- Smooth navigation between **Login**, **Register**, and **Home** screens
- Displays a success message and logged-in email on the Home screen
- **Logout** functionality clears session data

### 6. User Experience
- Loading states during authentication
- Clean, modern **Material Design 3** interface
- Proper keyboard types for email and password fields

## 🚀 Key Components

- **SecureStorageManager** — Handles encrypted credential storage.
- **SessionManager** — Manages user sessions with DataStore.
- **ValidationUtils** — Centralized validation logic for input fields.
- **Screens** — Login, Register, and Home screens with clean state management.

## 📱 Usage

The system automatically handles the following:

- ✅ User registration with validation
- ✅ Secure credential storage using encryption
- ✅ Login authentication and verification
- ✅ Session persistence across restarts
- ✅ Logout and session clearing

## ⚙️ Tech Stack

- Kotlin
- Jetpack Compose
- Material Design 3
- EncryptedSharedPreferences
- DataStore (Preferences)
- MVVM Architecture

---

Would you like me to include **installation instructions**, **architecture diagram**, or **API integration** setup next?

