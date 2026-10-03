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
import com.example.crs2025.models.User;
import com.example.crs2025.utils.CampusDatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etAddress, etEnrollment, etBranch, etInstitute, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private Button btnRegister;
    private String selectedRole = "Student"; // Default role

    private CampusDatabaseHelper dbHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = CampusDatabaseHelper.getInstance(this);
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception ignored) {}

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etEnrollment = findViewById(R.id.et_enrollment);
        etBranch = findViewById(R.id.et_branch);
        etInstitute = findViewById(R.id.et_institute);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        spinnerRole = findViewById(R.id.spinner_role);
        btnRegister = findViewById(R.id.btn_register);

        // Set up spinner with roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.register_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        toggleFieldsVisibility();

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
                toggleFieldsVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void toggleFieldsVisibility() {
        if ("Student".equals(selectedRole)) {
            etEnrollment.setVisibility(View.VISIBLE);
            etBranch.setVisibility(View.VISIBLE);
            etInstitute.setVisibility(View.VISIBLE);
        } else {
            etEnrollment.setVisibility(View.GONE);
            etBranch.setVisibility(View.GONE);
            etInstitute.setVisibility(View.GONE);
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
        User user;

        if ("Student".equals(selectedRole)) {
            String enrollment = etEnrollment.getText().toString().trim();
            String branch = etBranch.getText().toString().trim();
            String institute = etInstitute.getText().toString().trim();

            if (TextUtils.isEmpty(enrollment) || TextUtils.isEmpty(branch) || TextUtils.isEmpty(institute)) {
                Toast.makeText(this, "Please fill all student fields", Toast.LENGTH_SHORT).show();
                return;
            }

            user = new User(userId, name, email, address, "Student", enrollment, branch, institute);
        } else {
            user = new User(userId, name, email, address, "Company");
        }

        // Register in Android Studio Local Database
        boolean localSuccess = dbHelper.registerUser(user, password);

        // Optional Firebase Auth Registration
        if (mAuth != null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                user.setId(firebaseUser.getUid());
                            }
                        }
                    });
        }

        if (localSuccess) {
            Toast.makeText(this, "Registration Successful! Log in to continue.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed or email already registered.", Toast.LENGTH_SHORT).show();
        }
    }
}
