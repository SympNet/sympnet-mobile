package com.sympnet.app.api.models;

public class AuthResponse {
    private String token;
    private String role;
    private String email;
    private int userId;
    private String message;

    // Getters
    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public int getUserId() { return userId; }
    public String getMessage() { return message; }
}