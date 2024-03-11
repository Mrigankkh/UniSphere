package com.example.unisphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unisphere.model.Student;
import com.example.unisphere.model.University;
import com.example.unisphere.model.User;
import com.example.unisphere.service.AuthService;
import com.example.unisphere.service.DatabaseService;
import com.example.unisphere.service.LoginCallback;
import com.example.unisphere.service.DatabaseCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

/**
 * Activity for the Login Screen through email ID and password.
 */
public class LoginActivity extends AppCompatActivity implements LoginCallback {


    AuthService authService;
    DatabaseService databaseService;
    private Button loginButton;
    private EditText email;
    private EditText password;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference univeresityReference;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        authService = AuthService.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);


        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        return;
//
//        setContentView(R.layout.activity_login);
//
//        authService = AuthService.getInstance();
//        databaseService = DatabaseService.getInstance();
//
//        email = (EditText) findViewById(R.id.email);
//        password = (EditText) findViewById(R.id.password);
//
//
//        //Temporary login button logic
//        loginButton = (Button) findViewById(R.id.login);

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
                        if (snapshot.child(userKey).child("userRole").getValue(String.class).equals("student")) {

                            System.out.println("User is student");
                            DataSnapshot currStudentSnapshot = snapshot.child(userKey);
                            try {
                                user = new Student(new University(),
                                        currStudentSnapshot.child("name").getValue(String.class),
                                        currStudentSnapshot.child("emailID").getValue(String.class),
                                        currStudentSnapshot.child("password").getValue(String.class),
                                        currStudentSnapshot.child("phoneNumber").getValue(String.class),
                                        getprofilePictureFromLink(currStudentSnapshot.child("profilePicture").getValue(String.class)),
                                        new HashSet<>(),
                                        currStudentSnapshot.child("userRole").getValue(String.class),
                                        new SimpleDateFormat("dd/mm/yyyy")
                                                .parse(currStudentSnapshot.child("dateOfBirth").getValue(String.class)));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }




                        } else if (snapshot.child("userRole").equals("organization")) {
                            // Org code.
                        }

                        addUserDataToSharedPreferences(user);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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


    private void addUserDataToSharedPreferences(User user) throws RuntimeException {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putString("username", user.getName()).putString("university", user.getUniversity().getUniversityName()).putString("email", user.getEmailID())
                .putString("user_role", user.getUserRole()).putString("phone_number", user.getPhoneNumber()).putStringSet("tags", user.getUserTags())
        ;
        if (user.getUserRole().equals("student")) {
            preferences.edit().putString("DoB", ((Student) user).getDateOfBirth().toString());
        }
    }


//    @Override
//    public void onDataRetrieved(Object user) {
//        if (user instanceof User)
//            try {
//
//                addUserDataToSharedPreferences((User) user);
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//
//            } catch (Exception e) {
//                // TODO: Find out the correct error message here
//                Toast.makeText(LoginActivity.this, "Error logging in the user./ DBError", Toast.LENGTH_SHORT).show();
//            }
//    }

    private String getUniversityFromDomain(String domain) {
        return "Northeastern University";
    }

    private String getEmailDomain(String email) {
        return email.substring(email.indexOf('@'));
    }


    private File getprofilePictureFromLink(String link) {
        return null;
    }

}