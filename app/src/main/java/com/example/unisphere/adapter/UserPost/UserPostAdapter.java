package com.example.unisphere.adapter.UserPost;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostViewHolder> {


    private final List<Post> userPostUriList;

    private final RecyclerView recyclerView;

    public UserPostAdapter(List<Post> userPostUriList, RecyclerView recyclerView) {

        this.userPostUriList = userPostUriList;

        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_item_view, parent, false);
        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {

        Post postItem = userPostUriList.get(position);

        Picasso.get()
                .load(userPostUriList.get(position).getImageUrl())
                .resize(120, 120)  // Resize to 150dp width and height
                // Crop the image to fit the ImageView while maintaining aspect ratio
                .into(holder.userPostPreview);

    }

    @Override
    public int getItemCount() {
        return userPostUriList.size();
    }


}
