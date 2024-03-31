package com.example.unisphere.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Event;

import java.util.List;

public class EventViewHolder extends RecyclerView.ViewHolder {

    ImageView orgImage;
    TextView eventTitleTv;
    TextView eventDateTv;
    Context context;
    List<Event> events;

    public EventViewHolder(@NonNull View itemView, Context context, List<Event> events, EventAdapter.ClickListener clickListener) {
        super(itemView);
        this.context = context;
        this.orgImage = itemView.findViewById(R.id.orgIv);
        this.eventTitleTv = itemView.findViewById(R.id.eventTitleTv);
        this.eventDateTv = itemView.findViewById(R.id.eventTimeTv);
        this.events = events;
        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && clickListener != null) {
                clickListener.onEventClick(position);
            }
        });
    }

}
