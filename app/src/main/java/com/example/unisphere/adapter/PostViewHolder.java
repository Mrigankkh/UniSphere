package com.example.unisphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

    public PostViewHolder(@NonNull View itemView, Context context, List<Post> postList) {
        super(itemView);
        this.context = context;
        this.cardView = itemView.findViewById(R.id.post_list_container);
        this.postImage =  itemView.findViewById(R.id.imageView_post);
        this.description = itemView.findViewById(R.id.textView_post_description);
        this.likeCount = itemView.findViewById(R.id.like_count);
        this.commentCount = itemView.findViewById(R.id.comment_count);
        this.postList = postList;

    }

    @Override
    public void onClick(View v) {
        // TODO: implement action here
    }
}
