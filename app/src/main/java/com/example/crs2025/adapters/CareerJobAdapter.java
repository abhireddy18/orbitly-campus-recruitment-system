package com.example.crs2025.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.crs2025.R;
import com.example.crs2025.models.Job;
import com.example.crs2025.utils.SavedJobsStore;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CareerJobAdapter extends BaseAdapter {
    private final Context context;
    private final SavedJobsStore savedJobsStore;
    private final Runnable onSavedJobsChanged;
    private final List<Job> jobs = new ArrayList<>();

    public CareerJobAdapter(Context context, List<Job> jobs, SavedJobsStore savedJobsStore,
                            Runnable onSavedJobsChanged) {
        this.context = context;
        this.savedJobsStore = savedJobsStore;
        this.onSavedJobsChanged = onSavedJobsChanged;
        replaceJobs(jobs);
    }

    public void replaceJobs(List<Job> newJobs) {
        jobs.clear();
        jobs.addAll(newJobs);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return jobs.size();
    }

    @Override
    public Job getItem(int position) {
        return jobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.item_career_job, parent, false);
        }

        Job job = getItem(position);
        TextView title = row.findViewById(R.id.tv_career_job_title);
        TextView company = row.findViewById(R.id.tv_career_company_name);
        TextView details = row.findViewById(R.id.tv_career_job_details);
        MaterialButton saveButton = row.findViewById(R.id.btn_save_job);

        title.setText(job.getJobTitle());
        company.setText(job.getCompanyName());

        String jobDetails = job.getJobType() == null ? "" : job.getJobType();
        if (job.getSkills() != null && !job.getSkills().trim().isEmpty()) {
            jobDetails = jobDetails.isEmpty() ? job.getSkills() : jobDetails + "  |  " + job.getSkills();
        }
        if (job.getCgpa() != null && !job.getCgpa().trim().isEmpty()) {
            jobDetails = jobDetails.isEmpty() ? "Min CGPA " + job.getCgpa()
                    : jobDetails + "  |  Min CGPA " + job.getCgpa();
        }
        details.setText(jobDetails.isEmpty() ? "Explore this opportunity" : jobDetails);

        boolean saved = savedJobsStore.isSaved(job);
        saveButton.setText(saved ? "Saved" : "Save job");
        saveButton.setOnClickListener(view -> {
            savedJobsStore.toggle(job);
            notifyDataSetChanged();
            if (onSavedJobsChanged != null) {
                onSavedJobsChanged.run();
            }
        });

        return row;
    }
}
