package com.example.unisphere.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.EventAdapter;
import com.example.unisphere.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    private List<Event> events;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FloatingActionButton fabAddEvent;
    private static final String ARG_EVENT = "event";

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        events = new ArrayList<>();
        events.add(new Event(null, "Trivia Night1Trivia Night1Trivia Night1Trivia Night1", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night2", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night3", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night4", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));
        events.add(new Event(null, "Trivia Night5", "2024-03-04T12:33:58", "Krentzman Quad"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View eventsView = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = eventsView.findViewById(R.id.recyclerViewEventsList);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        eventAdapter = new EventAdapter(requireContext(), events, eventsView.findViewById(android.R.id.content), this::onEventClick);
        recyclerView.setAdapter(eventAdapter);
        fabAddEvent = eventsView.findViewById(R.id.fabAddEvent);
        String userType = "organization";
        if (userType.equals("organization")) {
            System.out.println("Setting fab visible");
            fabAddEvent.setVisibility(View.VISIBLE);
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }
        fabAddEvent.setOnClickListener(view -> createNewEvent(view));
        return eventsView;
    }

    private void createNewEvent(View view) {
    }

    private void onEventClick(int position) {
        Event event = events.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EVENT, event);
        Navigation.findNavController(requireView()).navigate(R.id.eventDetailsFragment,bundle);
    }

}