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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.activities.LoginActivity;
import com.example.crs2025.adapters.CareerJobAdapter;
import com.example.crs2025.models.Job;
import com.example.crs2025.utils.SavedJobsStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchJobActivity extends AppCompatActivity {

    private EditText etSearch;
    private ListView lvJobs;
    private Button btnApplyJob, btnGoBack, btnSavedJobs;
    private TextView tvNoJobs;
    private DatabaseReference jobsRef;
    private final List<Job> jobList = new ArrayList<>();
    private CareerJobAdapter jobAdapter;
    private SavedJobsStore savedJobsStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_job);

        etSearch = findViewById(R.id.et_search);
        lvJobs = findViewById(R.id.lv_jobs);
        tvNoJobs = findViewById(R.id.tv_no_jobs);
        btnApplyJob = findViewById(R.id.btn_apply_job);
        btnGoBack = findViewById(R.id.btn_go_back);
        btnSavedJobs = findViewById(R.id.btn_saved_jobs);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        jobsRef = FirebaseDatabase.getInstance().getReference("globalJobs");
        savedJobsStore = new SavedJobsStore(
            this,
            FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
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
        btnSavedJobs.setOnClickListener(v ->
            startActivity(new Intent(SearchJobActivity.this, SavedJobsActivity.class)));
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void fetchJobs() {
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnap : snapshot.getChildren()) {
                    Job job = jobSnap.getValue(Job.class);
                    if (job != null && jobSnap.getKey() != null) {
                        job.setJobId(jobSnap.getKey());
                        jobList.add(job);
                    }
                }
                filterJobs(etSearch.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                jobList.clear();
                filterJobs("");
            }
        });
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