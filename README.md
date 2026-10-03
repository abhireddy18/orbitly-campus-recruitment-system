# 🎓 Orbitly - Campus Recruitment System

Orbitly is a comprehensive Android application designed to streamline the campus recruitment process. It bridges the gap between students, employers, and university administrators by providing a centralized platform for job discovery, application tracking, interview scheduling, and feedback management.

## 🌟 Key Features

### 🧑‍🎓 For Students
- **Job Discovery**: Browse and search for the latest job openings posted by companies.
- **Easy Application**: Apply for jobs with a single click using registered profiles.
- **Application Tracking**: Monitor the status of submitted applications in real-time.
- **Interview Scheduling**: View upcoming interview dates, times, and details.
- **Saved Jobs**: Bookmark interesting opportunities to apply for later.
- **Feedback System**: Receive and view feedback from companies after interviews.

### 🏢 For Employers (Companies)
- **Job Posting**: Create, edit, and manage job openings with detailed requirements.
- **Application Review**: Browse through all student applications and filter candidates.
- **Interview Management**: Schedule interviews for shortlisted candidates and manage timings.
- **Feedback Provision**: Provide professional feedback to students after the interview process.
- **Candidate Tracking**: Maintain a history of all applicants and their progress.

### 🛡️ For Administrators
- **User Management**: Oversee and manage student and company registrations.
- **Job Oversight**: Monitor all active job postings and ensure quality control.
- **Interview Coordination**: Track and manage interview schedules across the campus.
- **Feedback Monitoring**: Review feedback given by companies to students.
- **System Administration**: Manage the overall recruitment workflow and resolve disputes.

## 🛠️ Technical Stack

- **Language**: Java
- **Platform**: Android SDK (Minimum API 24)
- **Backend**: Firebase
  - **Authentication**: Email/Password based secure login.
  - **Realtime Database**: NoSQL cloud database for instant data synchronization.
- **Architecture**: Activity-based navigation with custom Adapters for efficient list rendering.
- **UI/UX**: Material Design components for a modern, intuitive user interface.

## 🚀 Getting Started

### Prerequisites
- Android Studio (Latest version recommended)
- A Firebase Account
- Android Device or Emulator (API 24+)

### Installation for Developers
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/abhireddy18/orbitly-campus-recruitment-system.git
   ```
2. **Open in Android Studio**:
   - Import the project as a Gradle project.
3. **Firebase Setup**:
   - Create a project at [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with package name: `com.example.crs2025`.
   - Download `google-services.json` and place it in the `app/` directory.
   - Enable **Email/Password Authentication**.
   - Create a **Realtime Database** and set rules to allow authenticated access.
4. **Build & Run**:
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

### Installation for Users
1. Download the latest release APK from the [GitHub Releases](https://github.com/abhireddy18/orbitly-campus-recruitment-system/releases) page.
2. Enable "Install from Unknown Sources" in your Android device settings.
3. Install the APK and launch the app.

## 🔑 Admin Access Configuration
Admin access is restricted and cannot be granted through the app registration. It requires a custom Firebase Auth claim.

**To grant admin access:**
Use the Firebase Admin SDK to set a custom claim for the specific user UID:
```javascript
admin.auth().setCustomUserClaims(uid, { admin: true });
```
Users with this claim will be automatically redirected to the `AdminDashboardActivity` upon login.

## 📂 Project Structure
```text
app/
├── src/
│   └── main/
│       ├── java/com/example/crs2025/
│       │   ├── activities/        # Entry points (Login, Register)
│       │   ├── adapters/          # RecyclerView adapters for lists
│       │   ├── admin/             # Admin-specific management activities
│       │   ├── company/           # Company-specific features
│       │   ├── student/           # Student-specific features
│       │   ├── dashboards/        # Role-based home screens
│       │   └── models/            # Data POJOs (User, Job, Interview, etc.)
│       └── res/                   # Layouts, Drawables, and Values
└── build.gradle.kts               # Build configuration
```

## 📜 License
This project is available for educational and research purposes. Please refer to the license file for more details.
