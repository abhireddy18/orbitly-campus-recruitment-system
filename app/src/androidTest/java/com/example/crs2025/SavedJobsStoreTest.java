package com.example.crs2025;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.crs2025.models.Job;
import com.example.crs2025.utils.SavedJobsStore;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SavedJobsStoreTest {
    @Test
    public void bookmarksPersistPerUserAndCanBeRemoved() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String userId = "saved-jobs-instrumented-test";
        context.getSharedPreferences("saved_jobs_" + userId, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();

        Job job = new Job(
                "job-1",
                "company-1",
                "Northstar Labs",
                "Android Engineer",
                "Java, Firebase",
                "3.2",
                "Internship",
                false
        );

        try {
            SavedJobsStore firstStore = new SavedJobsStore(context, userId);
            firstStore.toggle(job);

            assertTrue(firstStore.isSaved(job));

            SavedJobsStore reopenedStore = new SavedJobsStore(context, userId);
            assertEquals(1, reopenedStore.getSavedJobs().size());
            assertEquals("Android Engineer", reopenedStore.getSavedJobs().get(0).getJobTitle());
            assertFalse(new SavedJobsStore(context, userId + "-other").isSaved(job));

            reopenedStore.toggle(job);
            assertFalse(reopenedStore.isSaved(job));
            assertTrue(reopenedStore.getSavedJobs().isEmpty());
        } finally {
            context.getSharedPreferences("saved_jobs_" + userId, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit();
        }
    }
}
