package com.sympnet.app.ui.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sympnet.app.R;
import com.sympnet.app.utils.TokenManager;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etPhone,
            etCnss, etDateOfBirth, etMedicalHistory;
    private de.hdodenhof.circleimageview.CircleImageView ivProfilePhoto;
    private TextView tvChangePhoto;
    private Button btnSave;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private TokenManager tokenManager;
    private Uri selectedImageUri = null;
    private String selectedDateOfBirth = "";

    // Image picker launcher
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProfilePhoto.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth        = FirebaseAuth.getInstance();
        storage      = FirebaseStorage.getInstance();
        tokenManager = new TokenManager(this);

        // Wire views
        ivProfilePhoto  = findViewById(R.id.ivProfilePhoto);
        tvChangePhoto   = findViewById(R.id.tvChangePhoto);
        etFirstName     = findViewById(R.id.etFirstName);
        etLastName      = findViewById(R.id.etLastName);
        etPhone         = findViewById(R.id.etPhone);
        etCnss          = findViewById(R.id.etCnss);
        etDateOfBirth   = findViewById(R.id.etDateOfBirth);
        etMedicalHistory= findViewById(R.id.etMedicalHistory);
        btnSave         = findViewById(R.id.btnSave);
        progressBar     = findViewById(R.id.progressBar);

        // Photo picker
        tvChangePhoto.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

        // Date picker
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Save
        btnSave.setOnClickListener(v -> saveProfile());
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

    private void saveProfile() {
        String firstName     = etFirstName.getText().toString().trim();
        String lastName      = etLastName.getText().toString().trim();
        String phone         = etPhone.getText().toString().trim();
        String cnss          = etCnss.getText().toString().trim();
        String medicalHistory= etMedicalHistory.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // If photo selected → upload to Firebase Storage first
        if (selectedImageUri != null) {
            uploadPhotoThenSave(firstName, lastName, phone, cnss, medicalHistory);
        } else {
            saveToBackend(firstName, lastName, phone, cnss, medicalHistory, null);
        }
    }

    private void uploadPhotoThenSave(String firstName, String lastName,
                                     String phone, String cnss, String medicalHistory) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        StorageReference ref = storage.getReference()
                .child("profile_photos/" + user.getUid() + ".jpg");

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(uri ->
                                saveToBackend(firstName, lastName, phone, cnss,
                                        medicalHistory, uri.toString())
                        )
                )
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Photo upload failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToBackend(String firstName, String lastName, String phone,
                               String cnss, String medicalHistory, String photoUrl) {
        // TODO: call your backend API here once teammate's endpoint is ready
        // For now, save locally and show success
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
    }
}