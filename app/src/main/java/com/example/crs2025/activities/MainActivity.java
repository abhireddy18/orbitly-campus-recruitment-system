package com.example.crs2025.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View hero = findViewById(R.id.main_hero);
        float density = getResources().getDisplayMetrics().density;
        hero.setCameraDistance(9000 * density);
        hero.setRotationY(-12f);
        hero.setRotationX(5f);
        hero.setAlpha(0f);
        hero.setTranslationY(24f);
        hero.animate()
            .rotationY(0f)
            .rotationX(0f)
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .start();

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);


        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }

}
