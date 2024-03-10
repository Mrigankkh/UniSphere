package com.example.unisphere.service;

import com.example.unisphere.model.User;

public interface DatabaseCallback<T> {

    public void onDataRetrieved(T data);
    public void onDataRetrieveFailed();
}
