package com.sympnet.app.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.sympnet.app.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnReset;
    private ProgressBar progressBar;
    private TextView tvBackToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth        = FirebaseAuth.getInstance();
        etEmail      = findViewById(R.id.etEmail);
        btnReset     = findViewById(R.id.btnReset);
        progressBar  = findViewById(R.id.progressBar);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnReset.setOnClickListener(v -> sendResetEmail());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void sendResetEmail() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnReset.setEnabled(false);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnReset.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show();
                        finish(); // go back to login
                    } else {
                        Toast.makeText(this,
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}