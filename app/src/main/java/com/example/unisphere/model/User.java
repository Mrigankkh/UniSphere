package com.example.unisphere.model;

import java.io.File;
import java.util.ArrayList;

/**
 * Represents a user of the application.
 */
public abstract class User {

    private University university;
    private String name;
    private String emailID;

    //Currently we do not hash it; Using Bcrypt to store hashed password would be good practice.
    private String hashedPassword;
    private String phoneNumber;
    private File profilePicture;

    private ArrayList<Tag> userTags;
    private String userRole;

    // Need to decide if email handle is suffecient and effecient for userID
    private String userID;






}
