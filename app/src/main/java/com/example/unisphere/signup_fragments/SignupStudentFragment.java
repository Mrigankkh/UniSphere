package com.example.unisphere.signup_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.unisphere.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;


public class SignupStudentFragment extends Fragment {


    private NavController navController;
    private FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference universityReference;
    private SharedPreferences preferences;
    private Spinner programSelector;
    private String[] programs;
    private Button uploadProfilePictureButton;
    private Uri profilePicture;
    private ImageView profilePictureView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    public SignupStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");

        preferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);


        universityReference = firebaseDatabase.getReference();
        universityReference.orderByChild("name").equalTo(preferences.getString("university", "Northeastern University")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String universityKey = (String) snapshot.getChildren().iterator().next().getKey();
                universityReference.child(universityKey).child("programs").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        programs = new String[(int) dataSnapshot.getChildrenCount()];
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String data = snapshot.getValue(String.class);
                            programs[i++] = data;
                        }
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

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        profilePicture = uri;
                        profilePictureView.setImageURI(profilePicture);

                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

    }

    public void onUploadButtonClick() {

        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

    }

    public void onProfilePictureUpload() {

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


        navController = Navigation.findNavController(view);
        programSelector = view.findViewById(R.id.programSelector);
        uploadProfilePictureButton = view.findViewById(R.id.uploadProfilePictureButton);
        uploadProfilePictureButton.setOnClickListener(View -> onUploadButtonClick());
        profilePictureView = view.findViewById(R.id.profilePictureView);
        // ArrayAdapter<String> programAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item     , programs);


    }

}