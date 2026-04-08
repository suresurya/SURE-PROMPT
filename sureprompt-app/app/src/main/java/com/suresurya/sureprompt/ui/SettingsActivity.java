package com.suresurya.sureprompt.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.suresurya.sureprompt.R;
import com.suresurya.sureprompt.network.ApiService;
import com.suresurya.sureprompt.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText etApiKey;
    private TextInputLayout tilApiKey;
    private MaterialButton btnSave;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etApiKey = findViewById(R.id.etApiKey);
        tilApiKey = findViewById(R.id.tilApiKey);
        btnSave = findViewById(R.id.btnSaveKey);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnSave.setOnClickListener(v -> saveKey());
    }

    private void saveKey() {
        tilApiKey.setError(null);
        String key = etApiKey.getText().toString().trim();
        if (key.length() < 20) {
            tilApiKey.setError(getString(R.string.settings_key_invalid));
            Toast.makeText(this, getString(R.string.settings_key_invalid), Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText(R.string.settings_connecting);

        apiService.saveKey(key).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                btnSave.setEnabled(true);
                btnSave.setText(R.string.settings_save);
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.settings_key_saved), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.settings_key_rejected, response.code()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText(R.string.settings_save);
                Toast.makeText(SettingsActivity.this, getString(R.string.settings_network_failure, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
