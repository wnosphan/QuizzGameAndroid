package com.prm392.quizgame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prm392.quizgame.LoginActivity;
import com.prm392.quizgame.databinding.FragmentProfileBinding;
import com.prm392.quizgame.R;
import com.prm392.quizgame.model.User;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore database;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //init view
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //load profile
        loadUserProfile();


        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        binding.updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    private void updateUserProfile() {
        String uid = auth.getCurrentUser().getUid();

        // Get updated values from the EditText fields
        String updatedEmail = binding.emailProfileBox.getText().toString().trim();
        String updatedName = binding.nameProfileBox.getText().toString().trim();
        String updatedPassword = binding.passwordProfileBox.getText().toString().trim();

        // Validate the inputs
        if (updatedEmail.isEmpty() || updatedName.isEmpty() || updatedPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map with the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", updatedEmail);
        updates.put("name", updatedName);
        updates.put("password", updatedPassword);  // Be careful about handling password storage!

        // Update the user document in Firestore
        database.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadUserProfile() {
        String uid = auth.getCurrentUser().getUid();

        database.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class); // Convert document to User object
                    if (user != null) {
                        // Bind user data to the views
                        binding.emailProfileBox.setText(user.getEmail());
                        binding.nameProfileBox.setText(user.getName());
                        binding.passwordProfileBox.setText(user.getPassword()); // Be cautious with password display
                        // You can also load and set the profile image here (if necessary)
                    }
                } else {
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}