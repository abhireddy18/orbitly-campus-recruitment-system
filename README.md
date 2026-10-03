# 🎓 Campus App - Campus Recruitment System

**Campus App** is a next-generation Android application designed to streamline campus recruitment. Built with an **Android Studio Native SQLite Local Database**, **Gemini 1.5 Flash AI** integration, and **Interactive 3D Card Animations**, it seamlessly bridges students, top hiring companies, and university administrators.

---

## 📲 Direct App Download

Anyone can download and install the app directly on an Android device (API 24+):

👉 **[Download CampusApp.apk](./CampusApp.apk)**

---

## 💾 Built-in Android Studio Local SQLite Database

- **100% Zero-Configuration Local Database**: Powered by Android's native `SQLiteOpenHelper` (`CampusDatabaseHelper.java`). No external cloud configuration or API credentials required!
- **Instant Pre-Populated Accounts for Immediate Testing**:
  - **Admin Login**: `admin@campus.com` | Password: `admin`
  - **Student Login**: `abhishekgb644@gmail.com` | Password: `password123`
  - **Company Login**: `recruitment@google.com` | Password: `password123`
- **Offline Reliability**: Data is saved locally on device in SQLite DB tables (`users`, `jobs`, `applications`, `interviews`, `feedbacks`).

---

## 🌟 Key Features & AI Innovations

### 🤖 Powered by Gemini AI (`Gemini 1.5 Flash`)
- **📄 AI Resume Analyzer & ATS Score Optimizer**: Scans student skills and project summaries, delivering instant ATS match scores, identified strengths, and actionable improvement tips.
- **🎯 AI Mock Interview Practice**: Generates high-frequency technical interview questions and model answer strategies tailored to specific job roles and companies.
- **✉️ AI Cover Letter Generator**: Creates personalized, professional application letters for campus placement roles.
- **💡 AI Career Coach**: Interactive guidance for campus job preparation, interviews, and application strategy.

### ✨ Interactive 3D Visuals & Animations
- **3D Touch Tilt Perspective**: Touch and drag hero cards to tilt them in 3D spatial coordinate space with real-time perspective depth (`ThreeDCardHelper`).
- **3D Card Flip Transitions**: Smooth 180-degree 3D axis flip animations when generating AI insights or flipping content cards.
- **3D Floating Breathing Animation**: Dynamic subtle floating motion for hero dashboard elements.

### 🧑‍🎓 For Students
- **Job Search & Discovery**: Filter and search live job openings posted by top recruiters.
- **1-Click Application & Tracking**: Apply with built-in profiles and track application progress in real-time.
- **Interactive Resume Links**: Tapping resume links directly launches candidate resumes (Google Drive, PDFs, or web links) in browser/viewers.
- **Interview Tracker**: Keep track of scheduled interview dates, times, venues, and types.

### 🏢 For Employers (Companies)
- **Job Posting**: Post and manage job openings with skill requirements and criteria.
- **Application Review**: Review student applications, inspect profiles, and approve/reject candidates.
- **Interview Scheduler**: Schedule interview slots, venues, and notify shortlisted candidates.

### 🛡️ For University Administrators
- **Comprehensive Management**: Oversee student directory, registered companies, job postings, and active interview workflows.

---

## 🛠️ Technical Stack

- **Database Engine**: Built-in Android Studio Native SQLite (`CampusDatabaseHelper.java`) & Session Manager (`SessionManager.java`)
- **Language**: Java & Modern Android Architecture
- **AI Integration**: Google Generative AI API (`gemini-1.5-flash`) via OkHttp with intelligent local fallback engine
- **Animations**: Custom 3D Matrix & Camera perspective transformations (`ThreeDCardHelper`)
- **Platform**: Android SDK (Min API 24, Target API 35)
- **UI/UX**: Material Design 3, Custom Vector Drawables, Adaptive Icons

---

## 🚀 Installation & Setup

### For Users
1. Download [CampusApp.apk](./CampusApp.apk).
2. Enable "Install from Unknown Sources" on your device if prompted.
3. Tap the APK file to install and launch **Campus App**.

### For Developers
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/abhireddy18/orbitly-campus-recruitment-system.git
   ```
2. **Open in Android Studio**:
   - Open as Gradle Project.
3. **Build & Run**:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📜 License
Available for educational and deployment purposes.
