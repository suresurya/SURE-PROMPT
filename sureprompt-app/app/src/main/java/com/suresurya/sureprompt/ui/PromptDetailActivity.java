package com.suresurya.sureprompt.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.suresurya.sureprompt.R;
import com.suresurya.sureprompt.models.PromptDetailDto;
import com.suresurya.sureprompt.models.PromptVersionDto;
import com.suresurya.sureprompt.network.ApiService;
import com.suresurya.sureprompt.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromptDetailActivity extends AppCompatActivity {

    private Long promptId;
    private ApiService apiService;
    private Handler statusHandler = new Handler(Looper.getMainLooper());
    private Runnable statusRunnable;
    
    private TextView tvTitle, tvAuthor, tvAiStatus, tvAiReason, tvScore, tvPromptBody, tvAiOutput;
    private MaterialCardView cardScore;
    private Spinner versionSpinner;
    private MaterialButton btnTryLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_detail);

        promptId = getIntent().getLongExtra("prompt_id", -1L);
        if (promptId == -1L) {
            finish();
            return;
        }

        initViews();
        apiService = RetrofitClient.getClient().create(ApiService.class);
        fetchDetails();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvAuthor = findViewById(R.id.tvDetailAuthor);
        tvAiStatus = findViewById(R.id.tvAiStatus);
        tvAiReason = findViewById(R.id.tvAiReason);
        tvScore = findViewById(R.id.tvDetailScore);
        tvPromptBody = findViewById(R.id.tvPromptBody);
        tvAiOutput = findViewById(R.id.tvAiOutput);
        cardScore = findViewById(R.id.cardDetailScore);
        versionSpinner = findViewById(R.id.versionSpinner);
        btnTryLive = findViewById(R.id.btnTryLive);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        btnTryLive.setOnClickListener(v -> {
            String prompt = tvPromptBody.getText().toString();
            LiveTryBottomSheet.newInstance(prompt).show(getSupportFragmentManager(), "LiveTry");
        });
    }

    private void fetchDetails() {
        apiService.getPrompt(promptId).enqueue(new Callback<PromptDetailDto>() {
            @Override
            public void onResponse(@NonNull Call<PromptDetailDto> call, @NonNull Response<PromptDetailDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PromptDetailDto> call, @NonNull Throwable t) {
                Toast.makeText(PromptDetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(PromptDetailDto data) {
        tvTitle.setText(data.getTitle());
        String author = data.getAuthorName() != null ? data.getAuthorName() : "Unknown creator";
        tvAuthor.setText("by " + author);
        tvPromptBody.setText(data.getPromptBody());
        tvAiOutput.setText(data.getAiOutput());
        
        // AI Insights
        updateAiStatus(data.getAiStatus());
        tvAiReason.setText(data.getAiVerificationReason());

        if (data.getAiScore() != null) {
            cardScore.setVisibility(View.VISIBLE);
            double scoreValue = data.getAiScore() / 4.0;
            tvScore.setText(String.format("%.1f", scoreValue));
            if (scoreValue >= 8.5) {
                cardScore.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_success_bg));
                tvScore.setTextColor(ContextCompat.getColor(this, R.color.status_success_text));
            } else if (scoreValue >= 6.0) {
                cardScore.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_warning_bg));
                tvScore.setTextColor(ContextCompat.getColor(this, R.color.status_warning_text));
            } else {
                cardScore.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_error_bg));
                tvScore.setTextColor(ContextCompat.getColor(this, R.color.status_error_text));
            }
        } else {
            cardScore.setVisibility(View.GONE);
        }

        // Version History logic
        setupVersions(data.getVersions());

        // Polling if PENDING
        if ("PENDING".equals(data.getAiStatus())) {
            startPolling();
        } else {
            stopPolling();
        }
    }

    private void updateAiStatus(String status) {
        if (status == null) return;
        switch (status) {
            case "PENDING":
                tvAiStatus.setText("AI processing in progress");
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.status_warning_text));
                break;
            case "COMPLETED":
                tvAiStatus.setText("AI verification complete");
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success_text));
                break;
            case "FAILED":
                tvAiStatus.setText("AI evaluation failed");
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.status_error_text));
                break;
            default:
                tvAiStatus.setText(status);
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.md_theme_on_surface_variant));
                break;
        }
    }

    private void setupVersions(List<PromptVersionDto> versions) {
        if (versions == null || versions.isEmpty()) {
            versionSpinner.setVisibility(View.GONE);
            return;
        }
        versionSpinner.setVisibility(View.VISIBLE);
        List<String> versionLabels = new ArrayList<>();
        for (PromptVersionDto v : versions) {
            versionLabels.add("Version " + v.getVersion());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, versionLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        versionSpinner.setAdapter(adapter);

        versionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PromptVersionDto v = versions.get(position);
                tvPromptBody.setText(v.getPromptText());
                tvAiOutput.setText(v.getAiOutput() != null ? v.getAiOutput() : "No baseline for this version.");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void startPolling() {
        stopPolling();
        statusRunnable = this::fetchDetails;
        statusHandler.postDelayed(statusRunnable, 4000);
    }

    private void stopPolling() {
        if (statusRunnable != null) {
            statusHandler.removeCallbacks(statusRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPolling();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
