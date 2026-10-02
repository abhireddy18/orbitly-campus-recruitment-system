package com.example.crs2025.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.crs2025.models.Job;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SavedJobsStore {
    private final SharedPreferences preferences;

    public SavedJobsStore(Context context, String userId) {
        preferences = context.getApplicationContext().getSharedPreferences(
                "saved_jobs_" + userId,
                Context.MODE_PRIVATE
        );
    }

    public List<Job> getSavedJobs() {
        List<Job> jobs = new ArrayList<>();
        Map<String, ?> savedEntries = preferences.getAll();

        for (Object value : savedEntries.values()) {
            if (!(value instanceof String)) {
                continue;
            }

            try {
                JSONObject savedJob = new JSONObject((String) value);
                String jobId = savedJob.optString("jobId", "");
                if (jobId.isEmpty()) {
                    continue;
                }

                jobs.add(new Job(
                        jobId,
                        savedJob.optString("companyId", ""),
                        savedJob.optString("companyName", ""),
                        savedJob.optString("jobTitle", ""),
                        savedJob.optString("skills", ""),
                        savedJob.optString("cgpa", ""),
                        savedJob.optString("jobType", ""),
                        savedJob.optBoolean("internshipRequired", false)
                ));
            } catch (JSONException ignored) {
            }
        }

        Collections.sort(jobs, (first, second) ->
                first.getJobTitle().compareToIgnoreCase(second.getJobTitle()));
        return jobs;
    }

    public boolean isSaved(Job job) {
        return job != null && job.getJobId() != null && preferences.contains(job.getJobId());
    }

    public void toggle(Job job) {
        if (job == null || job.getJobId() == null || job.getJobId().isEmpty()) {
            return;
        }

        if (isSaved(job)) {
            preferences.edit().remove(job.getJobId()).apply();
            return;
        }

        JSONObject savedJob = new JSONObject();
        try {
            savedJob.put("jobId", job.getJobId());
            savedJob.put("companyId", job.getCompanyId());
            savedJob.put("companyName", job.getCompanyName());
            savedJob.put("jobTitle", job.getJobTitle());
            savedJob.put("skills", job.getSkills());
            savedJob.put("cgpa", job.getCgpa());
            savedJob.put("jobType", job.getJobType());
            savedJob.put("internshipRequired", job.isInternshipRequired());
        } catch (JSONException ignored) {
            return;
        }

        preferences.edit().putString(job.getJobId(), savedJob.toString()).apply();
    }
}
