package com.example.notecook.Dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("force_password_change")
    private Boolean mustChangePassword;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @SerializedName("status")
    private String status;   // PASSWORD_CHANGE_REQUIRED or null
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("error")
    private String error;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public String getStatus() {
        return status;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
