package com.example.crs2025.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.activities.LoginActivity;
import com.example.crs2025.adapters.CareerJobAdapter;
import com.example.crs2025.utils.SavedJobsStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SavedJobsActivity extends AppCompatActivity {
    private CareerJobAdapter adapter;
    private ListView savedJobsList;
    private TextView emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_jobs);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        savedJobsList = findViewById(R.id.lv_saved_jobs);
        emptyState = findViewById(R.id.tv_empty_saved_jobs);
        Button browseJobs = findViewById(R.id.btn_browse_jobs);

        SavedJobsStore savedJobsStore = new SavedJobsStore(this, user.getUid());
        adapter = new CareerJobAdapter(
                this,
                savedJobsStore.getSavedJobs(),
                savedJobsStore,
                this::updateEmptyState
        );
        savedJobsList.setAdapter(adapter);
        browseJobs.setOnClickListener(view ->
                startActivity(new Intent(this, SearchJobActivity.class)));
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean hasSavedJobs = adapter != null && adapter.getCount() > 0;
        savedJobsList.setVisibility(hasSavedJobs ? View.VISIBLE : View.GONE);
        emptyState.setVisibility(hasSavedJobs ? View.GONE : View.VISIBLE);
    }
}
