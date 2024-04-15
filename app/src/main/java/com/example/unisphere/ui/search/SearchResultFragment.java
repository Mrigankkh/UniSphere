package com.example.unisphere.ui.search;

import static android.content.Context.MODE_PRIVATE;
import static com.example.unisphere.service.Util.USER_DATA;
import static com.example.unisphere.service.Util.getUserDataFromSharedPreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.searchResult.SearchResultAdapter;
import com.example.unisphere.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class SearchResultFragment extends Fragment {


    List<User> searchedUsers;
    FirebaseStorage storage;
    StorageReference storageRef;
    private NavController navController;
    private RecyclerView searchResultsRecyclerView;
    private ArrayList<String> tempUserEmails;
    private ArrayList<String> searchedUserKeys;
    private SearchResultAdapter searchResultAdapter;
    private DatabaseReference universityReference;
    private FirebaseDatabase firebaseDatabase;
    private User currentUser;


    public SearchResultFragment() {
        // Required empty public constructor
    }

    private void getUniversityKey() {

        universityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String universityName = currentUser.getUniversity();
                universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String universityKey = snapshot.getChildren().iterator().next().getKey();
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
        this.firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));
        SharedPreferences preferences = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
        currentUser = getUserDataFromSharedPreferences(preferences);
        universityReference = firebaseDatabase.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        getUniversityKey();// This is an async method and might cause null ptr issues.
        Bundle arguments = getArguments();

        searchedUserKeys = (ArrayList<String>) arguments.getSerializable("search_results");

        if (searchedUserKeys == null) {
            searchedUserKeys = new ArrayList<>();
        }
        getSearchedUsers(searchedUserKeys);
    }

    private void getSearchedUsers(ArrayList<String> searchedUserKeys) {
        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        universityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String universityName = currentUser.getUniversity();
                if (universityName == null) {
                    // Handle missing university name
                    return;
                }

                universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            // Handle missing university data
                            return;
                        }

                        String universityKey = snapshot.getChildren().iterator().next().getKey();
                        for (String searchedUserKey : searchedUserKeys) {
                            if (searchedUserKey == null) {
                                continue;
                            }

                            Task<DataSnapshot> userTask = universityReference.child(universityKey).child("users").child(searchedUserKey).get();
                            tasks.add(userTask);
                        }

                        Tasks.whenAll(tasks).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                // Handle errors fetching users
                                return;
                            }

                            for (Task<DataSnapshot> userTask : tasks) {
                                if (userTask.isSuccessful()) {
                                    DataSnapshot userSnapshot = userTask.getResult();
                                    if (userSnapshot.exists()) {
                                        String searchedUserName = userSnapshot.child("name").getValue(String.class);
                                        String searchedUserEmail = userSnapshot.child("emailID").getValue(String.class);
                                        String searchedUserRole = userSnapshot.child("userRole").getValue(String.class);
                                        String searchedUserUniversity = userSnapshot.child("university").getValue(String.class);

                                        StorageReference imageRef = storageRef.child("/" + currentUser.getUniversity() + "/Users/" + searchedUserEmail + "/profile_picture/profile_picture.jpg");
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            User searchedUser = new User(searchedUserName, searchedUserEmail, uri.toString(), null, searchedUserRole, searchedUserUniversity);
                                            if (searchedUser != null) {
                                                searchedUsers.add(searchedUser);
                                                searchResultAdapter.notifyDataSetChanged();  // Notify the adapter in a better way

                                            } else {
                                                System.out.println("User was null");
                                            }

//                                            searchResultAdapter = new SearchResultAdapter(requireContext(), searchedUsers, new SearchResultAdapter.ClickListener() {
//                                                @Override
//                                                public void onSearchResultClick(int position) {
//                                                    // Implement click handling
//                                                }
//                                            });
//                                            searchResultsRecyclerView.setAdapter(searchResultAdapter);

                                        }).addOnFailureListener(error -> {
                                            //searchedUsers.add(new User(searchedUserName, Uri.EMPTY, searchedUserEmail));
                                        });
                                    } else {
                                        // Handle missing user data
                                    }
                                }
                            }


                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error getting university data
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error getting universities
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.searchResultsRecyclerView = view.findViewById(R.id.recyclerView_search_results);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        searchedUsers = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(requireContext(), searchedUsers, this::onUserClick);
        searchResultsRecyclerView.setAdapter(searchResultAdapter);

    }

    private void onUserClick(int position) {
        Toast.makeText(this.getContext(), "User was pressed", Toast.LENGTH_SHORT).show();
    }


}