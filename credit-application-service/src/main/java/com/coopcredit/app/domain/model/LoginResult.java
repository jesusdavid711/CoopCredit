package com.coopcredit.app.domain.model;

public class LoginResult {
    private final String token;
    private final User user;

    public LoginResult(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
