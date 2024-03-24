package com.example.notecook.Dto;

import com.example.notecook.Model.User;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("user")
    private User user;
    @SerializedName("token")
    private String token;

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
