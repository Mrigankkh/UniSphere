package com.example.unisphere.service;

import com.google.firebase.database.FirebaseDatabase;

/**
 * This service is responsible for all data retrieval.
 */
public class DatabaseService {

    private static DatabaseService instance;
    private final FirebaseDatabase firebaseDatabase;

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    private DatabaseService() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    /**
     * Get user data from Cloud Database
     *
     * @param email email of the user
     */
    public void getUserData(String email, DatabaseCallback userDataCallback) {


        //userDataCallback.onUserDataRetrieved(new Student());

    }

    public void getUniversityList() {
    }

}
