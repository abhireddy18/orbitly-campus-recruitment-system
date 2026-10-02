package com.example.crs2025;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import com.example.crs2025.activities.LoginActivity;
import com.example.crs2025.utils.ElapsedTimeIdlingResource;

public class LoginUITest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    private IdlingResource idlingResource;

    @Before
    public void setUp() {
        // Increase wait time to ensure UI is fully loaded before interactions
        idlingResource = new ElapsedTimeIdlingResource(3000); // Increased to 3 seconds
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void hardcodedAdminCredentialsCannotOpenAdminDashboard() {
        Espresso.onView(ViewMatchers.withId(R.id.et_email))
            .perform(ViewActions.typeText("prototype-admin@example.invalid"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.et_password))
            .perform(ViewActions.typeText("not-a-real-password"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
