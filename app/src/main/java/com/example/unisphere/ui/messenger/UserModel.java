package com.example.unisphere.ui.messenger;


import java.io.Serializable;
import java.util.List;

public class UserModel implements Serializable {
    private String university;
    private String name;
    private String emailID;
    private String profilePictureURL;
    private List<String> userTags;
    private String userRole;

    public UserModel() {
    }

    public UserModel(String name, String emailID, String profilePictureURL, List<String> userTags, String userRole, String university) {
        this.name = name;
        this.emailID = emailID;
        this.profilePictureURL = profilePictureURL;
        this.userTags = userTags;
        this.userRole = userRole;
        this.university = university;
    }

    // Getters and Setters
    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public List<String> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<String> userTags) {
        this.userTags = userTags;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "User{" +
                "university='" + university + '\'' +
                ", name='" + name + '\'' +
                ", emailID='" + emailID + '\'' +
                ", profilePictureURL='" + profilePictureURL + '\'' +
                ", userTags=" + userTags +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}

