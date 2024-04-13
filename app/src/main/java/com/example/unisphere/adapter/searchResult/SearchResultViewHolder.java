package com.example.unisphere.adapter.searchResult;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;

import com.example.unisphere.model.User;

import java.util.List;

public class SearchResultViewHolder extends RecyclerView.ViewHolder  {

    ImageView searchedUserProfilePicture;
    TextView searchedUserName;
    Context context;
    List<User> searchedUsers;

    public SearchResultViewHolder(@NonNull View itemView, Context context, List<User> searchedUsers, SearchResultAdapter.ClickListener clickListener) {
        super(itemView);
        this.context = context;
        this.searchedUserProfilePicture = itemView.findViewById(R.id.profilePicture);
        this.searchedUserName = itemView.findViewById(R.id.searchedUserName);
        this.searchedUsers = searchedUsers;
        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && clickListener != null) {
                clickListener.onSearchResultClick(position);
            }
        });
    }

}
