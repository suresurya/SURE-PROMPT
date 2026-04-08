package com.suresurya.sureprompt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.suresurya.sureprompt.MainActivity;
import com.suresurya.sureprompt.R;
import com.suresurya.sureprompt.models.AuthResponse;
import com.suresurya.sureprompt.models.LoginRequest;
import com.suresurya.sureprompt.network.ApiService;
import com.suresurya.sureprompt.network.RetrofitClient;
import com.suresurya.sureprompt.network.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnUseDemo;
    private LinearProgressIndicator loginProgress;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);
        // If already logged in, skip to main
        if (tokenManager.getToken() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnUseDemo = findViewById(R.id.btnUseDemo);
        loginProgress = findViewById(R.id.loginProgress);
        
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnUseDemo.setOnClickListener(v -> {
            etUsername.setText("admin");
            etPassword.setText("admin123");
        });
    }

    private void attemptLogin() {
        tilUsername.setError(null);
        tilPassword.setError(null);

        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            if (user.isEmpty()) {
                tilUsername.setError(getString(R.string.login_username));
            }
            if (pass.isEmpty()) {
                tilPassword.setError(getString(R.string.login_password));
            }
            Toast.makeText(this, getString(R.string.login_fill_all), Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        apiService.login(new LoginRequest(user, pass)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    Toast.makeText(LoginActivity.this, getString(R.string.login_welcome_user, response.body().getUsername()), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                setLoading(false);
                String errorMsg = getString(R.string.login_network_error, t.getMessage());
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        btnUseDemo.setEnabled(!loading);
        loginProgress.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        btnLogin.setText(loading ? R.string.login_authenticating : R.string.login_sign_in);
    }
}
