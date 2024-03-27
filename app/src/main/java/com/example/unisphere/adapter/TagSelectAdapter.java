package com.example.unisphere.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagSelectAdapter extends RecyclerView.Adapter<TagSelectViewHolder> implements View.OnClickListener {

    private List<Tag> tagList;
    private RecyclerView recyclerView;

    // data is passed into the constructor
    private List<Boolean> selectedTags;  // List to track selected state

    public TagSelectAdapter(List<Tag> tagList, RecyclerView recyclerView) {
        this.tagList = tagList;
        this.recyclerView = recyclerView;
        selectedTags = new ArrayList<>(Collections.nCopies(tagList.size(), false));  // Initialize selectedTags with all false


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
        holder.itemView.setBackgroundColor(selectedTags.get(position) ? Color.parseColor("#5D3FD3") : Color.parseColor("#DDDDDD")); // Set background based on selection

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

//            v.setBackgroundColor(Color.parseColor("#5D3FD3")); // Set clicked color (red here)

            selectedTags.set(clickedPosition, !selectedTags.get(clickedPosition));  // Toggle selected state
            v.setBackgroundColor(selectedTags.get(clickedPosition) ? Color.parseColor("#5D3FD3") : Color.parseColor("#DDDDDD"));  // Set color based on selection

        }
    }
}


// stores and recycles views as they are scrolled off screen
