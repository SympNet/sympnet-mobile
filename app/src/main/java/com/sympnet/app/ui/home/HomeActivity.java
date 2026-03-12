package com.sympnet.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.sympnet.app.R;
import com.sympnet.app.ui.BaseActivity;
import com.sympnet.app.ui.auth.LoginActivity;
import com.sympnet.app.utils.TokenManager;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TokenManager tokenManager = new TokenManager(this);

        if (!tokenManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // ✅ Setup drawer
        setupDrawer(R.id.toolbar, R.id.drawerLayout, R.id.navigationView);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String email = tokenManager.getEmail();
        tvWelcome.setText("Welcome,\n" + (email != null ? email : "User"));
    }
}