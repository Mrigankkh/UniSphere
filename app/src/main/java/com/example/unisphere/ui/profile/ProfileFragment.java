package com.example.unisphere.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.unisphere.R;
import com.example.unisphere.service.AuthService;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class ProfileFragment extends Fragment {


    private Button tempLogout;
    private TextView tempUserData;
    private SharedPreferences sharedPreferences;
    private NavController navController;
    AuthService authService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        authService = AuthService.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tempLogout = view.findViewById(R.id.tempLogout);
        tempUserData = view.findViewById(R.id.tempProfileData);
        tempLogout.setOnClickListener(View -> logOut());
        navController = Navigation.findNavController(view);
        String userData = sharedPreferences.getString("username", "Default Name") + sharedPreferences.getString("university", "")
                + sharedPreferences.getString("email", " Default email") + sharedPreferences.getString("user_role", "") +
                sharedPreferences.getString("phone_number", "");
        tempUserData.setText(userData);
    }

    public void logOut() {
        sharedPreferences.edit().clear();
        authService.signOut();
        navController.clearBackStack(R.id.activity_login);
        navController.navigate(R.id.action_navigation_profile_to_activity_login);


    }
}