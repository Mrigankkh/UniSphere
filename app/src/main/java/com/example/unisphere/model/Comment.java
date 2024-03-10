package com.example.unisphere.model;

import lombok.Builder;

@Builder(toBuilder = true)
public class Comment {
    private String userId;
    private String text;

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
