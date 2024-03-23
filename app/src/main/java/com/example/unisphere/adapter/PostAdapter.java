package com.example.unisphere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    Context context;
    List<Post> list;
    View rootView;

    private ClickListener clickListener;


    // TODO: Fetch current user ID from SharedPreferences

    String currentUserId = "test@northeastern.edu";
    String university="northeastern";

    public PostAdapter(Context context, List<Post> list, View rootView, ClickListener clickListener) {
        this.context = context;
        this.list = list;
        this.rootView = rootView;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_post_element, parent, false), context, list,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post postItem = list.get(position);
        holder.likeCount.setText(String.valueOf(postItem.getLikedByUserIds().size()));
        holder.commentCount.setText(String.valueOf(postItem.getComments().size()));
        holder.description.setText(postItem.getDescription());
        Picasso.get().load(postItem.getImageUrl()).into(holder.postImage);

        if (postItem.getLikedByUserIds().contains(currentUserId)) {
            holder.likeIcon.setImageResource(R.drawable.ic_like_filled_foreground);
        } else {
            holder.likeIcon.setImageResource(R.drawable.ic_like_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ClickListener {
        void onPostClick(int position);
    }


}