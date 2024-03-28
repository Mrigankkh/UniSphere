package com.example.unisphere.ui.search;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unisphere.R;
import com.example.unisphere.adapter.EventAdapter;
import com.example.unisphere.adapter.searchResult.SearchResultAdapter;
import com.example.unisphere.adapter.tagSelect.TagSelectAdapter;
import com.example.unisphere.model.Event;
import com.example.unisphere.model.Tag;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class SearchResultFragment extends Fragment {


    private NavController navController;
    private RecyclerView searchResultsRecyclerView;
    private ArrayList<String> tempUserEmails;
    private SearchResultAdapter searchResultAdapter;

    public SearchResultFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        tempUserEmails = (ArrayList<String>) arguments.getSerializable("search_results");


        // Use the receivedList here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.searchResultsRecyclerView = view.findViewById(R.id.recyclerView_search_results);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        searchResultAdapter = new SearchResultAdapter(requireContext(), tempUserEmails, this::onUserClick);
        searchResultsRecyclerView.setAdapter(searchResultAdapter);

    }

    private void onUserClick(int position) {
        Toast.makeText(this.getContext(), "User was pressed", Toast.LENGTH_SHORT).show();
    }


}