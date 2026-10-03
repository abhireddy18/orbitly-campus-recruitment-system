package com.example.crs2025.student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.adapters.CareerJobAdapter;
import com.example.crs2025.models.Job;
import com.example.crs2025.utils.CampusDatabaseHelper;
import com.example.crs2025.utils.SavedJobsStore;
import com.example.crs2025.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchJobActivity extends AppCompatActivity {

    private EditText etSearch;
    private ListView lvJobs;
    private Button btnApplyJob, btnGoBack, btnSavedJobs;
    private TextView tvNoJobs;
    private final List<Job> jobList = new ArrayList<>();
    private CareerJobAdapter jobAdapter;
    private SavedJobsStore savedJobsStore;
    private CampusDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_job);

        dbHelper = CampusDatabaseHelper.getInstance(this);
        SessionManager sessionManager = new SessionManager(this);

        etSearch = findViewById(R.id.et_search);
        lvJobs = findViewById(R.id.lv_jobs);
        tvNoJobs = findViewById(R.id.tv_no_jobs);
        btnApplyJob = findViewById(R.id.btn_apply_job);
        btnGoBack = findViewById(R.id.btn_go_back);
        btnSavedJobs = findViewById(R.id.btn_saved_jobs);

        String userId = sessionManager.getUserId().isEmpty() ? "student_001" : sessionManager.getUserId();
        savedJobsStore = new SavedJobsStore(this, userId);
        jobAdapter = new CareerJobAdapter(this, jobList, savedJobsStore, null);
        lvJobs.setAdapter(jobAdapter);

        fetchJobs();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJobs(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnApplyJob.setOnClickListener(v -> startActivity(new Intent(SearchJobActivity.this, ApplyJobActivity.class)));
        btnSavedJobs.setOnClickListener(v -> startActivity(new Intent(SearchJobActivity.this, SavedJobsActivity.class)));
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void fetchJobs() {
        jobList.clear();
        List<Job> localJobs = dbHelper.getAllJobs();
        if (localJobs != null && !localJobs.isEmpty()) {
            jobList.addAll(localJobs);
        }
        filterJobs(etSearch.getText() != null ? etSearch.getText().toString() : "");
    }

    private void filterJobs(String query) {
        String normalizedQuery = query.trim().toLowerCase(Locale.getDefault());
        List<Job> filteredJobs = new ArrayList<>();

        for (Job job : jobList) {
            String searchableText = (job.getJobTitle() + " " + job.getCompanyName() + " "
                    + job.getSkills()).toLowerCase(Locale.getDefault());
            if (normalizedQuery.isEmpty() || searchableText.contains(normalizedQuery)) {
                filteredJobs.add(job);
            }
        }

        jobAdapter.replaceJobs(filteredJobs);
        boolean hasJobs = !filteredJobs.isEmpty();
        lvJobs.setVisibility(hasJobs ? View.VISIBLE : View.GONE);
        tvNoJobs.setVisibility(hasJobs ? View.GONE : View.VISIBLE);
    }
}
