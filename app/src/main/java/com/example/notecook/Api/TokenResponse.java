package com.example.notecook.Api;

import com.example.notecook.Model.User;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("user")
    private User user;

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
