package com.example.crs2025.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.crs2025.models.Application;
import com.example.crs2025.models.Company;
import com.example.crs2025.models.Feedback;
import com.example.crs2025.models.Interview;
import com.example.crs2025.models.Job;
import com.example.crs2025.models.Student;
import com.example.crs2025.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Built-in Android Studio SQLite Local Database Manager for Campus Recruitment System.
 * Enables 100% offline & local database storage without requiring external cloud/Firebase dependencies.
 */
public class CampusDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_recruitment_studio.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_JOBS = "jobs";
    private static final String TABLE_APPLICATIONS = "applications";
    private static final String TABLE_INTERVIEWS = "interviews";
    private static final String TABLE_FEEDBACKS = "feedbacks";

    private static CampusDatabaseHelper instance;

    public static synchronized CampusDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CampusDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private CampusDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "user_id TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "address TEXT, " +
                "role TEXT, " +
                "enrollment TEXT, " +
                "branch TEXT, " +
                "institute TEXT)");

        // Jobs Table
        db.execSQL("CREATE TABLE " + TABLE_JOBS + " (" +
                "job_id TEXT PRIMARY KEY, " +
                "company_id TEXT, " +
                "company_name TEXT, " +
                "job_title TEXT, " +
                "skills TEXT, " +
                "cgpa TEXT, " +
                "job_type TEXT, " +
                "internship_required INTEGER)");

        // Applications Table
        db.execSQL("CREATE TABLE " + TABLE_APPLICATIONS + " (" +
                "application_id TEXT PRIMARY KEY, " +
                "student_id TEXT, " +
                "job_id TEXT, " +
                "company_id TEXT, " +
                "job_title TEXT, " +
                "company_name TEXT, " +
                "skills TEXT, " +
                "full_name TEXT, " +
                "email TEXT, " +
                "address TEXT, " +
                "branch TEXT, " +
                "cgpa TEXT, " +
                "reason_to_apply TEXT, " +
                "resume_link TEXT, " +
                "status TEXT)");

        // Interviews Table
        db.execSQL("CREATE TABLE " + TABLE_INTERVIEWS + " (" +
                "interview_id TEXT PRIMARY KEY, " +
                "company_id TEXT, " +
                "company_name TEXT, " +
                "job_title TEXT, " +
                "student_id TEXT, " +
                "student_name TEXT, " +
                "interview_date TEXT, " +
                "interview_time TEXT, " +
                "venue TEXT, " +
                "interview_type TEXT)");

        // Feedbacks Table
        db.execSQL("CREATE TABLE " + TABLE_FEEDBACKS + " (" +
                "feedback_id TEXT PRIMARY KEY, " +
                "user_id TEXT, " +
                "user_name TEXT, " +
                "user_email TEXT, " +
                "user_type TEXT, " +
                "satisfaction TEXT, " +
                "improvements TEXT, " +
                "comments TEXT, " +
                "recommend TEXT)");

        // Insert Default Accounts for instant out-of-the-box testing
        insertDefaultData(db);
    }

    private void insertDefaultData(SQLiteDatabase db) {
        // Admin Default
        ContentValues admin = new ContentValues();
        admin.put("user_id", "admin_001");
        admin.put("name", "System Administrator");
        admin.put("email", "admin@campus.com");
        admin.put("password", "admin");
        admin.put("address", "Campus HQ");
        admin.put("role", "Admin");
        db.insert(TABLE_USERS, null, admin);

        // Demo Student Default
        ContentValues student = new ContentValues();
        student.put("user_id", "student_001");
        student.put("name", "Abhishekgb");
        student.put("email", "abhishekgb644@gmail.com");
        student.put("password", "password123");
        student.put("address", "Campus Hostel 4");
        student.put("role", "Student");
        student.put("enrollment", "EN2025001");
        student.put("branch", "Computer Science");
        student.put("institute", "Institute of Technology");
        db.insert(TABLE_USERS, null, student);

        // Demo Company Default
        ContentValues company = new ContentValues();
        company.put("user_id", "company_001");
        company.put("name", "Google Inc");
        company.put("email", "recruitment@google.com");
        company.put("password", "password123");
        company.put("address", "Silicon Valley Campus");
        company.put("role", "Company");
        db.insert(TABLE_USERS, null, company);

        // Sample Job
        ContentValues job = new ContentValues();
        job.put("job_id", "job_001");
        job.put("company_id", "company_001");
        job.put("company_name", "Google Inc");
        job.put("job_title", "Software Engineering Intern");
        job.put("skills", "Java, Android, SQL, Data Structures");
        job.put("cgpa", "7.5");
        job.put("job_type", "Full-Time / Internship");
        job.put("internship_required", 1);
        db.insert(TABLE_JOBS, null, job);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACKS);
        onCreate(db);
    }

    // --- USER OPERATIONS ---
    public boolean registerUser(User user, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user.getId() != null ? user.getId() : UUID.randomUUID().toString());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", password);
        values.put("address", user.getAddress());
        values.put("role", user.getRole());
        values.put("enrollment", user.getEnrollmentNo());
        values.put("branch", user.getBranch());
        values.put("institute", user.getInstitute());

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public User loginUser(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "email=? AND password=? AND role=?",
                new String[]{email, password, role}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = parseUser(cursor);
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();

        // Special fallback for Admin login
        if ("Admin".equalsIgnoreCase(role) && ("admin@campus.com".equals(email) || "admin".equalsIgnoreCase(email)) && "admin".equals(password)) {
            return new User("admin_001", "System Administrator", "admin@campus.com", "Campus HQ", "Admin");
        }

        return null;
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "user_id=?", new String[]{userId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = parseUser(cursor);
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    private User parseUser(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
        String enrollment = cursor.getString(cursor.getColumnIndexOrThrow("enrollment"));
        String branch = cursor.getString(cursor.getColumnIndexOrThrow("branch"));
        String institute = cursor.getString(cursor.getColumnIndexOrThrow("institute"));
        return new User(id, name, email, address, role, enrollment, branch, institute);
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "role=?", new String[]{"Student"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Student s = new Student(
                        cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("address")),
                        cursor.getString(cursor.getColumnIndexOrThrow("branch")),
                        cursor.getString(cursor.getColumnIndexOrThrow("enrollment")),
                        cursor.getString(cursor.getColumnIndexOrThrow("institute")),
                        "Student"
                );
                list.add(s);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Company> getAllCompanies() {
        List<Company> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "role=?", new String[]{"Company"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Company c = new Company(
                        cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("address")),
                        "Company"
                );
                list.add(c);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void deleteStudent(String studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, "user_id=?", new String[]{studentId});
    }

    public void deleteCompany(String companyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, "user_id=?", new String[]{companyId});
    }

    // --- JOB OPERATIONS ---
    public boolean addJob(Job job) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("job_id", job.getJobId() != null ? job.getJobId() : UUID.randomUUID().toString());
        values.put("company_id", job.getCompanyId());
        values.put("company_name", job.getCompanyName());
        values.put("job_title", job.getJobTitle());
        values.put("skills", job.getSkills());
        values.put("cgpa", job.getCgpa());
        values.put("job_type", job.getJobType());
        values.put("internship_required", job.isInternshipRequired() ? 1 : 0);

        long result = db.insert(TABLE_JOBS, null, values);
        return result != -1;
    }

    public List<Job> getAllJobs() {
        List<Job> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_JOBS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(parseJob(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    private Job parseJob(Cursor cursor) {
        return new Job(
                cursor.getString(cursor.getColumnIndexOrThrow("job_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("company_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("company_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("job_title")),
                cursor.getString(cursor.getColumnIndexOrThrow("skills")),
                cursor.getString(cursor.getColumnIndexOrThrow("cgpa")),
                cursor.getString(cursor.getColumnIndexOrThrow("job_type")),
                cursor.getInt(cursor.getColumnIndexOrThrow("internship_required")) == 1
        );
    }

    public void deleteJob(String jobId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_JOBS, "job_id=?", new String[]{jobId});
    }

    // --- APPLICATION OPERATIONS ---
    public boolean addApplication(Application app) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("application_id", app.getApplicationId() != null ? app.getApplicationId() : UUID.randomUUID().toString());
        values.put("student_id", app.getStudentId());
        values.put("job_id", app.getJobId());
        values.put("company_id", app.getCompanyId());
        values.put("job_title", app.getJobTitle());
        values.put("company_name", app.getCompanyName());
        values.put("skills", app.getSkills());
        values.put("full_name", app.getFullName());
        values.put("email", app.getEmail());
        values.put("address", app.getAddress());
        values.put("branch", app.getBranch());
        values.put("cgpa", app.getCgpa());
        values.put("reason_to_apply", app.getReasonToApply());
        values.put("resume_link", app.getResumeLink());
        values.put("status", app.getStatus());

        long result = db.insert(TABLE_APPLICATIONS, null, values);
        return result != -1;
    }

    public List<Application> getAllApplications() {
        List<Application> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPLICATIONS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(parseApplication(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Application> getApplicationsForCompany(String companyId) {
        List<Application> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPLICATIONS, null, "company_id=?", new String[]{companyId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(parseApplication(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Application> getApplicationsForStudent(String studentId) {
        List<Application> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPLICATIONS, null, "student_id=?", new String[]{studentId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(parseApplication(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Application getApplicationById(String appId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPLICATIONS, null, "application_id=?", new String[]{appId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Application app = parseApplication(cursor);
            cursor.close();
            return app;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void updateApplicationStatus(String appId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update(TABLE_APPLICATIONS, values, "application_id=?", new String[]{appId});
    }

    public void deleteApplication(String appId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPLICATIONS, "application_id=?", new String[]{appId});
    }

    private Application parseApplication(Cursor cursor) {
        return new Application(
                cursor.getString(cursor.getColumnIndexOrThrow("application_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("job_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("company_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("job_title")),
                cursor.getString(cursor.getColumnIndexOrThrow("company_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("skills")),
                cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("address")),
                cursor.getString(cursor.getColumnIndexOrThrow("branch")),
                cursor.getString(cursor.getColumnIndexOrThrow("cgpa")),
                cursor.getString(cursor.getColumnIndexOrThrow("reason_to_apply")),
                cursor.getString(cursor.getColumnIndexOrThrow("resume_link")),
                cursor.getString(cursor.getColumnIndexOrThrow("status"))
        );
    }

    // --- INTERVIEW OPERATIONS ---
    public boolean addInterview(Interview interview) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("interview_id", interview.getInterviewId() != null ? interview.getInterviewId() : UUID.randomUUID().toString());
        values.put("company_id", interview.getCompanyId());
        values.put("company_name", interview.getCompanyName());
        values.put("job_title", interview.getJobTitle());
        values.put("student_id", interview.getStudentId());
        values.put("student_name", interview.getStudentName());
        values.put("interview_date", interview.getDate());
        values.put("interview_time", interview.getTime());
        values.put("venue", interview.getVenue());
        values.put("interview_type", interview.getInterviewType());

        long result = db.insert(TABLE_INTERVIEWS, null, values);
        return result != -1;
    }

    public List<Interview> getAllInterviews() {
        List<Interview> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INTERVIEWS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(parseInterview(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Interview> getInterviewsForCompany(String companyId) {
        List<Interview> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INTERVIEWS, null, "company_id=?", new String[]{companyId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(parseInterview(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Interview> getInterviewsForStudent(String studentId) {
        List<Interview> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INTERVIEWS, null, "student_id=?", new String[]{studentId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(parseInterview(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Interview getInterviewById(String interviewId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INTERVIEWS, null, "interview_id=?", new String[]{interviewId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Interview i = parseInterview(cursor);
            cursor.close();
            return i;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void deleteInterview(String interviewId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INTERVIEWS, "interview_id=?", new String[]{interviewId});
    }

    private Interview parseInterview(Cursor cursor) {
        return new Interview(
                cursor.getString(cursor.getColumnIndexOrThrow("interview_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("company_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("company_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("job_title")),
                cursor.getString(cursor.getColumnIndexOrThrow("student_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("student_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("interview_date")),
                cursor.getString(cursor.getColumnIndexOrThrow("interview_time")),
                cursor.getString(cursor.getColumnIndexOrThrow("venue")),
                cursor.getString(cursor.getColumnIndexOrThrow("interview_type"))
        );
    }

    // --- FEEDBACK OPERATIONS ---
    public boolean addFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("feedback_id", feedback.getFeedbackId() != null ? feedback.getFeedbackId() : UUID.randomUUID().toString());
        values.put("user_id", feedback.getUserId());
        values.put("user_name", feedback.getName());
        values.put("user_email", feedback.getEmail());
        values.put("user_type", feedback.getSatisfaction() != null ? feedback.getSatisfaction() : "General");
        values.put("satisfaction", feedback.getSatisfaction());
        values.put("improvements", feedback.getImprovementSuggestions());
        values.put("comments", feedback.getAdditionalComments());
        values.put("recommend", feedback.getRecommendation());

        long result = db.insert(TABLE_FEEDBACKS, null, values);
        return result != -1;
    }

    public List<Feedback> getAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FEEDBACKS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Feedback f = new Feedback(
                        cursor.getString(cursor.getColumnIndexOrThrow("feedback_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("satisfaction")),
                        cursor.getString(cursor.getColumnIndexOrThrow("improvements")),
                        cursor.getString(cursor.getColumnIndexOrThrow("comments")),
                        cursor.getString(cursor.getColumnIndexOrThrow("recommend"))
                );
                list.add(f);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void deleteFeedback(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEEDBACKS, "user_id=?", new String[]{userId});
    }
}
