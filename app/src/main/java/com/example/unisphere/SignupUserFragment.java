package com.example.unisphere;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SignupUserFragment extends Fragment {

    private NavController navController;


    public SignupUserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


        FloatingActionButton nextButton = view.findViewById(R.id.signup3_btn);
        if (nextButton != null) {
            nextButton.setOnClickListener(v -> navController.navigate(R.id.action_fragment_signup_user_to_fragment_signup_student));
        }

    }


//    public void signupStudent(View view) {
//          navController.navigate(R.id.action_fragment_signup_user_to_fragment_signup_student);
//
//    }
}