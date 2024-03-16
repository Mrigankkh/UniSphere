package com.example.unisphere;

import com.google.firebase.FirebaseApp;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        super.onCreate();

    }
}
