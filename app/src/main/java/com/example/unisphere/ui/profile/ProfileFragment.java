package com.example.unisphere.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Tag;
import com.example.unisphere.service.AuthService;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {


    private Button tempLogout;
    private SharedPreferences sharedPreferences;
    private NavController navController;
    AuthService authService;
    StorageReference storageRef;
    private DatabaseReference universityReference;
    private DatabaseReference tagReference;
    private DatabaseReference userReference;
    private FirebaseDatabase firebaseDatabase;
    private String universityKey;
    private ImageView profilePicture;
    private TextView profileUsername;
    private TextView profileEmail;
    private TextView profileUniversity;
    private TextView profileUserRole;
    private RecyclerView recyclerViewTags;
    private List<Tag> tagList;

    private TagSelectAdapter tagSelectAdapter;


    private List<Tag> getTagListFromSnapshots(DataSnapshot dataSnapshot) {
        int i = 0;
        List<Tag> tags = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String data = snapshot.getValue(String.class);
            tags.add(new Tag(data));
        }
        return tags;
    }

    /**
     * Get the list of predefined tags offered by the university.
     */
    public void loadTagList() {
        String universityName = sharedPreferences.getString("university", null);
        if (universityName == null) {
            Toast.makeText(getContext(), "Error! Please go back!", Toast.LENGTH_SHORT).show();
            return;
        }
        universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = sharedPreferences.getString("email", "NULL");
                universityKey = (String) snapshot.getChildren().iterator().next().getKey();
                userReference = universityReference.child(universityKey).child("users");

                userReference.orderByChild("emailID").equalTo(email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userKey = (String) snapshot.getChildren().iterator().next().getKey();

                        tagReference = userReference.child(userKey).child("userTags");
                        tagReference.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                tagList = getTagListFromSnapshots(dataSnapshot);
                                tagSelectAdapter = new TagSelectAdapter(tagList, false, recyclerViewTags);
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        authService = AuthService.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");
        universityReference = firebaseDatabase.getReference();

        String email = sharedPreferences.getString("email", null);
        StorageReference imageRef = storageRef.child("/Northeastern University/Users/" + email + "/profile_picture/profile_picture.jpg");
        loadTagList();


        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri.toString())
                    .resize(400, 400)
                    .centerCrop()
                    .into(profilePicture);


        }).addOnFailureListener(error -> {

        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewTags = view.findViewById(R.id.recyclerViewProfileTags);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
        recyclerViewTags.setLayoutManager(layoutManager);

        tempLogout = view.findViewById(R.id.tempLogout);
        profilePicture = view.findViewById(R.id.profilePicture);
        tempLogout.setOnClickListener(View -> logOut());
        navController = Navigation.findNavController(view);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileUniversity = view.findViewById(R.id.profileUniversity);
        profileUserRole = view.findViewById(R.id.profileUserRole);
        profileUsername = view.findViewById(R.id.profileUsername);
        String university = sharedPreferences.getString("university", "NULL");
        String username = sharedPreferences.getString("username", "NULL");
        String email = sharedPreferences.getString("email", "NULL");
        String userRole = sharedPreferences.getString("user_role", "NULL");

        profileUniversity.setText(university);
        profileUsername.setText(username);
        profileEmail.setText(email);
        profileUserRole.setText(userRole);


    }

    public void logOut() {
        sharedPreferences.edit().clear();
        authService.signOut();
        navController.clearBackStack(R.id.activity_login);
        navController.navigate(R.id.action_navigation_profile_to_activity_login);
        getActivity().finish();


    }
}