package com.example.servlets.login;

import com.example.database.models.User;

public class LoginResponse {

    public User user;
    public String token;

    public LoginResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
