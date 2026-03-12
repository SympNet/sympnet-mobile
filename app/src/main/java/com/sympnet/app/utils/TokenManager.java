package com.sympnet.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "SympNetPrefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "user_email";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token, String role, int userId) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_ROLE, role)
                .putInt(KEY_USER_ID, userId)
                .apply();
    }

    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, null); }
    public int getUserId() { return prefs.getInt(KEY_USER_ID, -1); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }

    public boolean isLoggedIn() { return getToken() != null; }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public void clearToken() {
        logout();
    }
}