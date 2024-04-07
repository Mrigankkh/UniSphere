package com.example.unisphere.adapter.UserPost;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;

import java.util.List;

public class UserPostViewHolder extends RecyclerView.ViewHolder {

    private List<String> userPostUriList;
    private Context context;
     ImageView userPostPreview;

    public UserPostViewHolder(@NonNull View itemView, Context context, List<String> userPostUriList, UserPostAdapter.ClickListener clickListener) {
        super(itemView);
        this.context = context;
        this.userPostUriList = userPostUriList;
        this.userPostPreview = itemView.findViewById(R.id.userPostPreview);

//
//        itemView.setOnClickListener(v -> {
//            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION && clickListener != null) {
//                clickListener.onPostClick(position);
//            }
//        });


    }

}
