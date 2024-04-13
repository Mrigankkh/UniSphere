package com.example.unisphere.model;


import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a user of the application.
 */
public  class User implements Serializable {

    public String getName() {
        return name;
    }

    public String getEmailID() {
        return emailID;
    }




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

    private String university;
    private String name;
    private String emailID;

    //Currently we do not hash it; Using Bcrypt to store hashed password would be good practice.


    private String profilePictureURL;
    private Collection<String> userTags;
    private String userRole;

    public User(String name, String emailID, String profilePictureURL, Collection<String> userTags, String userRole, String university) {
//        this.university = university;
        this.name = name;
        this.emailID = emailID;
       this.university = university;
        this.profilePictureURL = profilePictureURL;
        this.userTags = userTags;
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
