package com.taskmanager.infrastructure.http.dto;

public class LoginUserResponse {
    public String id;
    public String username;
    public String token;

    public LoginUserResponse(String id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }
}