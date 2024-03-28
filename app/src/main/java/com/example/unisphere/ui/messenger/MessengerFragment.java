package com.example.unisphere.ui.messenger;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unisphere.R;
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

    private EditText searchBar;
    private RecyclerView usersRecyclerView;
    private DatabaseReference usersDatabaseReference;
    private List<User> userList = new ArrayList<>();

    public MessengerFragment() {
    }

    public static MessengerFragment newInstance(String param1, String param2) {
        MessengerFragment fragment = new MessengerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger, container, false);
        searchBar = view.findViewById(R.id.search_bar);
        usersRecyclerView = view.findViewById(R.id.search_results_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final UserSearchInnerAdapter userSearchAdapter = new UserSearchInnerAdapter(userList);
        usersRecyclerView.setAdapter(userSearchAdapter);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                if (!searchText.isEmpty()) {
                    performSearch(searchText, userSearchAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void performSearch(String searchText, final UserSearchInnerAdapter adapter) {
        Query searchQuery = usersDatabaseReference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private static class UserSearchInnerAdapter extends RecyclerView.Adapter<UserSearchInnerAdapter.UserViewHolder> {

        private List<User> userList;

        public UserSearchInnerAdapter(List<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.userNameTextView.setText(user.getName());
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        static class UserViewHolder extends RecyclerView.ViewHolder {

            TextView userNameTextView;

            UserViewHolder(View itemView) {
                super(itemView);
                userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            }
        }
    }
}
