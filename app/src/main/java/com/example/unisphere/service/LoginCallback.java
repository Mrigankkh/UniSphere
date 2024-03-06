package com.example.unisphere.service;

import com.example.unisphere.model.User;

/**
 * A callback for log in. On successful login, onLoginSuccess callback function is called.
 */
public interface LoginCallback {

    /**
     * Callback function called on successful login.
     * @param user user that is logged in.
     */
    public void onLoginSuccess(String email);

    /**
     * Callback function called in case login is unsuccessful
     * @param e exception during login
     */
    public void onLoginFailure(Exception e);
}
