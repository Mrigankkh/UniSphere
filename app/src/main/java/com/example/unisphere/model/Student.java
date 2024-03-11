package com.example.unisphere.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Student extends User {
    private Date dateOfBirth;

    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    // private final String program;


    public Student(University university, String name, String emailID, String hashedPassword, String phoneNumber, File profilePicture, HashSet<Tag> userTags, String userRole, Date dateOfBirth) {
        super(university, name, emailID, hashedPassword, phoneNumber, profilePicture, userTags, "Student");
        this.dateOfBirth = dateOfBirth;
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
