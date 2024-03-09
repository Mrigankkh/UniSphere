package com.example.unisphere.model;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public class Post {
    private String imageUrl;
    private String description;
    private String userId;

    public List<String> getLikedByUserIds() {
        return likedByUserIds;
    }

    public void setLikedByUserIds(List<String> likedByUserIds) {
        this.likedByUserIds = likedByUserIds;
    }

    private List<String> likedByUserIds;

    private List<Comment> comments;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
