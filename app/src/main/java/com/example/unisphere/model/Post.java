package com.example.unisphere.model;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public class Post {
    private String imageUrl;
    private String description;
    private String userId;
    private Long likes;
    private List<Comment> comments;
}
