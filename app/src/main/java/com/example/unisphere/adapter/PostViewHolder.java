package com.example.unisphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Comment;
import com.example.unisphere.model.Post;

import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    CardView cardView;
    TextView description;
    ImageView postImage;

    TextView likeCount;

    TextView commentCount;

    Context context;
    List<Post> postList;

    ImageView likeIcon;




    public PostViewHolder(@NonNull View itemView, Context context, List<Post> postList) {
        super(itemView);
        this.context = context;
        this.cardView = itemView.findViewById(R.id.post_list_container);
        this.postImage =  itemView.findViewById(R.id.imageView_post);
        this.description = itemView.findViewById(R.id.textView_post_description);
        this.likeCount = itemView.findViewById(R.id.like_count);
        this.commentCount = itemView.findViewById(R.id.comment_count);
        this.likeIcon = itemView.findViewById(R.id.like_icon);
        this.postList = postList;
        this.description.setTextColor(ContextCompat.getColor(context, android.R.color.black)); // Change text color to black

        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Post post = postList.get(position);
                List<String> likedByUserIds = post.getLikedByUserIds();

                // TODO: FETCH USER ID FROM SHARED PREFERENCES
                String currentUserId = "test@northeastern.edu";

                // Toggle like status
                if (likedByUserIds.contains(currentUserId)) {
                    // User already liked the post, remove like
                    likedByUserIds.remove(currentUserId);
                } else {
                    // User has not liked the post, add like
                    likedByUserIds.add(currentUserId);
                }

                // Update like count
                int likes = likedByUserIds.size();
                likeCount.setText(String.valueOf(likes));

                // Change like button icon based on like status
                if (likedByUserIds.contains(currentUserId)) {
                    likeIcon.setImageResource(R.drawable.ic_like_filled_foreground);
                } else {
                    likeIcon.setImageResource(R.drawable.ic_like_foreground);
                }


                // TODO: Implement code to update like status in firebase
            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}
