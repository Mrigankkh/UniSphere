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


public class EventsFragment extends Fragment {

    private static final String ARG_EVENT = "event";
    private List<Event> events;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FloatingActionButton fabAddEvent;

    public EventsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        events = new ArrayList<>();
        List<String> options = new ArrayList<>();
        options.add("Yes");
        options.add("No");
        events.add(new Event("Trivia Night",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!"));
        events.add(new Event("Trivia Night2",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!"));
        events.add(new Event("Trivia Night3",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!"));
        events.add(new Event("Trivia Night4",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!"));
        events.add(new Event("Trivia Night5",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!"));
        events.add(new Event("Trivia Night6",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!"));
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
        Navigation.findNavController(requireView()).navigate(R.id.createEventFragment);
    }

    private void onEventClick(int position) {
        Event event = events.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EVENT, event);
        Navigation.findNavController(requireView()).navigate(R.id.eventDetailsFragment, bundle);
    }

}