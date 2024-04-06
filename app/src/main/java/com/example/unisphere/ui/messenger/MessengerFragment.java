package com.example.unisphere.ui.messenger;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.UserSearchAdapter;
import com.example.unisphere.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessengerFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView usersRecyclerView;
    private UserSearchAdapter userSearchAdapter;
    private List<User> userList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private DatabaseReference usersReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direct_message_search, container, false);

        searchEditText = view.findViewById(R.id.search_user_name);
        usersRecyclerView = view.findViewById(R.id.user_search_results_recycler_view);
        view.findViewById(R.id.search_button).setOnClickListener(v -> performSearch());

        sharedPreferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchAdapter = new UserSearchAdapter(getContext(), userList, user -> {
        });
        usersRecyclerView.setAdapter(userSearchAdapter);
    }

    private void performSearch() {
        String searchText = searchEditText.getText().toString().trim();
        if (searchText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        String universityName = sharedPreferences.getString("university", "");
        if (universityName.isEmpty()) {
            Toast.makeText(getContext(), "University not found in preferences", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = usersReference.child(universityName).child("users");

        Query query = usersRef.orderByChild("name").equalTo(searchText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    }
                    if (userList.isEmpty()) {
                        Toast.makeText(getContext(), "No users found with the name: " + searchText, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Users found: " + userList.size(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No users found with the name: " + searchText, Toast.LENGTH_SHORT).show();
                }
                userSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
