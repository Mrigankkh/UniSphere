package com.example.unisphere.model;


import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a user of the application.
 */
public class User implements Serializable {

    private final String university;
    private final String name;
    private final String emailID;
    private final String profilePictureURL;
    private final Collection<String> userTags;
    private final String userRole;

    public User(String name, String emailID, String profilePictureURL, Collection<String> userTags, String userRole, String university) {
//        this.university = university;
        this.name = name;
        this.emailID = emailID;
        this.university = university;
        this.profilePictureURL = profilePictureURL;
        this.userTags = userTags;
        this.userRole = userRole;

    }

    public String getName() {
        return name;
    }

    public String getEmailID() {
        return emailID;
    }

    //Currently we do not hash it; Using Bcrypt to store hashed password would be good practice.

    public String getProfilePicture() {
        return profilePictureURL;
    }

    public Collection<String> getUserTags() {
        return userTags;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUniversity() {
        return university;
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
