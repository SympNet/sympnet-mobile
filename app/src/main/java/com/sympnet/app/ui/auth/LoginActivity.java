package com.sympnet.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.sympnet.app.R;
import com.sympnet.app.api.RetrofitClient;
import com.sympnet.app.api.models.AuthResponse;
import com.sympnet.app.api.models.LoginRequest;
import com.sympnet.app.ui.MainActivity;
import com.sympnet.app.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.firebase.auth.FirebaseUser;
import com.sympnet.app.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private TokenManager tokenManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = new TokenManager(this);
        mAuth = FirebaseAuth.getInstance();

        // Already logged in → go to MainActivity
        if (tokenManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        btnLogin         = findViewById(R.id.btnLogin);
        tvRegister       = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // ➕ Forgot password
        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Connexion...");

        // Firebase login only — no backend needed for now
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Check email verified
                        if (user != null && !user.isEmailVerified()) {
                            Toast.makeText(this,
                                    "Please verify your email first",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // ✅ Save session locally with Firebase email
                        tokenManager.saveSession(
                                user.getUid(),   // use Firebase UID as token for now
                                "PATIENT",
                                -1,
                                email
                        );

                        Toast.makeText(this, "Bienvenue !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this,
                                "Email ou mot de passe incorrect",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loginWithBackend(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        RetrofitClient.getAuthService().login(request)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                        if (response.isSuccessful() && response.body() != null) {
                            AuthResponse auth = response.body();
                            tokenManager.saveSession(
                                    auth.getToken(),
                                    auth.getRole(),
                                    auth.getUserId(),
                                    email
                            );
                            Toast.makeText(LoginActivity.this,
                                    "Bienvenue !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                        Toast.makeText(LoginActivity.this,
                                "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}