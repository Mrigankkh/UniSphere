package com.example.unisphere.ui.profile;

import static android.content.Context.MODE_PRIVATE;

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
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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


    private NavController navController;
    private FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    private DatabaseReference tagReference;
    private DatabaseReference universityReference;

    private SharedPreferences preferences;

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
    private FirebaseAuth firebaseAuth;
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

                universityKey = (String) snapshot.getChildren().iterator().next().getKey();
                tagReference = universityReference.child(universityKey).child("tags");
                tagReference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        tagList = getTagListFromSnapshots(dataSnapshot);
                        tagSelectAdapter = new TagSelectAdapter(tagList, true, recyclerViewTags);
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
        loadTagList();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_edit_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUIComponents(view);

        prevButton.setOnClickListener(this::onPrevClicked);
        nextButton.setOnClickListener(this::editUserProfile);

    }

    private void onPrevClicked(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();

    }

    /**
     * Set up the firebase service, shared preferences and register for activity results.
     */
    private void setup() {

        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");
        this.storage = FirebaseStorage.getInstance();
        this.preferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);


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

    private void removeUserFromAllTags(String userKey,List<String> selectedTags ) {

        tagReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> updates = new HashMap<>();
                for (DataSnapshot tagSnapshot : snapshot.getChildren()) {
                    String tagName = (String) tagSnapshot.child("name").getValue();

                    List<String> existingUsers = (List<String>) tagSnapshot.child("users").getValue();
                    if(existingUsers!= null) {

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
        removeUserFromAllTags(userKey,  selectedTags);
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

        universityReference.child(universityKey).child("users").orderByChild("emailID").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userKey = (String) snapshot.getChildren().iterator().next().getKey();
                universityReference.child(universityKey).child("users").child(userKey).child("userTags").setValue(selectedTags).addOnCompleteListener(task -> {
                    if(profilePicture!=null) {
                        imageRef = storage.getReference().child(fireStoreProfilePictureURL);

                        imageRef.putFile(profilePicture);
                    }


                });
                syncTags(userKey, selectedTags);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Add this userkey in each selected tag

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }


    private void initializeUIComponents(View view) {
        recyclerViewTags = view.findViewById(R.id.recyclerViewEditTags);

        navController = Navigation.findNavController(view);
        uploadProfilePictureButton = view.findViewById(R.id.uploadEditedProfilePictureButton);
        uploadProfilePictureButton.setOnClickListener(View -> onUploadButtonClick());
        profilePictureView = view.findViewById(R.id.editProfilePictureView);
        nextButton = view.findViewById(R.id.editProfileConfirm);
        prevButton = view.findViewById(R.id.editProfileCancel);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
        recyclerViewTags.setLayoutManager(layoutManager);


    }
}