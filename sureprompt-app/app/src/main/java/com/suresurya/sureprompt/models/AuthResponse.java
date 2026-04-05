package com.suresurya.sureprompt.models;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUsername() {
        return username;
    }
}
