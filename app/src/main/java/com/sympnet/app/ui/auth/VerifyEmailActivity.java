package com.sympnet.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sympnet.app.R;
import com.sympnet.app.ui.home.HomeActivity;

public class VerifyEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Handler handler = new Handler();
    private Runnable checkVerificationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        TextView tvEmail = findViewById(R.id.tvEmail);
        Button btnResend = findViewById(R.id.btnResend);
        Button btnCheck  = findViewById(R.id.btnCheck);

        if (user != null) {
            tvEmail.setText("Verification email sent to:\n" + user.getEmail());
        }

        // Resend email
        btnResend.setOnClickListener(v -> {
            if (user != null) {
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Email resent!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Manually check if verified
        btnCheck.setOnClickListener(v -> checkVerification());

        // Auto-check every 5 seconds
        checkVerificationRunnable = new Runnable() {
            @Override
            public void run() {
                checkVerification();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(checkVerificationRunnable, 5000);
    }

    private void checkVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (user.isEmailVerified()) {
                    handler.removeCallbacks(checkVerificationRunnable);
                    Toast.makeText(this, "Email verified!", Toast.LENGTH_SHORT).show();

                    // ✅ Save registration data to ProfilePrefs
                    getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                            .edit()
                            .putString("firstName", getIntent().getStringExtra("firstName"))
                            .putString("lastName",  getIntent().getStringExtra("lastName"))
                            .putString("phone",     getIntent().getStringExtra("phone"))
                            .putString("dateOfBirth", getIntent().getStringExtra("dateOfBirth"))
                            .putString("gender",    getIntent().getStringExtra("gender"))
                            .apply();

                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkVerificationRunnable);
    }
}