package com.example.unisphere.ui.messenger;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unisphere.R;
import com.example.unisphere.ui.messenger.UserModel;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private List<UserModel> userList;
    private LayoutInflater inflater;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClicked(UserModel user);
    }

    public UsersAdapter(List<UserModel> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = inflater.inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<UserModel> newUsers) {
        this.userList.clear();
        this.userList.addAll(newUsers);
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, userEmailTextView;

        public UserViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name);
            userEmailTextView = itemView.findViewById(R.id.user_email);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUserClicked((UserModel) itemView.getTag());
                    }
                }
            });
        }

        void bind(UserModel user) {
            userNameTextView.setText(user.getName());
            userEmailTextView.setText(user.getEmailID());
            itemView.setTag(user);
        }
    }
}
