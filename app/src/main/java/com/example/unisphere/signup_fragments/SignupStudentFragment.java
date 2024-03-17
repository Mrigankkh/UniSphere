package com.example.unisphere.signup_fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
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
import android.widget.ArrayAdapter;
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
    private DatabaseReference programReference;
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

        // Set up Firebase and Shared Preferences
        setup();

        String university = preferences.getString("university", "Northeastern University");

        universityReference = firebaseDatabase.getReference();
        universityReference.orderByChild("name").equalTo(university).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String universityKey = (String) snapshot.getChildren().iterator().next().getKey();
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


    }

    /**
     * Set up the firebase service, shared preferences and register for activity results.
     */
    public void setup() {
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");
        preferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {

                    if (uri != null) {
                        profilePicture = uri;
                        profilePictureView.setImageURI(profilePicture);

                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

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

        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

    }




}