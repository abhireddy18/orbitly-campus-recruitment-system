package com.example.crs2025.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.dashboards.AdminDashboardActivity;
import com.example.crs2025.dashboards.CompanyDashboardActivity;
import com.example.crs2025.dashboards.StudentDashboardActivity;
import com.example.crs2025.models.User;
import com.example.crs2025.utils.CampusDatabaseHelper;
import com.example.crs2025.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Spinner spinnerRole;
    private Button btnLogin;
    private String selectedRole = "Student"; // Default role

    private CampusDatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = CampusDatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception ignored) {}

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        spinnerRole = findViewById(R.id.spinner_role);
        btnLogin = findViewById(R.id.btn_login);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.login_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Check Local Android Studio Database first
        User user = dbHelper.loginUser(email, password, selectedRole);
        if (user != null) {
            sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail(), selectedRole);
            Toast.makeText(this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
            navigateToDashboard(selectedRole);
            return;
        }

        // 2. Fallback to Firebase Auth if available
        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            sessionManager.createLoginSession(
                                    mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "user_101",
                                    email.split("@")[0],
                                    email,
                                    selectedRole
                            );
                            navigateToDashboard(selectedRole);
                        } else {
                            Toast.makeText(this, "Login failed. Check your credentials or select correct role.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Invalid credentials for role: " + selectedRole, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard(String role) {
        if ("Admin".equalsIgnoreCase(role)) {
            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
        } else if ("Company".equalsIgnoreCase(role)) {
            startActivity(new Intent(LoginActivity.this, CompanyDashboardActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
        }
        finish();
    }
}
