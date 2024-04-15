package com.example.unisphere.ui.profile;

import static android.content.Context.MODE_PRIVATE;
import static com.example.unisphere.service.Util.USER_DATA;
import static com.example.unisphere.service.Util.getUserDataFromSharedPreferences;

import android.content.SharedPreferences;
import android.graphics.Color;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.MainActivity;
import com.example.unisphere.R;
import com.example.unisphere.adapter.UserPost.UserPostAdapter;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Post;
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


public class ProfileFragment extends Fragment {


    AuthService authService;
    StorageReference storageRef;
    String userKey;
    List<Post> postList;
    UserPostAdapter userPostAdapter;
    String universityKey;
    private Button tempLogout;
    private NavController navController;
    private DatabaseReference postDatabaseReference;
    private DatabaseReference universityReference;
    private DatabaseReference tagReference;
    private DatabaseReference userReference;
    private FirebaseDatabase firebaseDatabase;
    private User currentUser;
    private ImageView profilePicture;
    private TextView profileUsername;
    private TextView profileEmail;
    private TextView profileUniversity;
    private TextView profileUserRole;
    private RecyclerView recyclerViewTags;
    private List<Tag> tagList;
    private Button editProfileBtn;
    private RecyclerView recyclerViewUserPosts;
    private TagSelectAdapter tagSelectAdapter;
    private String email;

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
                userReference = universityReference.child(currentUser.getUniversity()).child("users");

                userReference.orderByChild("emailID").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userKey = snapshot.getChildren().iterator().next().getKey();

                        tagReference = userReference.child(userKey).child("userTags");
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void loadUserPosts() {
        retrievePostsFromFirebase();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
        currentUser = getUserDataFromSharedPreferences(preferences);
        authService = AuthService.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        this.firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));
        universityReference = firebaseDatabase.getReference();
        this.universityKey = currentUser.getUniversity();
        postDatabaseReference = firebaseDatabase.getReference().child(universityKey).child(getString(R.string.posts));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        postList = new ArrayList<>();

        recyclerViewTags = profileView.findViewById(R.id.recyclerViewProfileTags);
        recyclerViewUserPosts = profileView.findViewById(R.id.userPostPreview);

        userPostAdapter = new UserPostAdapter(postList, recyclerViewUserPosts);
        recyclerViewUserPosts.setAdapter(userPostAdapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
        recyclerViewTags.setLayoutManager(layoutManager);
        recyclerViewUserPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        tagList = new ArrayList<>();
        tagSelectAdapter = new TagSelectAdapter(tagList, false, recyclerViewTags, Color.WHITE);
        recyclerViewTags.setAdapter(tagSelectAdapter);
        tempLogout = profileView.findViewById(R.id.tempLogout);

        profilePicture = profileView.findViewById(R.id.profilePicture);
        tempLogout.setOnClickListener(View -> logOut());
        profileEmail = profileView.findViewById(R.id.profileEmail);
        profileUniversity = profileView.findViewById(R.id.profileUniversity);
        profileUserRole = profileView.findViewById(R.id.profileUserRole);
        profileUsername = profileView.findViewById(R.id.profileUsername);
        String university = currentUser.getUniversity();
        String username = currentUser.getName();
        String email = currentUser.getEmailID();
        String userRole = currentUser.getUserRole();

        profileUniversity.setText(university);
        profileUsername.setText(username);
        profileEmail.setText(email);
        profileUserRole.setText(userRole);

        editProfileBtn = profileView.findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_navigation_profile_to_editProfileFragment);
            }
        });
        loadUserPosts();
        loadTagList();

        StorageReference imageRef = storageRef.child("/" + currentUser.getUniversity() + "/Users/" + email + "/profile_picture/profile_picture.jpg");

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri.toString()).resize(400, 400).centerCrop().into(profilePicture);


        }).addOnFailureListener(error -> {

        });
        return profileView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

    }

    public void retrievePostsFromFirebase() {
        postDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.userId.equals(currentUser.getEmailID())) {
                        posts.add(post);
                    }
                }

                // Assuming postList is a member variable of your class
                postList.clear();
                postList.addAll(posts);
                userPostAdapter.notifyDataSetChanged();
                // Notify the adapter of the data change

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void logOut() {

        SharedPreferences preferences = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
        preferences.edit().clear().apply();
        authService.signOut();
        navController.clearBackStack(R.id.activity_login);
        navController.navigate(R.id.action_navigation_profile_to_activity_login);
        getActivity().finish();

        ((MainActivity) requireActivity()).stopNotificationService();


    }
}