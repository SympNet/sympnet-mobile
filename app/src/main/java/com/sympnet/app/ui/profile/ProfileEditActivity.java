package com.sympnet.app.ui.profile;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.sympnet.app.R;
import com.sympnet.app.ui.BaseActivity;

import java.util.Calendar;

public class ProfileEditActivity extends BaseActivity {

    private TextInputEditText etFirstName, etLastName, etPhone,
            etCnss, etDateOfBirth, etMedicalHistory;
    private de.hdodenhof.circleimageview.CircleImageView ivProfilePhoto;
    private TextView tvChangePhoto;
    private Button btnSave;
    private ProgressBar progressBar;
    private String selectedDateOfBirth = "";

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) ivProfilePhoto.setImageURI(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ✅ Setup drawer
        setupDrawer(R.id.toolbar, R.id.drawerLayout, R.id.navigationView);

        ivProfilePhoto   = findViewById(R.id.ivProfilePhoto);
        tvChangePhoto    = findViewById(R.id.tvChangePhoto);
        etFirstName      = findViewById(R.id.etFirstName);
        etLastName       = findViewById(R.id.etLastName);
        etPhone          = findViewById(R.id.etPhone);
        etCnss           = findViewById(R.id.etCnss);
        etDateOfBirth    = findViewById(R.id.etDateOfBirth);
        etMedicalHistory = findViewById(R.id.etMedicalHistory);
        btnSave          = findViewById(R.id.btnSave);
        progressBar      = findViewById(R.id.progressBar);

        // Load existing data
        SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
        etFirstName.setText(prefs.getString("firstName", ""));
        etLastName.setText(prefs.getString("lastName", ""));
        etPhone.setText(prefs.getString("phone", ""));
        etCnss.setText(prefs.getString("cnss", ""));
        etMedicalHistory.setText(prefs.getString("medicalHistory", ""));
        String savedDate = prefs.getString("dateOfBirth", "");
        if (!savedDate.isEmpty()) {
            etDateOfBirth.setText(savedDate);
            selectedDateOfBirth = savedDate;
        }

        tvChangePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDateOfBirth = String.format("%02d/%02d/%04d", day, month + 1, year);
                    etDateOfBirth.setText(selectedDateOfBirth);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void saveProfile() {
        String firstName      = etFirstName.getText().toString().trim();
        String lastName       = etLastName.getText().toString().trim();
        String phone          = etPhone.getText().toString().trim();
        String cnss           = etCnss.getText().toString().trim();
        String medicalHistory = etMedicalHistory.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "First and Last name are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SharedPreferences (until backend is ready)
        getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                .edit()
                .putString("firstName", firstName)
                .putString("lastName", lastName)
                .putString("phone", phone)
                .putString("cnss", cnss)
                .putString("dateOfBirth", selectedDateOfBirth)
                .putString("medicalHistory", medicalHistory)
                .apply();

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
        finish(); // go back to ProfileViewActivity
    }
}
