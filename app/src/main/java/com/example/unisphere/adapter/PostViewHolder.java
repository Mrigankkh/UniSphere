package com.example.unisphere.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Post;
import com.example.unisphere.model.User;
import com.example.unisphere.service.Util;
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

    TextView usernameText;
    Context context;
    List<Post> postList;
    ImageView likeIcon;


    String currentUserId;
    String university;

    SharedPreferences sharedPreferences;

    public PostViewHolder(@NonNull View itemView, Context context, List<Post> postList, PostAdapter.ClickListener clickListener) {
        super(itemView);
        this.context = context;

        sharedPreferences = context.getSharedPreferences("USER_DATA", MODE_PRIVATE);

        User userDataPreferences = Util.getUserDataFromSharedPreferences(sharedPreferences);
        this.currentUserId = userDataPreferences.getEmailID();
        this.university = userDataPreferences.getUniversity();


        this.cardView = itemView.findViewById(R.id.post_list_container);
        this.usernameText = itemView.findViewById(R.id.textView_username);
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
                    postRef.child("latestLikeAction").setValue("unliked");
                    likedByUserIds.remove(currentUserId);
                } else {
                    // User has not liked the post, add like
                    postRef.child("latestLikeAction").setValue("liked");
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
