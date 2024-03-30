package com.example.unisphere.model;

import android.net.Uri;

public class SearchedUser {
    String userName;

    Uri profilePicture;

    public String getUserName() {
        return userName;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public SearchedUser(String userName, Uri profilePicture) {
        this.userName = userName;
        this.profilePicture = profilePicture;
    }
}
