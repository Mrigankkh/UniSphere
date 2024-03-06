package com.example.unisphere.service;

import androidx.annotation.NonNull;

import com.example.unisphere.model.Student;
import com.example.unisphere.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthService {

    private FirebaseAuth firebaseAuth;
    private static AuthService instance;

    /**
     * Get a static instance of the Auth Service.
     *
     * @return an instance of the auth service.
     */
    public static AuthService getInstance() {

        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private AuthService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public User loginWithEmailAndPassword(String email, String password, LoginCallback loginCallback) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


            }
        });

        return new Student();
    }

}
