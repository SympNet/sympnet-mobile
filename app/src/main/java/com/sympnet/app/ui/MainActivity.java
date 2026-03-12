package com.sympnet.app.ui;

import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sympnet.app.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup hamburger + drawer
        setupDrawer(R.id.toolbar, R.id.drawerLayout, R.id.navigationView);

        // Show welcome message
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && tvWelcome != null) {
            tvWelcome.setText("Bienvenue, " + user.getEmail());
        }
    }
}