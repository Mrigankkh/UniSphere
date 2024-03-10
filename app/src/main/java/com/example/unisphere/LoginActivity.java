package com.example.unisphere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unisphere.model.User;
import com.example.unisphere.service.AuthService;
import com.example.unisphere.service.DatabaseService;
import com.example.unisphere.service.LoginCallback;
import com.example.unisphere.service.DatabaseCallback;

/**
 * Activity for the Login Screen through email ID and password.
 */
public class LoginActivity extends AppCompatActivity implements LoginCallback, DatabaseCallback {


    AuthService authService;
    DatabaseService databaseService;
    private Button loginButton;
    private EditText email;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = AuthService.getInstance();
        databaseService = DatabaseService.getInstance();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);


        //Temporary login button logic
        loginButton = (Button) findViewById(R.id.login);

    }

    private User getUserData(String email) {
        return null;
        //Temporary
        // return new Student();
    }

    private boolean checkInputValidity(String emailString, String passwordString) {
        //TODO: Check if any validation required
        return true;
    }

    public void signupUser(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void loginUser(View view) {

        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        if (!checkInputValidity(emailString, passwordString)) {
            Toast.makeText(LoginActivity.this, "Email entered is invalid!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            authenticate(emailString, passwordString);
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Error logging in the user.", Toast.LENGTH_SHORT).show();

        }
        //TODO: Error handling during authentication.
    }


    private void authenticate(String emailString, String passwordString) {

        try {

            authService.loginWithEmailAndPassword(emailString, passwordString, this);

        } catch (Exception e) {
            System.out.println("Inside auth method");
            //TODO: Exception handling
        }

    }

    @Override
    public void onLoginSuccess(String email) {

        databaseService.getUserData(email, this);

    }

    @Override
    public void onLoginFailure(Exception e) {


        // Toasting temporarily
        Toast.makeText(LoginActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
    }





    private void addUserDataToSharedPreferences(User user) throws RuntimeException {
    }


    @Override
    public void onDataRetrieved(Object user) {
        if (user instanceof User)
            try {

                addUserDataToSharedPreferences((User) user);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            } catch (Exception e) {
                // TODO: Find out the correct error message here
                Toast.makeText(LoginActivity.this, "Error logging in the user./ DBError", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void onDataRetrieveFailed() {

    }


}