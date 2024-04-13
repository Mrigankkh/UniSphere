package com.example.unisphere.ui.events;

import static android.content.Context.MODE_PRIVATE;
import static com.example.unisphere.service.Util.USER_DATA;
import static com.example.unisphere.service.Util.getUserDataFromSharedPreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unisphere.R;
import com.example.unisphere.adapter.EventAdapter;
import com.example.unisphere.model.Event;
import com.example.unisphere.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class EventsFragment extends Fragment {

    private static final String ARG_EVENT = "event";
    private static final String ORG_USER_ROLE = "Organization";
    private List<Event> events;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FloatingActionButton fabAddEvent;
    private ImageView noEventsIv;
    private TextView noEventsTv;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference eventDatabaseReference;
    private User currentUser;

    public EventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
        currentUser = getUserDataFromSharedPreferences(preferences);
        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_db_url));
        eventDatabaseReference = firebaseDatabase.getReference().child(currentUser.getUniversity()).child(getString(R.string.events));
        events = new ArrayList<>();
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
        noEventsIv = eventsView.findViewById(R.id.noEventsIv);
        noEventsTv = eventsView.findViewById(R.id.noEventsTv);
        if (currentUser.getUserRole().equals(ORG_USER_ROLE)) {
            fabAddEvent.setVisibility(View.VISIBLE);
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }
        fabAddEvent.setOnClickListener(view -> createNewEvent(view));
        retrieveEventsFromFirebase();
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


    public void retrieveEventsFromFirebase() {
        eventDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Event> eventsFromDb = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    eventsFromDb.add(event);
                }

                // Assuming postList is a member variable of your class
                events.clear();
                events.addAll(eventsFromDb);

                // Notify the adapter of the data change TODO if this can be improved
                eventAdapter.notifyDataSetChanged();
//                if(events.size()>0) {
//                    noEventsIv.setVisibility(View.GONE);
//                    noEventsTv.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: remove
    private void addEventsOnFirebase() {
        events = new ArrayList<>();
        List<String> options = new ArrayList<>();
        options.add("Yes");
        options.add("No");
        events.add(new Event(currentUser.getEmailID(), null, "Trivia Night",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!", null));
        events.add(new Event(currentUser.getEmailID(), null, "Trivia Night2",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!", null));
        events.add(new Event(currentUser.getEmailID(), null, "Trivia Night3",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!", null));
        events.add(new Event(currentUser.getEmailID(), null, "Trivia Night4",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!", null));
        events.add(new Event(currentUser.getEmailID(), null, "Trivia Night5",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!", null));
        events.add(new Event(currentUser.getEmailID(), null, "Trivia Night6",
                "This will be a game night for all the college students. Pizza will be served.",
                null,
                "2024-03-04T18:30:00",
                "2024-03-04T22:30:00",
                "Krentzman Quad",
                "Enter your email id below",
                "Register",
                "Are you going to join the event?",
                options,
                "Go!", null));
    }

}