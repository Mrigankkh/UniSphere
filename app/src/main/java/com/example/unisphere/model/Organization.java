package com.example.unisphere.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Organization extends User{

    Organization(University university, String name, String emailID, String hashedPassword, String phoneNumber, File profilePicture, HashSet<Tag> userTags, String userRole) {
        super(university, name, emailID, hashedPassword, phoneNumber, profilePicture, userTags, userRole);
    }
}
