package com.sympnet.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.sympnet.app.R;
import com.sympnet.app.api.RetrofitClient;
import com.sympnet.app.api.models.AuthResponse;
import com.sympnet.app.api.models.LoginRequest;
import com.sympnet.app.ui.auth.RegisterActivity;
import com.sympnet.app.ui.home.HomeActivity;
import com.sympnet.app.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = new TokenManager(this);

        // Already logged in → go to Home
        if (tokenManager.isLoggedIn()) {
            goToHome();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Loading...");

        RetrofitClient.getAuthService()
                .login(new LoginRequest(email, password))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call,
                                           Response<AuthResponse> response) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");

                        if (response.isSuccessful() && response.body() != null) {
                            AuthResponse auth = response.body();
                            tokenManager.saveSession(
                                    auth.getToken(),
                                    auth.getRole(),
                                    auth.getUserId(),
                                    auth.getEmail()
                            );
                            Toast.makeText(MainActivity.this,
                                    "Welcome!", Toast.LENGTH_SHORT).show();
                            goToHome();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Invalid email or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                        Toast.makeText(MainActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}