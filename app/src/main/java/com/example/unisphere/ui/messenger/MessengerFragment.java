package com.example.unisphere.ui.messenger;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessengerFragment extends Fragment implements UsersAdapter.OnUserClickListener{

    private static final String TAG = "MessengerFragment";
    private RecyclerView usersRecyclerView, searchResultsRecyclerView;
    private SharedPreferences sharedPreferences;
    private DatabaseReference chatsReference, usersReference;
    private ChatSessionAdapter adapter;
    private UsersAdapter searchResultsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direct_message_search, container, false);
        usersRecyclerView = view.findViewById(R.id.user_search_results_recycler_view);
        sharedPreferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        usersRecyclerView = view.findViewById(R.id.user_search_results_recycler_view);
        searchResultsRecyclerView = view.findViewById(R.id.search_results_recycler_view);
        SearchView searchView = view.findViewById(R.id.search_view);
        String currentUserOrganization = sharedPreferences.getString("university", "");
        chatsReference = FirebaseDatabase.getInstance().getReference("chats");

        setupRecyclerView();
        setupSearchResultsRecyclerView();
        loadChatSessions();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query, currentUserOrganization);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    searchUsers(newText, currentUserOrganization);
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                    usersRecyclerView.setVisibility(View.GONE);
                } else {
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    usersRecyclerView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatSessionAdapter(new ArrayList<>());
        usersRecyclerView.setAdapter(adapter);
    }

    private void setupSearchResultsRecyclerView() {
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsAdapter = new UsersAdapter(new ArrayList<>(), this);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
    }

    @Override
    public void onUserClicked(UserModel user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("CHAT_PARTNER_EMAIL", user.getEmailID());
        intent.putExtra("CHAT_PARTNER_NAME", user.getName());
        intent.putExtra("CHAT_PARTNER_IMAGE_URL", user.getProfilePictureURL());
        startActivity(intent);
    }

    private void loadChatSessions() {
        String loggedInUserEmail = sharedPreferences.getString("email", "");
        if (!loggedInUserEmail.isEmpty()) {
            Log.d(TAG, "Logged in user: " + loggedInUserEmail);
            ValueEventListener chatSessionListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Set<String> chatPartnersSet = new HashSet<>();
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot userSnapshot : sessionSnapshot.getChildren()) {
                            String senderEmail = userSnapshot.child("senderEmail").getValue(String.class);
                            String recipientEmail = userSnapshot.child("recipientEmail").getValue(String.class);
                            if (loggedInUserEmail.equals(senderEmail) && recipientEmail != null && !recipientEmail.equals(loggedInUserEmail)) {
                                chatPartnersSet.add(recipientEmail);
                                Log.d(TAG, "Recipient: " + recipientEmail);
                            } else if (loggedInUserEmail.equals(recipientEmail) && senderEmail != null && !senderEmail.equals(loggedInUserEmail)) {
                                chatPartnersSet.add(senderEmail);
                                Log.d(TAG, "Sender: " + senderEmail);
                            }
                        }
                    }
                    List<String> chatPartnersList = new ArrayList<>(chatPartnersSet);
                    adapter.updateData(chatPartnersList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadChat:onCancelled", databaseError.toException());
                }
            };
            chatsReference.addListenerForSingleValueEvent(chatSessionListener);
        } else {
            Log.w(TAG, "No logged in user email found.");
        }
    }

    private void searchUsers(String searchText, String currentUserOrganization) {
        if (searchText == null || searchText.isEmpty()) {
            searchResultsAdapter.updateData(new ArrayList<>());
            searchResultsRecyclerView.setVisibility(View.GONE);
            usersRecyclerView.setVisibility(View.VISIBLE);
            return;
        }

        usersReference = FirebaseDatabase.getInstance().getReference(currentUserOrganization + "/users");
        usersReference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<UserModel> searchResults = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            if (user != null && user.getUniversity().equals(currentUserOrganization)) {
                                searchResults.add(user);
                            }
                        }
                        searchResultsAdapter.updateData(searchResults);
                        if (!searchResults.isEmpty()) {
                            searchResultsRecyclerView.setVisibility(View.VISIBLE);
                            usersRecyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "searchUsers:onCancelled", databaseError.toException());
                    }
                });
    }

}
