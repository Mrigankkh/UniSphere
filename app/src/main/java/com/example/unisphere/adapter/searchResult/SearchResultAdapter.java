package com.example.unisphere.adapter.searchResult;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultViewHolder> implements View.OnClickListener {
    Context context;
    List<User> searchedUsers;
    View rootView;

    private final ClickListener clickListener;

    public SearchResultAdapter(Context context, List<User> searchedUsers, ClickListener clickListener) {
        this.context = context;
        this.searchedUsers = searchedUsers;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchResultViewHolder(LayoutInflater.from(context).inflate(R.layout.search_result_row_item, parent, false), context, searchedUsers, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        User currentSearchedUser = searchedUsers.get(holder.getAdapterPosition());
        holder.searchedUserName.setText(currentSearchedUser.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Searched User", currentSearchedUser);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_search_results_to_searchedUsersearchedUserProfileFragment, bundle);
            }
        });
        Picasso.get()
                .load(currentSearchedUser.getProfilePicture())
                .resize(400, 400)
                .centerCrop()
                .into(holder.searchedUserProfilePicture);
    }

    @Override
    public int getItemCount() {
        return searchedUsers.size();
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
//        bundle.putParcelable("Searched User", searchedUsers.get(p) );
        NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.action_search_results_to_searchedUsersearchedUserProfileFragment, bundle);

    }

    //TODO: Review this listener
    public interface ClickListener {
        void onSearchResultClick(int position);
    }
}
