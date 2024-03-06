package com.example.unisphere.service;

/**
 * This service is responsible for all data retrieval.
 */
public class DatabaseService {

    DatabaseService databaseService;

    public DatabaseService getInstance() {
        if (databaseService == null) {
            databaseService = new DatabaseService();
        }
        return databaseService;
    }

    private DatabaseService() {

    }
}
