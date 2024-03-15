package com.example.unisphere.service;


/**
 * A callback for log in. On successful login, onLoginSuccess callback function is called.
 */
public interface LoginCallback {

    /**
     * Callback function called on successful login.
     * @param email that is logged in.
     */
     void onLoginSuccess(String email);

    /**
     * Callback function called in case login is unsuccessful
     * @param e exception during login
     */
     void onLoginFailure(Exception e);
}
