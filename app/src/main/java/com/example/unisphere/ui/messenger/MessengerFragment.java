package com.example.unisphere.ui.messenger;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;

public class MessengerFragment extends Fragment {

    private RecyclerView usersRecyclerView;
    private SharedPreferences sharedPreferences;
    private DatabaseReference chatsReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direct_message_search, container, false);
        usersRecyclerView = view.findViewById(R.id.user_search_results_recycler_view);
        sharedPreferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        chatsReference = FirebaseDatabase.getInstance().getReference("chats");

        setupRecyclerView();
        loadChatSessions();

        return view;
    }

    private void setupRecyclerView() {
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ChatSessionAdapter adapter = new ChatSessionAdapter(new ArrayList<>());
        usersRecyclerView.setAdapter(adapter);
    }

    private void loadChatSessions() {
        String loggedInUserEmail = sharedPreferences.getString("email", "");
        if (!loggedInUserEmail.isEmpty()) {
            Log.d("ChatPartner", "Logged: " + loggedInUserEmail);
            ValueEventListener chatSessionListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> chatPartners = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String sessionKey = childSnapshot.getKey();
                            if (sessionKey != null && sessionKey.contains(loggedInUserEmail.replace(".", ","))) {
                                String partnerEmail = sessionKey.replace(loggedInUserEmail.replace(".", ","), "")
                                        .replace("_", "").replace(",", ".");
                                chatPartners.add(partnerEmail);
                                Log.d("ChatPartner", "Chat with: " + partnerEmail);
                            }
                        }
                    }
                    ChatSessionAdapter adapter = (ChatSessionAdapter) usersRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.updateData(chatPartners);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("loadChat:onCancelled", databaseError.toException());
                }
            };
            chatsReference.addListenerForSingleValueEvent(chatSessionListener);
        }
    }
}
