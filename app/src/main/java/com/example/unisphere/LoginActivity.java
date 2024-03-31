package com.example.unisphere;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unisphere.model.Student;
import com.example.unisphere.model.User;
import com.example.unisphere.service.AuthService;
import com.example.unisphere.service.LoginCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashSet;

/**
 * Activity for the Login Screen through email ID and password.
 */
public class LoginActivity extends AppCompatActivity implements LoginCallback {


    AuthService authService;
    private Button loginButton;
    private EditText email;
    private EditText password;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference univeresityReference;
    private DatabaseReference userReference;
    private FirebaseStorage storage;

    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Check if user exists.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        authService = AuthService.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");
        this.storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);


    }


    private boolean checkInputValidity(String emailString, String passwordString) {

        if (emailString.isEmpty() || passwordString.length() < 6)
            return false;
        //TODO: Check if any validation required
        return true;
    }

    /**
     * If sign up is clicked, user is redirected to the signup view.
     *
     * @param view
     */
    public void onSignupClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the case when the user presses the login button from the UI.
     *
     * @param view
     */
    public void onLoginClicked(View view) {

        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        //Check if inputs are valid
        if (!checkInputValidity(emailString, passwordString)) {
            Toast.makeText(LoginActivity.this, "Email or password entered is invalid!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Authenticate the user
        try {
            authenticate(emailString, passwordString);
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Error logging in the user.", Toast.LENGTH_SHORT).show();

        }
        //TODO: Error handling during authentication.
    }

    /**
     * Authenticate the user using the email and password.
     *
     * @param emailString    is the email ID of the user
     * @param passwordString is the password of the user
     */
    private void authenticate(String emailString, String passwordString) {

        try {
            //call the auth service
            authService.loginWithEmailAndPassword(emailString, passwordString, this);

        } catch (Exception e) {
            System.out.println("Inside auth method");
            //TODO: Exception handling
        }

    }

    /**
     * Callback if the user is authenticated
     *
     * @param email that is logged in.
     */
    @Override
    public void onLoginSuccess(String email) {

        String universityName = getUniversityFromDomain(getEmailDomain(email));
        univeresityReference = firebaseDatabase.getReference();
        univeresityReference.orderByChild("name").equalTo("Northeastern University").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println(snapshot);
                String universityKey = (String) snapshot.getChildren().iterator().next().getKey();
                userReference = univeresityReference.child(universityKey).child("users");
                userReference.orderByChild("emailID").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = null;

                        String userKey = (String) snapshot.getChildren().iterator().next().getKey();

                        //TODO: check if snapshot exists first
                        if (snapshot.child(userKey).child("userRole").getValue(String.class).equals("Student")) {

                            DataSnapshot currStudentSnapshot = snapshot.child(userKey);
                            try {
                                user = new Student(
                                        currStudentSnapshot.child("name").getValue(String.class),
                                        currStudentSnapshot.child("emailID").getValue(String.class),
                                        currStudentSnapshot.child("phoneNumber").getValue(String.class),
                                        null, new HashSet<>(),
                                        currStudentSnapshot.child("userRole").getValue(String.class));


                                System.out.println("User is student");

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }


                        } else if (snapshot.child("userRole").equals("organization")) {
                            // Org code.
                        }

                        addUserDataToSharedPreferences(user);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onLoginFailure(Exception e) {


        // Toasting temporarily
        Toast.makeText(LoginActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
    }

    /**
     * Add the user data to shared preferences.
     *
     * @param user is the user data of the user
     * @throws RuntimeException
     */
    private void addUserDataToSharedPreferences(User user) throws RuntimeException {
        SharedPreferences preferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        //TODO: University name is hardcoded
        preferences.edit().putString("username", user.getName()).putString("university", "Northeastern University").putString("email", user.getEmailID())
                .putString("user_role", user.getUserRole()).putString("phone_number", user.getPhoneNumber()).putStringSet("tags", new HashSet<>()).apply();
        ;

    }

    /**
     * Gets the university name from domain "university" in email@university.edu
     *
     * @param domain
     * @return name of the university
     */
    private String getUniversityFromDomain(String domain) {
        return "Northeastern University";
    }

    private String getEmailDomain(String email) {
        return email.substring(email.indexOf('@'));
    }


}