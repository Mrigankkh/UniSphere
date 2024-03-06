package com.example.unisphere.service;

import com.example.unisphere.model.User;

public interface UserDataCallback {

    public void onUserDataRetrieved(User user);
    public void onUserDataRetrieveFailed();
}
