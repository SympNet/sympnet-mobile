package com.sympnet.app.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sympnet.app.R;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etEmail,
            etPassword, etConfirmPassword,
            etPhone, etDateOfBirth;
    private RadioGroup rgGender;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvLogin;
    private FirebaseAuth mAuth;
    private String selectedDateOfBirth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etFirstName       = findViewById(R.id.etFirstName);
        etLastName        = findViewById(R.id.etLastName);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone           = findViewById(R.id.etPhone);
        etDateOfBirth     = findViewById(R.id.etDateOfBirth);
        rgGender          = findViewById(R.id.rgGender);
        btnRegister       = findViewById(R.id.btnRegister);
        progressBar       = findViewById(R.id.progressBar);
        tvLogin           = findViewById(R.id.tvLogin);

        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDateOfBirth = String.format("%04d-%02d-%02d",
                            year, month + 1, day);
                    etDateOfBirth.setText(String.format("%02d/%02d/%04d",
                            day, month + 1, year));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName  = etLastName.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString().trim();
        String confirm   = etConfirmPassword.getText().toString().trim();
        String phone     = etPhone.getText().toString().trim();

        // Gender
        String gender = "";
        int genderId = rgGender.getCheckedRadioButtonId();
        if (genderId == R.id.rbMale)        gender = "Male";
        else if (genderId == R.id.rbFemale) gender = "Female";

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirm.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDateOfBirth.isEmpty()) {
            Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        String finalGender = gender;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        progressBar.setVisibility(View.GONE);
                                        btnRegister.setEnabled(true);
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(this,
                                                    "Verification email sent to " + email,
                                                    Toast.LENGTH_LONG).show();

                                            // Save registration data for profile
                                            getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                                                    .edit()
                                                    .putString("firstName", firstName)
                                                    .putString("lastName", lastName)
                                                    .putString("phone", phone)
                                                    .putString("dateOfBirth", selectedDateOfBirth)
                                                    .putString("gender", finalGender)
                                                    .apply();

                                            Intent intent = new Intent(this, VerifyEmailActivity.class);
                                            intent.putExtra("firstName", firstName);
                                            intent.putExtra("lastName", lastName);
                                            intent.putExtra("email", email);
                                            intent.putExtra("phone", phone);
                                            intent.putExtra("dateOfBirth", selectedDateOfBirth);
                                            intent.putExtra("gender", finalGender);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this,
                                                    "Failed to send verification email",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}