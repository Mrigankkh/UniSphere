package com.example.unisphere.ui.messenger;

public class ChatMessage {
    private String message;
    private String senderEmail;
    private String recipientEmail;
    private long timestamp;

    // Default constructor for Firebase
    public ChatMessage() {
    }

    public ChatMessage(String message, String senderEmail, String recipientEmail, long timestamp) {
        this.message = message;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
