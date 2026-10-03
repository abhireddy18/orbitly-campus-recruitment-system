package com.example.crs2025.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.utils.GeminiAiHelper;
import com.example.crs2025.utils.ThreeDCardHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class GeminiAiAssistantActivity extends AppCompatActivity {

    private TextInputEditText etPrompt;
    private MaterialButton btnGenerate, btnCopy;
    private MaterialButton chipResume, chipInterview, chipCoverLetter, chipApiKey;
    private ProgressBar pbLoading;
    private MaterialCardView cardResult, cardAiHero;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_ai_assistant);

        initViews();
        setupListeners();

        // Apply interactive 3D Touch Tilt animation to AI Hero card
        ThreeDCardHelper.enable3DTouchTilt(cardAiHero);
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        etPrompt = findViewById(R.id.et_ai_prompt);
        btnGenerate = findViewById(R.id.btn_generate_ai);
        btnCopy = findViewById(R.id.btn_copy_response);

        chipResume = findViewById(R.id.chip_resume);
        chipInterview = findViewById(R.id.chip_interview);
        chipCoverLetter = findViewById(R.id.chip_cover_letter);
        chipApiKey = findViewById(R.id.chip_api_key);

        pbLoading = findViewById(R.id.pb_ai_loading);
        cardResult = findViewById(R.id.card_ai_result);
        cardAiHero = findViewById(R.id.card_ai_hero);
        tvResult = findViewById(R.id.tv_ai_result);
    }

    private void setupListeners() {
        chipResume.setOnClickListener(v -> {
            etPrompt.setText("Analyze my resume for campus recruitment and ATS optimization:\n- Target Role: Software Engineer\n- Skills: Java, Android, Firebase, Data Structures, SQL\n- Experience: 2 Academic Projects");
        });

        chipInterview.setOnClickListener(v -> {
            etPrompt.setText("Generate 3 technical interview questions and sample answers for a Software Engineering role at a top campus hiring company.");
        });

        chipCoverLetter.setOnClickListener(v -> {
            etPrompt.setText("Write a professional cover letter for applying to campus placement roles for Software Development Engineer.");
        });

        chipApiKey.setOnClickListener(v -> showApiKeyDialog());

        btnGenerate.setOnClickListener(v -> generateAiContent());

        btnCopy.setOnClickListener(v -> {
            String text = tvResult.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Gemini Response", text);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied response to clipboard!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateAiContent() {
        String prompt = etPrompt.getText() != null ? etPrompt.getText().toString().trim() : "";
        if (TextUtils.isEmpty(prompt)) {
            Toast.makeText(this, "Please enter a prompt or select a template option above.", Toast.LENGTH_SHORT).show();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        cardResult.setVisibility(View.GONE);

        GeminiAiHelper.generateContent(prompt, new GeminiAiHelper.GeminiCallback() {
            @Override
            public void onSuccess(String aiResponse) {
                pbLoading.setVisibility(View.GONE);
                tvResult.setText(aiResponse);
                cardResult.setVisibility(View.VISIBLE);

                // Perform 3D Flip animation on result card when content arrives
                ThreeDCardHelper.perform3DFlip(cardResult, null);
            }

            @Override
            public void onError(String errorMessage) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(GeminiAiAssistantActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showApiKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configure Gemini API Key");
        builder.setMessage("Enter your Google Gemini API Key below for live AI responses (or leave empty to use built-in local AI engine):");

        final EditText inputKey = new EditText(this);
        inputKey.setHint("AIzaSy...");
        inputKey.setText(GeminiAiHelper.getApiKey());
        builder.setView(inputKey);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String key = inputKey.getText().toString();
            GeminiAiHelper.setApiKey(key);
            if (GeminiAiHelper.hasApiKey()) {
                Toast.makeText(this, "Gemini API Key saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Using built-in AI reasoning engine.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
