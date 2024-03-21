package com.example.unisphere.model;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Organization extends User {

    Organization(String name, String emailID, String hashedPassword, String phoneNumber, String profilePictureURL, HashSet<Tag> userTags, String userRole) {
        super(name, emailID, phoneNumber, profilePictureURL, userTags, userRole);
    }
}
