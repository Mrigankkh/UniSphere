package com.example.unisphere.model;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Student extends User {


    // private final String program;


    public Student(String name, String emailID, String phoneNumber, String profilePictureURL, Collection<String> userTags, String userRole) {
        super(name, emailID, phoneNumber, profilePictureURL, userTags, "Student");

    }

//    public static StudentBuilder getBuilder() {
//        return new StudentBuilder();
//    }
//
//    public static class StudentBuilder extends UserBuilder {
//
//        private Date dateOfBirth;
//
//        private StudentBuilder() {
//            super();
//
//
//        }
//
//        public StudentBuilder date(Date date) {
//
//            return this;
//        }
//
//        public Student build() {
//            return new Student(this.university, this.name, this.emailID, this.hashedPassword, this.phoneNumber, this.profilePicture, this.userTags, "student", this.dateOfBirth);
//        }
//
//
//    }

}
