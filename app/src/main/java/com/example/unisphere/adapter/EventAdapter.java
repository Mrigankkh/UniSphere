package com.example.unisphere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.model.Event;
import com.example.unisphere.model.Post;
import com.example.unisphere.service.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    Context context;
    List<Event> events;
    View rootView;

    private ClickListener clickListener;


    public EventAdapter(Context context, List<Event> events, View rootView, ClickListener clickListener) {
        this.context = context;
        this.events = events;
        this.rootView = rootView;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.event_list_row_item, parent, false), context, events,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventTitleTv.setText(event.getEventTitle());
        holder.eventPlaceTv.setText(event.getEventPlace());
        holder.eventDateTv.setText(Util.convertDateTime(event.getEventDate()));

        Picasso.get()
                .load(R.drawable.no_events)
                .resize(400, 400)
                .centerCrop()
                .into(holder.orgImage);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface ClickListener {
        void onEventClick(int position);
    }
}
