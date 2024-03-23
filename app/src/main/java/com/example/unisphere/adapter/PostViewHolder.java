package com.example.unisphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Comment;
import com.example.unisphere.model.Post;
import com.example.unisphere.ui.home.PostDetailsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder  {

    CardView cardView;
    TextView description;
    ImageView postImage;
    TextView likeCount;
    TextView commentCount;
    Context context;
    List<Post> postList;
    ImageView likeIcon;


    // TODO: Fetch current user ID from SharedPreferences

    String currentUserId = "test@northeastern.edu";
    String university="northeastern";

    public PostViewHolder(@NonNull View itemView, Context context, List<Post> postList, PostAdapter.ClickListener clickListener) {
        super(itemView);
        this.context = context;
        this.cardView = itemView.findViewById(R.id.post_list_container);
        this.postImage =  itemView.findViewById(R.id.imageView_post);
        this.description = itemView.findViewById(R.id.textView_post_description);
        this.likeCount = itemView.findViewById(R.id.like_count);
        this.commentCount = itemView.findViewById(R.id.comment_count);
        this.likeIcon = itemView.findViewById(R.id.like_icon);
        this.postList = postList;

        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && clickListener != null) {
                clickListener.onPostClick(position);
            }
        });
        this.description.setTextColor(ContextCompat.getColor(context, android.R.color.black)); // Change text color to black
       // itemView.setOnClickListener(this);
        likeIcon.setOnClickListener(v -> {
            int position = getAdapterPosition();
            Post post = postList.get(position);
            toggleLike(post);
        });

    }


    private void toggleLike(Post post) {


        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child(university).child("posts").child(post.getKeyFirebase());

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> likedByUserIds = post.getLikedByUserIds();
                if (likedByUserIds.contains(currentUserId)) {
                    // User already liked the post, remove like
                    likedByUserIds.remove(currentUserId);
                } else {
                    // User has not liked the post, add like
                    likedByUserIds.add(currentUserId);
                }

                // Update the like count text view
                likeCount.setText(String.valueOf(likedByUserIds.size()));

                // Update the like icon based on the like status
                if (likedByUserIds.contains(currentUserId)) {
                    likeIcon.setImageResource(R.drawable.ic_like_filled_foreground);
                } else {
                    likeIcon.setImageResource(R.drawable.ic_like_foreground);
                }

                // Update the likedByUserIds field in the database
                postRef.child("likedByUserIds").setValue(likedByUserIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

}
