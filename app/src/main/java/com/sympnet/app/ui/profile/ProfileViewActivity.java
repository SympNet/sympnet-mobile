package com.sympnet.app.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.sympnet.app.R;
import com.sympnet.app.ui.BaseActivity;
import com.sympnet.app.utils.TokenManager;

public class ProfileViewActivity extends BaseActivity {

    private TextView tvName, tvEmail, tvPhone, tvDateOfBirth, tvCnss, tvMedicalHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        // ✅ Setup drawer
        setupDrawer(R.id.toolbar, R.id.drawerLayout, R.id.navigationView);

        tvName           = findViewById(R.id.tvName);
        tvEmail          = findViewById(R.id.tvEmail);
        tvPhone          = findViewById(R.id.tvPhone);
        tvDateOfBirth    = findViewById(R.id.tvDateOfBirth);
        tvCnss           = findViewById(R.id.tvCnss);
        tvMedicalHistory = findViewById(R.id.tvMedicalHistory);
        Button btnEdit   = findViewById(R.id.btnEdit);

        loadProfileData();

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileEditActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        TokenManager tokenManager = new TokenManager(this);
        SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);

        String firstName      = prefs.getString("firstName", "");
        String lastName       = prefs.getString("lastName", "");
        String phone          = prefs.getString("phone", "");
        String dateOfBirth    = prefs.getString("dateOfBirth", "");
        String cnss           = prefs.getString("cnss", "");
        String medicalHistory = prefs.getString("medicalHistory", "");

        String fullName = (firstName + " " + lastName).trim();
        tvName.setText(fullName.isEmpty() ? "No name set" : fullName);
        tvEmail.setText(tokenManager.getEmail() != null ? tokenManager.getEmail() : "");
        tvPhone.setText(phone.isEmpty() ? "Not set" : phone);
        tvDateOfBirth.setText(dateOfBirth.isEmpty() ? "Not set" : dateOfBirth);
        tvCnss.setText(cnss.isEmpty() ? "Not set" : cnss);
        tvMedicalHistory.setText(medicalHistory.isEmpty() ? "Not set" : medicalHistory);
    }
}