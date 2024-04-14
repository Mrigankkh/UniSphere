package com.example.unisphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {
    public String imageUrl;
    public String description;
    public String userId;

    public String getLatestLikeAction() {
        return latestLikeAction;
    }

    public void setLatestLikeAction(String latestLikeAction) {
        this.latestLikeAction = latestLikeAction;
    }

    public String latestLikeAction;

    public String getKeyFirebase() {
        return keyFirebase;
    }

    public void setKeyFirebase(String keyFirebase) {
        this.keyFirebase = keyFirebase;
    }

    public String keyFirebase;

    public Post() {
        likedByUserIds=new ArrayList<>();
        comments = new ArrayList<>();
    }

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
