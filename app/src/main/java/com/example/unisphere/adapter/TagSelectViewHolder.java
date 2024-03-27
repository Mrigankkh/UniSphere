package com.example.unisphere.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Tag;
import com.google.android.gms.tasks.OnCompleteListener;

public class TagSelectViewHolder extends RecyclerView.ViewHolder  {
    TextView textViewTag;

    TagSelectViewHolder(View itemView) {
        super(itemView);
        textViewTag = itemView.findViewById(R.id.tagName);
    }

    void bind(Tag tag) {
        textViewTag.setText(tag.getTagName());


    }


}

    // convenience method for getting data at click position
