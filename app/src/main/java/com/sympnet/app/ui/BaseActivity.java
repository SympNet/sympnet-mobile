package com.sympnet.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sympnet.app.R;
import com.sympnet.app.ui.auth.LoginActivity;
import com.sympnet.app.ui.body.BodyMapActivity;
import com.sympnet.app.ui.home.HomeActivity;
import com.sympnet.app.ui.profile.ProfileViewActivity;
import com.sympnet.app.utils.TokenManager;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Call this after setContentView() in each activity
    protected void setupDrawer(int toolbarId, int drawerId, int navViewId) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(drawerId);
        NavigationView navigationView = findViewById(navViewId);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set email in header
        TokenManager tokenManager = new TokenManager(this);
        NavigationView navView = findViewById(navViewId);
        android.widget.TextView navEmail = navView.getHeaderView(0)
                .findViewById(R.id.navUserEmail);
        if (navEmail != null && tokenManager.getEmail() != null) {
            navEmail.setText(tokenManager.getEmail());
        }

        // Sidebar item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                if (!(this instanceof HomeActivity)) {
                    startActivity(new Intent(this, HomeActivity.class));
                }
            } else if (id == R.id.nav_profile) {
                if (!(this instanceof ProfileViewActivity)) {
                    startActivity(new Intent(this, ProfileViewActivity.class));
                }
            } else if (id == R.id.nav_body) {
                if (!(this instanceof BodyMapActivity)) {
                    startActivity(new Intent(this, BodyMapActivity.class));
                }
            } else if (id == R.id.nav_settings) {
                // TODO: settings screen
            } else if (id == R.id.nav_logout) {
                tokenManager.logout();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}