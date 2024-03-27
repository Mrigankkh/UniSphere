package com.example.unisphere.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Tag;

import java.util.List;

public class TagSelectAdapter extends RecyclerView.Adapter<TagSelectViewHolder> implements View.OnClickListener {

    private List<Tag> tagList;
    private RecyclerView recyclerView;

    // data is passed into the constructor

    public TagSelectAdapter(List<Tag> tagList, RecyclerView recyclerView) {
        this.tagList = tagList;
        this.recyclerView = recyclerView;
    }

    // inflates the cell layout from xml when needed
    @NonNull
    @Override
    public TagSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item_view, parent, false);
        return new TagSelectViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull TagSelectViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.itemView.setOnClickListener(this);  // Set click listener on the entire item view
        holder.bind(tag);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return tagList.size();
    }
    @Override
    public void onClick(View v) {
        int clickedPosition = recyclerView.getChildAdapterPosition(v);  // Get clicked item position
        if (clickedPosition != RecyclerView.NO_POSITION) {
//            if(( (ColorDrawable)v.getBackground()).getColor() ==  Color.parseColor("#DDDDDD"))
            v.setBackgroundColor(Color.parseColor("#5D3FD3")); // Set clicked color (red here)

            // Update background color based on clicked state (store a flag or use another mechanism)
//            if ((clickedPosition)) {
//                v.setBackgroundColor(Color.parseColor("#FF0000")); // Set clicked color (red here)
//            } else {
//                v.setBackgroundColor(Color.parseColor("#FFFFFF")); // Set default color (white here)
//            }
        }
    }
}


// stores and recycles views as they are scrolled off screen
