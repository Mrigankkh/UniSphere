package com.example.unisphere.ui.messenger;

public class ChatMessage {
    private String message;
    private String senderEmail;
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(String message, String senderEmail, long timestamp) {
        this.message = message;
        this.senderEmail = senderEmail;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
