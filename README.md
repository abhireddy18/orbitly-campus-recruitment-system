# Orbitly

Orbitly is an Android campus recruitment app for students, employers, and administrators. It supports job discovery, applications, interview scheduling, feedback, and saved jobs.

## Build

Open this project in Android Studio, or build a debug APK from Windows:

```bat
gradlew.bat assembleDebug
```

Install on a connected Android device or emulator:

```bat
gradlew.bat installDebug
```

The Android application ID is `com.example.crs2025` and the minimum Android version is API 24.

## Firebase setup

The app uses Firebase Authentication and Realtime Database. Configure `app/google-services.json` for the Firebase Android app and create a Realtime Database instance with rules appropriate for your deployment. Do not use open read/write rules for real users.

Student and company accounts authenticate with Firebase Authentication. Admin sign-in additionally requires a server-issued Firebase Auth custom claim named `admin` with the boolean value `true`. Set this claim only from a trusted server using the Firebase Admin SDK; the Android client cannot grant admin access to itself.

Saved jobs are stored locally per signed-in user on the current device. They do not currently sync between devices.
