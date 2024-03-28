package com.example.unisphere.ui.search;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unisphere.R;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Tag;
import com.example.unisphere.model.User;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import java.util.List;

import java.util.stream.Collectors;


public class SearchFragment extends Fragment {

    private List<Tag> tagList;

    private TagSelectAdapter tagSelectAdapter;
    private SharedPreferences preferences;
    private NavController navController;

    FirebaseStorage storage;
    private DatabaseReference universityReference;
    private DatabaseReference tagReference;
    private FirebaseDatabase firebaseDatabase;
    private String universityKey;
    private RecyclerView recyclerViewTags;
    private Button searchButton;
    private EditText searchByNameInput;
    private Bundle bundle = new Bundle();

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
     * Get the list of predefined tags offered by the university.
     */
    public void loadTagList() {
        String universityName = preferences.getString("university", null);
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


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
        universityReference = firebaseDatabase.getReference();
        loadTagList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        searchButton = view.findViewById(R.id.searchBtn);
        recyclerViewTags = view.findViewById(R.id.recyclerViewSearchByTag);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexWrap(FlexWrap.WRAP); // Enable line wrapping
        recyclerViewTags.setLayoutManager(layoutManager);
        searchByNameInput = view.findViewById(R.id.searchByNameInput);
        searchButton.setOnClickListener(v -> search());

    }

    private void search() {
        List<String> selectedTags = tagSelectAdapter.getSelectedTags().stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
        String name = searchByNameInput.getText().toString().trim();
        if (selectedTags.size() == 0 && name.isEmpty()) {
            Toast.makeText(this.getContext(), "You must enter a search criteria!", Toast.LENGTH_SHORT).show();
            return;
        }

        universityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String universityName = preferences.getString("university", null);
                List<User> userSearchResults = new ArrayList<>();
                List<String> userSearchResultsEmail = new ArrayList<>();
                universityKey = (String) snapshot.getChildren().iterator().next().getKey();

                universityReference.orderByChild("name").equalTo(universityName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        universityKey = (String) snapshot.getChildren().iterator().next().getKey();
                        tagReference = universityReference.child(universityKey).child("tags");
                        tagReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> allUsers = new ArrayList<>();
                                boolean isFirstTag = true;
                                for (DataSnapshot tagSnapshot : snapshot.getChildren()) {
                                    String tagName = (String) tagSnapshot.child("name").getValue();
                                    if (selectedTags.contains(tagName)) {
                                        List<String> users = (List<String>) tagSnapshot.child("users").getValue();
                                        if (isFirstTag) {
                                            allUsers.addAll(users);
                                            isFirstTag = false;
                                        } else {
                                            if (!allUsers.isEmpty()) {
                                                allUsers.retainAll(users);
                                            }
                                        }
                                    }
                                }


                                bundle.putSerializable("search_results", allUsers);
                                navController.navigate(R.id.action_navigation_search_to_search_results, bundle);

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
        }}