package com.example.crs2025.company;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Job;
import com.example.crs2025.utils.CampusDatabaseHelper;
import com.example.crs2025.utils.SessionManager;

import java.util.UUID;

public class PostJobActivity extends AppCompatActivity {

    private EditText etJobTitle, etSkills, etCgpa, etJobType;
    private CheckBox cbInternship;
    private Button btnPostJob, btnGoBack;

    private CampusDatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        dbHelper = CampusDatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        etJobTitle = findViewById(R.id.et_job_title);
        etSkills = findViewById(R.id.et_skills);
        etCgpa = findViewById(R.id.et_cgpa);
        etJobType = findViewById(R.id.et_job_type);
        cbInternship = findViewById(R.id.cb_internship);
        btnPostJob = findViewById(R.id.btn_post_job);
        btnGoBack = findViewById(R.id.btn_go_back);

        btnPostJob.setOnClickListener(v -> postJob());
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void postJob() {
        String companyId = sessionManager.getUserId().isEmpty() ? "company_001" : sessionManager.getUserId();
        String companyName = sessionManager.getUserName().isEmpty() ? "Google Inc" : sessionManager.getUserName();

        String jobTitle = etJobTitle.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();
        String cgpa = etCgpa.getText().toString().trim();
        String jobType = etJobType.getText().toString().trim();
        boolean isInternship = cbInternship.isChecked();

        if (TextUtils.isEmpty(jobTitle) || TextUtils.isEmpty(skills) ||
                TextUtils.isEmpty(cgpa) || TextUtils.isEmpty(jobType)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String jobId = "job_" + UUID.randomUUID().toString().substring(0, 8);
        Job job = new Job(jobId, companyId, companyName, jobTitle, skills, cgpa, jobType, isInternship);

        boolean success = dbHelper.addJob(job);
        if (success) {
            Toast.makeText(this, "Job Posted Successfully in Local Database!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to post job", Toast.LENGTH_SHORT).show();
        }
    }
}
