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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Spinner spinnerRole;
    private Button btnLogin;
    private String selectedRole = "Student"; // Default role

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        spinnerRole = findViewById(R.id.spinner_role);
        btnLogin = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            return;
                        }

                        if ("Admin".equals(selectedRole)) {
                            user.getIdToken(true).addOnCompleteListener(this, tokenTask -> {
                                boolean isAdmin = tokenTask.isSuccessful()
                                        && Boolean.TRUE.equals(tokenTask.getResult().getClaims().get("admin"));
                                if (isAdmin) {
                                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                    finish();
                                } else {
                                    mAuth.signOut();
                                    Toast.makeText(this, "This account is not authorized for admin access.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        if ("Student".equals(selectedRole)) {
                            startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                        } else if ("Company".equals(selectedRole)) {
                            startActivity(new Intent(LoginActivity.this, CompanyDashboardActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
