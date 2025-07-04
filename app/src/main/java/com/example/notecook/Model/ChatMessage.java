package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ChatMessage {
    @SerializedName("id")
    private int id;
    @SerializedName("recipeId")
    private int recipeId;
    @SerializedName("senderId")
    private int senderId;
    @SerializedName("receiverId")
    private int receiverId;
    @SerializedName("message")
    private String message;
    @SerializedName("timestamp")
    private Date timestamp;
    @SerializedName("status")
    private String status;
    @SerializedName("readAt")
    private Date readAt;

    public ChatMessage(int recipeId, int receiverId, int senderId, String message, Date timestamp) {
        this.recipeId = recipeId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ChatMessage(int senderId, int receiverId, String message, Date timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
