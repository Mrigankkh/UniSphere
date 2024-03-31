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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

        }

    }

    /**
     * Callback if the user is authenticated
     *
     * @param email that is logged in.
     */
    @Override
    public void onLoginSuccess(String email) {

        String domainName = getEmailDomain(email);
        univeresityReference = firebaseDatabase.getReference();
        univeresityReference.orderByChild("domain").equalTo(domainName).addListenerForSingleValueEvent(new ValueEventListener() {

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


                        DataSnapshot currUserSnapshot = snapshot.child(userKey);
                        try {


                            user = new User(
                                    currUserSnapshot.child("name").getValue(String.class),
                                    currUserSnapshot.child("emailID").getValue(String.class),
                                    null,
                                    getTagListFromSnapshots( currUserSnapshot.child("userTags"))
                                    ,
                                    currUserSnapshot.child("userRole").getValue(String.class),
                                    universityKey
                            );
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                        addUserDataToSharedPreferences(user);


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
        preferences.edit()
                .putString("username", user.getName())
                .putString("university", user.getUniversity())
                .putString("email", user.getEmailID())
                .putString("user_role", user.getUserRole())
                .putStringSet("tags", new HashSet<>()).apply();
        ;

    }

    private String getEmailDomain(String email) {
        return email.substring(email.indexOf('@') + 1);
    }

    private List<String> getTagListFromSnapshots(DataSnapshot dataSnapshot) {
        int i = 0;
        List<String> tags = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String data = snapshot.child("name").getValue(String.class);
            tags.add(data);
        }
        return tags;
    }


}