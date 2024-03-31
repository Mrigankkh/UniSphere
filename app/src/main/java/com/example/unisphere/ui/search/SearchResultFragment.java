package com.example.unisphere.ui.search;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unisphere.R;
import com.example.unisphere.adapter.EventAdapter;
import com.example.unisphere.adapter.searchResult.SearchResultAdapter;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Event;
import com.example.unisphere.model.SearchedUser;
import com.example.unisphere.model.Tag;
import com.example.unisphere.model.User;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
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
import java.util.concurrent.CountDownLatch;


public class SearchResultFragment extends Fragment {


    private NavController navController;
    private RecyclerView searchResultsRecyclerView;
    private ArrayList<String> tempUserEmails;
    private ArrayList<String> searchedUserKeys;
    private SearchResultAdapter searchResultAdapter;
    List<SearchedUser> searchedUsers;

    private DatabaseReference universityReference;
    private FirebaseDatabase firebaseDatabase;

    private SharedPreferences preferences;
    StorageReference storageRef;


    public SearchResultFragment() {
        // Required empty public constructor
    }

    private void getUniversityKey() {

        universityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String universityName = preferences.getString("university", null);
                universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String universityKey = (String) snapshot.getChildren().iterator().next().getKey();
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
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://unisphere-340ac-default-rtdb.firebaseio.com/");
        this.preferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        universityReference = firebaseDatabase.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        getUniversityKey();// This is an async method and might cause null ptr issues.
        Bundle arguments = getArguments();
        searchedUsers = new ArrayList<>();
        searchedUserKeys = (ArrayList<String>) arguments.getSerializable("search_results");

        if (searchedUserKeys == null) {
            searchedUserKeys = new ArrayList<>();
        }
      getSearchedUsers( searchedUserKeys);
    }
    private void getSearchedUsers(ArrayList<String> searchedUserKeys) {
        List<SearchedUser> searchedUsers = new ArrayList<>();
        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        universityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String universityName = preferences.getString("university", null);
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

                        String universityKey = (String) snapshot.getChildren().iterator().next().getKey();
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

                                        StorageReference imageRef = storageRef.child("/Northeastern University/Users/" + searchedUserEmail + "/profile_picture/profile_picture.jpg");
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                            searchedUsers.add(new SearchedUser(searchedUserName, uri));

                                            searchResultAdapter = new SearchResultAdapter(requireContext(), searchedUsers, new SearchResultAdapter.ClickListener() {
                                                @Override
                                                public void onSearchResultClick(int position) {
                                                    // Implement click handling
                                                }
                                            });
                                            searchResultsRecyclerView.setAdapter(searchResultAdapter);
                                            searchResultAdapter.notifyDataSetChanged();  // Notify the adapter in a better way

                                        }).addOnFailureListener(error -> {
                                            searchedUsers.add(new SearchedUser(searchedUserName, Uri.EMPTY));
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
//    private List<SearchedUser> getSearchedUsers( ArrayList<String> searchedUserKeys) {
//
//        List<SearchedUser> searchedUsers = new ArrayList<>();
//        final CountDownLatch latch = new CountDownLatch(searchedUserKeys.size());
//
//        universityReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String universityName = preferences.getString("university", null);
//                universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        String universityKey = (String) snapshot.getChildren().iterator().next().getKey();
//                        for (String searchedUserKey : searchedUserKeys) {
//                            if(searchedUserKey==null)
//                                continue;
//                            universityReference.child(universityKey).child("users").child(searchedUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    String searchedUserName = snapshot.child("name").getValue(String.class);
//                                    String searchedUserEmail = snapshot.child("emailID").getValue(String.class); // Change this later to userkey
//                                    StorageReference imageRef = storageRef.child("/Northeastern University/Users/" + searchedUserEmail + "/profile_picture/profile_picture.jpg");
//                                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                                        String imageUrl = uri.toString();
//                                        searchedUsers.add(new SearchedUser(searchedUserName, uri));
//
//                                    }).addOnFailureListener(error -> {
//                                        searchedUsers.add(new SearchedUser(searchedUserName, Uri.EMPTY));
//                                    });
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        }
//                        latch.countDown();
//                        searchResultAdapter = new SearchResultAdapter(requireContext(), searchedUsers, new SearchResultAdapter.ClickListener() {
//                            @Override
//                            public void onSearchResultClick(int position) {
//
//                            }
//                        });
//                        searchResultsRecyclerView.setAdapter(searchResultAdapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//
//        return searchedUsers;
//
//    }

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
        searchResultAdapter = new SearchResultAdapter(requireContext(), searchedUsers, this::onUserClick);
        searchResultsRecyclerView.setAdapter(searchResultAdapter);

    }

    private void onUserClick(int position) {
        Toast.makeText(this.getContext(), "User was pressed", Toast.LENGTH_SHORT).show();
    }


}