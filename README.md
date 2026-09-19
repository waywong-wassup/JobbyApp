# Jobby 💼

**Jobby** is a modern, offline-first Android application designed to be your ultimate job search buddy. Built with Jetpack Compose and Firebase, it helps you organise your job hunt by tracking applications, interviews, and offers in one centralised place.

<img width="150" height="150" alt="Screenshot 2026-08-18 221358" src="https://github.com/user-attachments/assets/51c8db0b-fda5-4990-8a23-dc5979e205d8" />

(originally, the sign the person holding in the app icon was "hire me plz" 😏)

## 🚀 Features

*   **Offline-First Architecture:** Your data is always accessible. View and edit your job applications even without an internet connection.
*   **Authentication Options:** 
    *   **Google Sign-In:** Modern, one-tap sign-in using the **Android Credential Manager API**.
    *   **Email/Password:** Traditional secure login and sign-up via Firebase Auth.
*   **Smart Cloud Sync:** Intelligent two-way synchronization that automatically pushes unsynced data upon login or app launch.
*   **Persistent Preferences:** Remembers your settings (like Sync status) even after app restarts using **Jetpack DataStore**.
*   **Guest Mode:** Start tracking jobs immediately without an account; your data is saved locally and can be synced later.
*   **Comprehensive Tracking:** Manage detailed job information including:
    *   Job Title & Company
    *   Application Status (To Apply, Applied, Interview, Offered, etc.)
    *   Salary, Location, and Application URLs
    *   Contact Information & Custom Notes
*   **Modern UI:** A clean, reactive interface built entirely with **Jetpack Compose** and **Material 3**.

## 📸 Screenshots

| Start Screen | List Screen | Details Screen |
| :---: | :---: | :---: |
|<img width="181" height="404" alt="image" src="https://github.com/user-attachments/assets/e1b2e6c2-d347-4c69-958c-bd7639d058c2" />| <img width="181" height="404" alt="image" src="https://github.com/user-attachments/assets/ab4ffd25-b60c-40c5-bbba-e0dbf82ca841" />| <img width="181" height="404" alt="image" src="https://github.com/user-attachments/assets/972ee9c5-4e36-4aed-9d64-f16404077d3c" />|


## 🛠 Tech Stack

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
*   **Database (Local):** [Room](https://developer.android.com/training/data-storage/room)
*   **Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
*   **Identity:** [Credential Manager API](https://developer.android.com/training/sign-in/credential-manager)
*   **Backend:** [Firebase](https://firebase.google.com/) (Auth, Firestore, Analytics)
*   **Architecture:** MVVM + Repository Pattern + Offline-First Sync Logic
*   **Navigation:** Jetpack Navigation Compose (Type-safe routes)
*   **Concurrency:** Kotlin Coroutines & Flow

## 🏗 Architecture

Jobby follows the **Offline-First** architecture. The UI always reads from the local Room database, ensuring the app is always fast and responsive. A "Sync" layer sits between the local and remote repositories, handling the logic of pushing updates to Firestore when a connection is available.

```mermaid
graph TD
    UI[Compose UI] --> VM[ViewModel]
    VM --> Repository[Sync Repository]
    Repository --> Room[Room Database]
    Repository --> Firestore[Firebase Firestore]
```

## 🏁 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   A Firebase Project

### Setup
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/JobbyApp.git
    ```
2.  **Add Firebase:**
    *   Create an Android app in your Firebase Console.
    *   Download the `google-services.json` and place it in the `app/` directory.
    *   Enable **Email/Password**, **Google**, and **Anonymous** Authentication in the Firebase Console.
    *   Enable **Cloud Firestore**.
3.  **Add Configuration:**
    *   Open `local.properties` in the project root.
    *   Add your Firebase Web Client ID: 
        `GOOGLE_WEB_CLIENT_ID=your_web_client_id_here.apps.googleusercontent.com`
4.  **Build & Run:**
    *   Sync Gradle and run the app on your emulator or physical device.



