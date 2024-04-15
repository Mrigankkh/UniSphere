package com.example.unisphere.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Comment;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView textViewComment;
    TextView textViewEmail;


    CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewComment = itemView.findViewById(R.id.textView_comment);
        textViewEmail = itemView.findViewById(R.id.textView_email);
    }

    void bind(Comment comment) {
        textViewComment.setText(comment.getText());
        textViewEmail.setText(comment.getUserId());

    }
}
