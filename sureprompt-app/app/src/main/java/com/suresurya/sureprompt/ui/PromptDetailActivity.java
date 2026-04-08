package com.suresurya.sureprompt.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.suresurya.sureprompt.R;
import com.suresurya.sureprompt.models.PromptDetailDto;
import com.suresurya.sureprompt.models.PromptVersionDto;
import com.suresurya.sureprompt.network.ApiService;
import com.suresurya.sureprompt.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromptDetailActivity extends AppCompatActivity {

    private Long promptId;
    private ApiService apiService;
    private final Handler statusHandler = new Handler(Looper.getMainLooper());
    private Runnable statusRunnable;

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvAiStatus;
    private TextView tvAiReason;
    private TextView tvScore;
    private TextView tvPromptBody;
    private TextView tvAiOutput;
    private MaterialCardView cardScore;
    private MaterialButton btnTryLive;
    private MaterialButton btnCopyPrompt;
    private TextInputLayout tilVersion;
    private MaterialAutoCompleteTextView versionDropdown;
    private ChipGroup chipGroupTags;
    private LinearProgressIndicator progressPending;

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
        btnTryLive = findViewById(R.id.btnTryLive);
        btnCopyPrompt = findViewById(R.id.btnCopyPrompt);
        tilVersion = findViewById(R.id.tilVersion);
        versionDropdown = findViewById(R.id.versionDropdown);
        chipGroupTags = findViewById(R.id.chipGroupTags);
        progressPending = findViewById(R.id.progressPending);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnTryLive.setOnClickListener(view -> {
            String prompt = tvPromptBody.getText().toString();
            LiveTryBottomSheet.newInstance(prompt).show(getSupportFragmentManager(), "LiveTry");
        });

        btnCopyPrompt.setOnClickListener(view -> {
            copyToClipboard(tvPromptBody.getText().toString());
            Snackbar.make(view, R.string.prompt_copy_success, Snackbar.LENGTH_SHORT).show();
        });
    }

    private void fetchDetails() {
        apiService.getPrompt(promptId).enqueue(new Callback<PromptDetailDto>() {
            @Override
            public void onResponse(@NonNull Call<PromptDetailDto> call, @NonNull Response<PromptDetailDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                } else {
                    Toast.makeText(PromptDetailActivity.this, getString(R.string.prompt_loaded_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PromptDetailDto> call, @NonNull Throwable t) {
                Toast.makeText(PromptDetailActivity.this, getString(R.string.prompt_loaded_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(PromptDetailDto data) {
        tvTitle.setText(data.getTitle());
        String author = data.getAuthorName() != null ? data.getAuthorName() : getString(R.string.prompt_author_fallback);
        tvAuthor.setText(getString(R.string.prompt_author_format, author));

        tvPromptBody.setText(nonEmptyOrFallback(data.getPromptBody(), "-"));
        tvAiOutput.setText(nonEmptyOrFallback(data.getAiOutput(), "-"));

        bindTags(data.getTags());
        updateAiStatus(data.getAiStatus());

        String reason = nonEmptyOrFallback(data.getAiVerificationReason(), getString(R.string.prompt_reason_fallback));
        tvAiReason.setText(reason);

        bindScore(data.getAiScore());
        setupVersions(data.getVersions());

        if ("PENDING".equals(data.getAiStatus())) {
            startPolling();
        } else {
            stopPolling();
        }
    }

    private void bindScore(Double score) {
        if (score == null) {
            cardScore.setVisibility(View.GONE);
            return;
        }

        cardScore.setVisibility(View.VISIBLE);
        double scoreValue = score / 4.0;
        tvScore.setText(String.format(Locale.US, "%.1f", scoreValue));

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
    }

    private void bindTags(List<String> tags) {
        chipGroupTags.removeAllViews();
        if (tags == null || tags.isEmpty()) {
            return;
        }

        for (String tag : tags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setEnsureMinTouchTargetSize(false);
            chip.setChipBackgroundColorResource(R.color.tag_chip_bg);
            chip.setTextColor(ContextCompat.getColor(this, R.color.tag_chip_text));
            chipGroupTags.addView(chip);
        }
    }

    private void updateAiStatus(String status) {
        if (status == null) {
            progressPending.setVisibility(View.GONE);
            tvAiStatus.setText(getString(R.string.item_status_unknown));
            tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.md_theme_on_surface_variant));
            return;
        }

        switch (status) {
            case "PENDING":
                progressPending.setVisibility(View.VISIBLE);
                tvAiStatus.setText(getString(R.string.prompt_status_pending));
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.status_warning_text));
                break;
            case "COMPLETED":
                progressPending.setVisibility(View.GONE);
                tvAiStatus.setText(getString(R.string.prompt_status_completed));
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success_text));
                break;
            case "FAILED":
                progressPending.setVisibility(View.GONE);
                tvAiStatus.setText(getString(R.string.prompt_status_failed));
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.status_error_text));
                break;
            default:
                progressPending.setVisibility(View.GONE);
                tvAiStatus.setText(status);
                tvAiStatus.setTextColor(ContextCompat.getColor(this, R.color.md_theme_on_surface_variant));
                break;
        }
    }

    private void setupVersions(List<PromptVersionDto> versions) {
        if (versions == null || versions.isEmpty()) {
            tilVersion.setVisibility(View.GONE);
            return;
        }

        tilVersion.setVisibility(View.VISIBLE);
        List<String> versionLabels = new ArrayList<>();
        for (int i = 0; i < versions.size(); i++) {
            PromptVersionDto version = versions.get(i);
            if (i == 0) {
                versionLabels.add(getString(R.string.version_latest_label, version.getVersion()));
            } else {
                versionLabels.add(getString(R.string.version_label, version.getVersion()));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                versionLabels
        );
        versionDropdown.setAdapter(adapter);

        versionDropdown.setOnItemClickListener((parent, view, position, id) -> bindVersion(versions.get(position)));
        versionDropdown.setText(versionLabels.get(0), false);
        bindVersion(versions.get(0));
    }

    private void bindVersion(PromptVersionDto version) {
        if (version == null) {
            return;
        }

        tvPromptBody.setText(nonEmptyOrFallback(version.getPromptText(), tvPromptBody.getText().toString()));
        String aiOutput = nonEmptyOrFallback(version.getAiOutput(), getString(R.string.prompt_no_baseline_version));
        tvAiOutput.setText(aiOutput);
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

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        ClipData clip = ClipData.newPlainText("prompt-template", text);
        clipboard.setPrimaryClip(clip);
    }

    private String nonEmptyOrFallback(String text, String fallback) {
        if (TextUtils.isEmpty(text)) {
            return fallback;
        }
        return text;
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
