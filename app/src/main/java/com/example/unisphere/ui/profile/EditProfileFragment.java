package com.example.unisphere.ui.profile;

import static android.content.Context.MODE_PRIVATE;
import static com.example.unisphere.service.Util.USER_DATA;
import static com.example.unisphere.service.Util.getUserDataFromSharedPreferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Tag;
import com.example.unisphere.model.User;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class EditProfileFragment extends Fragment {


    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase;
    private NavController navController;

    private DatabaseReference tagReference;
    private DatabaseReference universityReference;
    private User currentUser;

    private Button uploadProfilePictureButton;
    private Uri profilePicture;
    private ImageView profilePictureView;

    private RecyclerView recyclerViewTags;
    private FloatingActionButton nextButton;
    private String universityKey;
    private StorageReference imageRef;
    private String universityName;
    private String email;
    private FloatingActionButton prevButton;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private List<Tag> tagList;

    private TagSelectAdapter tagSelectAdapter;

    public EditProfileFragment() {
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

                universityKey = snapshot.getChildren().iterator().next().getKey();
                tagReference = universityReference.child(universityKey).child("tags");
                tagReference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tagList.clear();
                        tagList.addAll(getTagListFromSnapshots(dataSnapshot));
                        tagSelectAdapter.notifyDataSetChanged();
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
        universityName = currentUser.getUniversity();
        email = currentUser.getEmailID();
        universityReference = firebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View editProfileView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initializeUIComponents(editProfileView);
        return editProfileView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void onPrevClicked(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    /**
     * Set up the firebase service, shared preferences and register for activity results.
     */
    private void setup() {
        this.firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));
        this.storage = FirebaseStorage.getInstance();
        SharedPreferences preferences = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
        currentUser = getUserDataFromSharedPreferences(preferences);
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

    private void removeUserFromAllTags(String userKey, List<String> selectedTags) {

        tagReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> updates = new HashMap<>();
                for (DataSnapshot tagSnapshot : snapshot.getChildren()) {
                    String tagName = (String) tagSnapshot.child("name").getValue();

                    List<String> existingUsers = (List<String>) tagSnapshot.child("users").getValue();
                    if (existingUsers != null) {

                        if (existingUsers.contains(userKey)) {
                            existingUsers.remove(userKey);
                            updates.put(tagSnapshot.getKey() + "/users", existingUsers);
                        }

                    }
                }
                if (!updates.isEmpty()) {
                    tagReference.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addUserToTags(String userKey, List<String> selectedTags) {
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
                    tagReference.updateChildren(updates);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void syncTags(String userKey, List<String> selectedTags) {
        removeUserFromAllTags(userKey, selectedTags);
        addUserToTags(userKey, selectedTags);

    }

    /**
     * Handle sign up complete event
     *
     * @param view
     */
    public void editUserProfile(View view) {

        if (!validateInputs()) {
            Toast.makeText(this.getContext(), "Invalid Inputs!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fireStoreProfilePictureURL = "/" + universityName + "/" + "Users" + "/" + email + "/" + "profile_picture/profile_picture.jpg";
        List<String> selectedTags = tagSelectAdapter.getSelectedTags().stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());

        universityReference.child(universityKey).child("users").orderByChild("emailID").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userKey = snapshot.getChildren().iterator().next().getKey();
                universityReference.child(universityKey).child("users").child(userKey).child("userTags").setValue(selectedTags).addOnCompleteListener(task -> {
                    if (profilePicture != null) {
                        imageRef = storage.getReference().child(fireStoreProfilePictureURL);
                        showLoadingScreen();
                        imageRef.putFile(profilePicture).addOnSuccessListener(taskSnapshot -> {

                            // Image upload successful, pop the fragment off the back stack
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.popBackStack();
                            hideLoadingScreen();
                        }).addOnFailureListener(exception -> {
                            // Handle unsuccessful image upload
                            Toast.makeText(getContext(), "Failed to upload profile picture!", Toast.LENGTH_SHORT).show();
                            hideLoadingScreen();
                        });
                    } else {
                        // No profile picture to upload, just pop the fragment off the back stack
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    }
                });
                syncTags(userKey, selectedTags);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void showLoadingScreen() {
        // Show loading screen fragment
        navController.navigate(R.id.loadingFragment);
    }

    private void hideLoadingScreen() {
        // Hide loading screen fragment
        navController.popBackStack();
    }


    private void initializeUIComponents(View view) {
        recyclerViewTags = view.findViewById(R.id.recyclerViewEditTags);

        uploadProfilePictureButton = view.findViewById(R.id.uploadEditedProfilePictureButton);
        uploadProfilePictureButton.setOnClickListener(View -> onUploadButtonClick());
        profilePictureView = view.findViewById(R.id.editProfilePictureView);
        nextButton = view.findViewById(R.id.editProfileConfirm);
        prevButton = view.findViewById(R.id.editProfileCancel);
        prevButton.setOnClickListener(this::onPrevClicked);
        nextButton.setOnClickListener(this::editUserProfile);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
        recyclerViewTags.setLayoutManager(layoutManager);

        tagList = new ArrayList<>();
        tagSelectAdapter = new TagSelectAdapter(tagList, true, recyclerViewTags);
        recyclerViewTags.setAdapter(tagSelectAdapter);
        loadTagList();

    }
}