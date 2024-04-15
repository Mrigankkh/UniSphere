package com.example.unisphere.ui.search;


import static android.content.Context.MODE_PRIVATE;
import static com.example.unisphere.service.Util.USER_DATA;
import static com.example.unisphere.service.Util.getUserDataFromSharedPreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.unisphere.adapter.UserPost.UserPostAdapter;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Tag;
import com.example.unisphere.model.User;
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

public class SearchedUsersearchedUserProfileFragment extends Fragment {


    AuthService authService;
    StorageReference storageRef;
    private NavController navController;
    private DatabaseReference universityReference;
    private DatabaseReference tagReference;
    private DatabaseReference userReference;
    private DatabaseReference postReference;
    private FirebaseDatabase firebaseDatabase;
    private String universityKey;
    private ImageView searchedUserProfilePicture;
    private TextView searchedUserProfileUsername;
    private TextView searchedUserProfileEmail;
    private TextView searchedUserProfileUniversity;
    private TextView searchedUserProfileUserRole;
    private RecyclerView recyclerViewTags;
    private RecyclerView recyclerViewUserPostsPreview;
    private UserPostAdapter userPostAdapter;
    private List<Tag> tagList;

    private TagSelectAdapter tagSelectAdapter;
    private User currentUser;
    private User searchedUser;
    private List<String> userPostUri;


    private void getUserPosts(String email) {

        String universityName = currentUser.getUniversity();
        userPostUri = new ArrayList<>();
        if (universityName == null) {
            Toast.makeText(getContext(), "Error! Please go back!", Toast.LENGTH_SHORT).show();
            return;
        }
        universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postReference = universityReference.child(universityName).child("posts");

                postReference.orderByChild("userId").equalTo(email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            userPostUri.add(dataSnapshot.child("imageUrl").getValue(String.class));
                        }
//                        userPostAdapter = new UserPostAdapter(getContext(), userPostUri, recyclerViewUserPostsPreview, new UserPostAdapter.ClickListener() {
//                            @Override
//                            public void onPostClick(int position) {
//
//                            }
//                        });
//                        recyclerViewUserPostsPreview.setAdapter(userPostAdapter);
//                        userPostAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                recyclerViewUserPostsPreview.setAdapter(userPostAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

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
        String universityName = currentUser.getUniversity();
        if (universityName == null) {
            Toast.makeText(getContext(), "Error! Please go back!", Toast.LENGTH_SHORT).show();
            return;
        }
        universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = currentUser.getEmailID();
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
        Bundle arguments = getArguments();
        searchedUser = (User) arguments.getSerializable("Searched User");
        SharedPreferences preferences = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
        currentUser = getUserDataFromSharedPreferences(preferences);
        authService = AuthService.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        this.firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));
        universityReference = firebaseDatabase.getReference();

        String email = searchedUser.getEmailID();
        StorageReference imageRef = storageRef.child("/" + currentUser.getUniversity() + "/Users/" + email + "/searchedUserProfile_picture/searchedUserProfile_picture.jpg");
        loadTagList();
        getUserPosts(email);

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri.toString())
                    .resize(400, 400)
                    .centerCrop()
                    .into(searchedUserProfilePicture);


        }).addOnFailureListener(error -> {

        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_searched_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewTags = view.findViewById(R.id.recyclerViewsearchedUserProfileTags);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
        recyclerViewTags.setLayoutManager(layoutManager);

//        recyclerViewUserPostsPreview = view.findViewById(R.id.searchedUserPostsRecyclerView);
//        recyclerViewUserPostsPreview.setLayoutManager(new GridLayoutManager(getContext(), 4));
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(requireContext());
        layoutManager2.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
//        recyclerViewUserPostsPreview.setLayoutManager(layoutManager2);

        searchedUserProfilePicture = view.findViewById(R.id.searchedUserProfilePicture);
        navController = Navigation.findNavController(view);
        searchedUserProfileEmail = view.findViewById(R.id.searchedUserProfileEmail);
        searchedUserProfileUniversity = view.findViewById(R.id.searchedUserProfileUniversity);
        searchedUserProfileUserRole = view.findViewById(R.id.searchedUserProfileUserRole);
        searchedUserProfileUsername = view.findViewById(R.id.searchedUserProfileUsername);
        String university = searchedUser.getUniversity();
        String username = searchedUser.getName();
        String email = searchedUser.getEmailID();
        String userRole = searchedUser.getUserRole();
        String searchedUserProfPic = searchedUser.getProfilePicture();
        searchedUserProfileUniversity.setText(university);
        searchedUserProfileUsername.setText(username);
        searchedUserProfileEmail.setText(email);
        searchedUserProfileUserRole.setText(userRole);
        Picasso.get()
                .load(searchedUserProfPic)
                .resize(400, 400)
                .centerCrop()
                .into(searchedUserProfilePicture);


    }


}