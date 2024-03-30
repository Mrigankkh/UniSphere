package com.example.unisphere.signup_fragments;

import static android.content.Context.MODE_PRIVATE;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.unisphere.R;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Student;

import com.example.unisphere.model.Tag;
import com.example.unisphere.model.User;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SignupStudentFragment extends Fragment {


    private NavController navController;
    private FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    private DatabaseReference programReference;
    private DatabaseReference tagReference;
    private DatabaseReference universityReference;
    private DatabaseReference userReference;

    private SharedPreferences preferences;
    private Spinner programSelector;
    private String[] programs;
    private Button uploadProfilePictureButton;
    private Uri profilePicture;
    private ImageView profilePictureView;
    private String userRole;
    private RecyclerView recyclerViewTags;
    private FloatingActionButton nextButton;
    private String universityKey;
    private StorageReference imageRef;
    private String universityName;
    private String email;
    private FloatingActionButton prevButton;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private FirebaseAuth firebaseAuth;
    private List<Tag> tagList;

    private TagSelectAdapter tagSelectAdapter;

    public SignupStudentFragment() {
        // Required empty public constructor
    }


    /**
     * Get the list of predefined tags offered by the university.
     */
    public void loadTagList() {
        if (universityName == null) {
            Toast.makeText(getContext(), "Error! Please go back!", Toast.LENGTH_SHORT).show();
            return;
        }
        universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                universityKey = (String) snapshot.getChildren().iterator().next().getKey();
                tagReference = universityReference.child(universityKey).child("tags");
                tagReference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        tagList = getTagListFromSnapshots(dataSnapshot);
                        tagSelectAdapter = new TagSelectAdapter(tagList, recyclerViewTags);
                        recyclerViewTags.setAdapter(tagSelectAdapter);

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

    /**
     * Get the list of programs offered by the university.
     */
    public void loadProgramList() {

        if (universityName == null) {
            Toast.makeText(getContext(), "Error! Please go back!", Toast.LENGTH_SHORT).show();
            return;
        }
        universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                universityKey = (String) snapshot.getChildren().iterator().next().getKey();
                programReference = universityReference.child(universityKey).child("programs");
                programReference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        programs = getListFromSnapshots(dataSnapshot);
                        populateSpinner(getContext(), programSelector, programs);


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
    public void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        // Set up Firebase and Shared Preferences
        setup();

        galleryLauncher = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                // Handle gallery pick result
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    profilePicture = data.getData();
                    profilePictureView.setImageURI(profilePicture);
                }
            }
        });
        universityName = preferences.getString("university", "Northeastern University");
        email = preferences.getString("email", null);
        universityReference = firebaseDatabase.getReference();
        userRole =  preferences.getString("userRole", "Student");

        loadTagList();
        loadProgramList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_signup_student, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUIComponents(view);

        prevButton.setOnClickListener(this::onPrevClicked);
        nextButton.setOnClickListener(this::signupStudent);

    }

    private void onPrevClicked(View view) {
        navController.navigate(R.id.action_signupStudentFragment_to_signupUserFragment);
        navController.clearBackStack(R.id.action_signupStudentFragment_to_signupUserFragment);
    }

    /**
     * Set up the firebase service, shared preferences and register for activity results.
     */
    private void setup() {

        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.preferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);


    }

    /**
     * Gets a string array from a data snapshot.
     *
     * @param dataSnapshot is the datasnapshot containing data.
     */
    private String[] getListFromSnapshots(DataSnapshot dataSnapshot) {
        int i = 0;
        String[] programs = new String[(int) dataSnapshot.getChildrenCount()];
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String data = snapshot.getValue(String.class);
            programs[i++] = data;
        }
        return programs;
    }

    private List<Tag> getTagListFromSnapshots(DataSnapshot dataSnapshot) {
        int i = 0;
        List<Tag> tags = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String data = snapshot.child("name").getValue(String.class);
            tags.add(new Tag(data));
        }
        return tags;
    }


    /**
     * Add the string array to a spinner adapter.
     */
    private void populateSpinner(Context context, Spinner spinner, String[] arr) {
        spinner.setAdapter(new ArrayAdapter<>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, arr));
    }

    /**
     * Launch a image picker when the image upload button is clicked.
     */
    public void onUploadButtonClick() {

        galleryLauncher.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

    }

    public boolean validateInputs() {


//        if (!userConfirmPassword.getText().toString().equals(userPassword.getText().toString()))
//            return false;
        //TODO: Complete this method; Add stuff for date of birth and phone number
        return true;
    }


    public void addUserToTags(String userKey, List<String> selectedTags)
    {
        // Assuming there is a tag reference since it was previously used to load the tags

        tagReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> updates = new HashMap<>();
                for (DataSnapshot tagSnapshot : snapshot.getChildren()) {
                    String tagName = (String) tagSnapshot.child("name").getValue();
                    if (selectedTags.contains(tagName)) {
                        List<String> existingUsers = (List<String>) tagSnapshot.child("users").getValue();
                        if (existingUsers == null) {
                            existingUsers = new ArrayList<>(); // Initialize if users list doesn't exist
                        }
                        existingUsers.add(userKey);
                        updates.put(tagSnapshot.getKey() + "/users", existingUsers); // Update with the entire list

                    }
                }
                if (!updates.isEmpty()) {
                    tagReference.updateChildren(updates); // Write all updates at once
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });    }

    /**
     * Handle sign up complete event
     *
     * @param view
     */
    public void signupStudent(View view) {

        if (!validateInputs()) {
            Toast.makeText(this.getContext(), "Invalid Inputs!", Toast.LENGTH_SHORT).show();
            return;
        }
        String fireStoreProfilePictureURL = "/" + universityName + "/" + "Users" + "/" + email + "/" + "profile_picture/profile_picture.jpg";
        List<String> selectedTags = tagSelectAdapter.getSelectedTags().stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
        User user = new Student(preferences.getString("username", "NULL"), preferences.getString("email", "NULL"), preferences.getString("phone", "NULL"), fireStoreProfilePictureURL, selectedTags, "Student", new Date());

        String userKey = universityReference.child(universityKey).child("users").push().getKey();
        // Add this userkey in each selected tag

        userReference = universityReference.child(universityKey).child("users").child(userKey);
        String password = preferences.getString("password", null);
        preferences.edit().remove("password").apply();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    userReference.setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "User added successfully!");
                                    addUserToTags(userKey, selectedTags);
                                    imageRef = storage.getReference().child(fireStoreProfilePictureURL);
                                    imageRef.putFile(profilePicture).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            navController.navigate(R.id.action_signupStudentFragment_to_mainActivity);

                                        }
                                    });
                                }

                            });
                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(getContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    private void addProfilePictureToFirebase(Uri profilePictureUri) {


        imageRef = storage.getReference().child("/Northeastern University/Users/mrigank@northeastern.edu/profile_picture/profile_picture.jpg");
        UploadTask uploadTask = imageRef.putFile(profilePictureUri);


    }

    private void initializeUIComponents(View view) {
        recyclerViewTags = view.findViewById(R.id.recyclerViewTags);

        navController = Navigation.findNavController(view);
        programSelector = view.findViewById(R.id.programSelector);
        uploadProfilePictureButton = view.findViewById(R.id.uploadProfilePictureButton);
        uploadProfilePictureButton.setOnClickListener(View -> onUploadButtonClick());
        profilePictureView = view.findViewById(R.id.profilePictureView);
        nextButton = view.findViewById(R.id.signup_student_next_btn);
        prevButton = view.findViewById(R.id.signup_student_prev_btn);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
        recyclerViewTags.setLayoutManager(layoutManager);

        if(userRole.equals("Organization"))
        {
            //Hide program selector
        }

    }
}