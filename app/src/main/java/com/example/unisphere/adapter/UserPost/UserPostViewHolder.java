package com.example.unisphere.adapter.UserPost;


import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;



public class UserPostViewHolder extends RecyclerView.ViewHolder {

    ImageView userPostPreview;

    public UserPostViewHolder(@NonNull View itemView) {

        super(itemView);
        this.userPostPreview = itemView.findViewById(R.id.userPostPreview);
    }

    void bind(Uri uri) {
        userPostPreview.setImageURI(uri);


    }

}
