package com.example.servlets.register;

import com.example.database.models.User;

public class RegisterResponse {

    public User user;
    public String token;

    public RegisterResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
