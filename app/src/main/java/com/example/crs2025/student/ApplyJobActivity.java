package com.example.crs2025.student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Application;
import com.example.crs2025.models.Job;
import com.example.crs2025.utils.CampusDatabaseHelper;
import com.example.crs2025.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApplyJobActivity extends AppCompatActivity {

    private Spinner spJobTitle, spCompany;
    private TextView tvSkills;
    private EditText etFullName, etEmail, etAddress, etBranch, etCgpa, etReason, etResume;
    private Button btnApply, btnGoBack;

    private CampusDatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private String selectedJobTitle, selectedCompanyId, selectedCompanyName, selectedSkills, selectedJobCgpa, selectedJobId;
    private final Map<String, List<Job>> jobMap = new HashMap<>();
    private final Map<String, Job> companyJobMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        dbHelper = CampusDatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        spJobTitle = findViewById(R.id.sp_job_title);
        spCompany = findViewById(R.id.sp_company);
        tvSkills = findViewById(R.id.tv_skills);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etBranch = findViewById(R.id.et_branch);
        etCgpa = findViewById(R.id.et_cgpa);
        etReason = findViewById(R.id.et_reason);
        etResume = findViewById(R.id.et_resume);
        btnApply = findViewById(R.id.btn_apply);
        btnGoBack = findViewById(R.id.btn_go_back);

        // Pre-fill student info if logged in
        etFullName.setText(sessionManager.getUserName());
        etEmail.setText(sessionManager.getUserEmail());

        loadJobTitles();

        spJobTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedJobTitle = parent.getItemAtPosition(position).toString();
                loadCompaniesForJob(selectedJobTitle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCompanyName = parent.getItemAtPosition(position).toString();
                Job jobDetails = companyJobMap.get(selectedCompanyName);
                if (jobDetails != null) {
                    selectedJobId = jobDetails.getJobId();
                    selectedCompanyId = jobDetails.getCompanyId();
                    selectedSkills = jobDetails.getSkills();
                    selectedJobCgpa = jobDetails.getCgpa();
                    tvSkills.setText(selectedSkills);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnApply.setOnClickListener(v -> applyForJob());
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void loadJobTitles() {
        List<Job> allJobs = dbHelper.getAllJobs();
        List<String> jobTitles = new ArrayList<>();
        jobMap.clear();

        if (allJobs != null) {
            for (Job job : allJobs) {
                String title = job.getJobTitle();
                if (title != null) {
                    jobMap.putIfAbsent(title, new ArrayList<>());
                    jobMap.get(title).add(job);
                    if (!jobTitles.contains(title)) {
                        jobTitles.add(title);
                    }
                }
            }
        }

        if (jobTitles.isEmpty()) {
            jobTitles.add("No Jobs Posted Yet");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJobTitle.setAdapter(adapter);
    }

    private void loadCompaniesForJob(String jobTitle) {
        List<Job> companyList = jobMap.get(jobTitle);
        List<String> companyNames = new ArrayList<>();
        companyJobMap.clear();

        if (companyList != null) {
            for (Job job : companyList) {
                String cName = job.getCompanyName() != null ? job.getCompanyName() : "Company";
                companyNames.add(cName);
                companyJobMap.put(cName, job);
            }
        }

        if (companyNames.isEmpty()) {
            companyNames.add("No Company");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, companyNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCompany.setAdapter(adapter);
    }

    private void applyForJob() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String branch = etBranch.getText().toString().trim();
        String cgpa = etCgpa.getText().toString().trim();
        String reason = etReason.getText().toString().trim();
        String resume = etResume.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(cgpa)) {
            Toast.makeText(this, "Please fill required fields (Name, Email, CGPA)", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (selectedJobCgpa != null && !selectedJobCgpa.isEmpty() && Double.parseDouble(cgpa) < Double.parseDouble(selectedJobCgpa)) {
                Toast.makeText(this, "You do not meet the CGPA requirement (" + selectedJobCgpa + ").", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException ignored) {}

        String studentId = sessionManager.getUserId().isEmpty() ? "student_001" : sessionManager.getUserId();
        String appId = "app_" + UUID.randomUUID().toString().substring(0, 8);

        Application application = new Application(
                appId, studentId, selectedJobId != null ? selectedJobId : "job_001",
                selectedCompanyId != null ? selectedCompanyId : "company_001",
                selectedJobTitle != null ? selectedJobTitle : "Job Role",
                selectedCompanyName != null ? selectedCompanyName : "Company",
                selectedSkills != null ? selectedSkills : "Skills",
                fullName, email, address, branch, cgpa, reason, resume, "Pending"
        );

        boolean success = dbHelper.addApplication(application);
        if (success) {
            Toast.makeText(this, "Application Submitted Successfully in Local Database!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to submit application.", Toast.LENGTH_SHORT).show();
        }
    }
}
