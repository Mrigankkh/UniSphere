package com.example.unisphere.model;

import lombok.Builder;

public class Comment {
    private String userId;
    private String text;

    public Comment(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }
    public Comment() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
