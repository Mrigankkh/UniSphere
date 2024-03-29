package com.example.unisphere.model;


import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a user of the application.
 */
public abstract class User {

    public String getName() {
        return name;
    }

    public String getEmailID() {
        return emailID;
    }


    public String getPhoneNumber() {
        return phoneNumber;
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


    //    private University university;
    private String name;
    private String emailID;

    //Currently we do not hash it; Using Bcrypt to store hashed password would be good practice.

    private String phoneNumber;
    private String profilePictureURL;
    private Collection<String> userTags;
    private String userRole;

    public User(String name, String emailID, String phoneNumber, String profilePictureURL, Collection<String> userTags, String userRole) {
//        this.university = university;
        this.name = name;
        this.emailID = emailID;
        this.phoneNumber = phoneNumber;
        this.profilePictureURL = profilePictureURL;
        this.userTags = userTags;
        this.userRole = userRole;

    }


//
//    public static UserBuilder getBuilder() {
//        return null;
//    }
//
//    public static class UserBuilder {
//
//        University university;
//        String name;
//        String emailID;
//
//        //Currently we do not hash it; Using Bcrypt to store hashed password would be good practice.
//        String hashedPassword;
//        String phoneNumber;
//        File profilePicture;
//        HashSet<Tag> userTags;
//        String userRole;
//
//        public UserBuilder university(University university) {
//            this.university = university;
//            return this;
//        }
//
//        public UserBuilder name(String name) {
//            this.name = name;
//            return this;
//        }
//
//        public UserBuilder email(String emailID) {
//            this.emailID = emailID;
//            return this;
//        }
//
//        public UserBuilder hashedPassword(String hashedPassword) {
//            this.hashedPassword = hashedPassword;
//            return this;
//        }
//
//        public UserBuilder phoneNumber(String phoneNumber) {
//            this.phoneNumber = phoneNumber;
//            return this;
//        }
//
//        public UserBuilder profilePicture(File profilePicture) {
//            this.profilePicture = profilePicture;
//            return this;
//        }
//
//        private UserBuilder userTags(HashSet<Tag> userTags) {
//            this.userTags = userTags;
//            return this;
//        }
//
//        private UserBuilder userRole(String userRole) {
//            this.userRole = userRole;
//            return this;
//        }
//
//
//        UserBuilder() {
//
//
//        }
//
//
//    }


}
