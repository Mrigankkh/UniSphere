package com.example.unisphere.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Post;
import com.example.unisphere.model.User;
import com.example.unisphere.service.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    Context context;
    List<Post> list;
    View rootView;

    private ClickListener clickListener;

    String currentUserId;
    String university;

    SharedPreferences sharedPreferences;

    public PostAdapter(Context context, List<Post> list, View rootView, ClickListener clickListener) {
        this.context = context;
        this.list = list;
        this.rootView = rootView;
        this.clickListener = clickListener;
        sharedPreferences = context.getSharedPreferences("USER_DATA", MODE_PRIVATE);
        User userDataPreferences = Util.getUserDataFromSharedPreferences(sharedPreferences);
        this.currentUserId = userDataPreferences.getEmailID();
        this.university = userDataPreferences.getUniversity();
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
        holder.usernameText.setText(postItem.getUserId());
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