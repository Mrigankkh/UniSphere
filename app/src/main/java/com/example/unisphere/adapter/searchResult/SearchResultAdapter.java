package com.example.unisphere.adapter.searchResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.unisphere.R;
import com.example.unisphere.adapter.EventViewHolder;
import com.example.unisphere.model.User;
import com.example.unisphere.service.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {
    Context context;
    List<User> users;
    View rootView;

    private ClickListener clickListener;

    public SearchResultAdapter(Context context, List<User> users, View rootView, ClickListener clickListener) {
        this.context = context;
        this.users = users;
        this.rootView = rootView;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchResultViewHolder(LayoutInflater.from(context).inflate(R.layout.search_result_row_item, parent, false), context, users, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        User user = users.get(position);
        holder.searchedUserName.setText(user.getName());
        Picasso.get()
                .load(R.drawable.no_events)
                .resize(400, 400)
                .centerCrop()
                .into(holder.searchedUserProfilePicture);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    //TODO: Review this listener
    public interface ClickListener {
        void onSearchResultClick(int position);
    }
}
