package com.example.traderfinder;

import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.traderfinder.R;
import com.example.traderfinder.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileDetailsFragment extends Fragment {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText locationEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;

    private Button saveButton;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);

        firstNameEditText = view.findViewById(R.id.first_name);
        lastNameEditText = view.findViewById(R.id.last_name);
        locationEditText = view.findViewById(R.id.location);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        emailEditText = view.findViewById(R.id.email);

        saveButton = view.findViewById(R.id.save_button);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadUserDetails();

        saveButton.setOnClickListener(v -> saveUserDetails());

        return view;
    }

    private void loadUserDetails() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDetails userDetails = task.getResult().toObject(UserDetails.class);

                        if(userDetails != null) {
                            firstNameEditText.setText(userDetails.getFirstName() != null ? userDetails.getFirstName() : "");
                            lastNameEditText.setText(userDetails.getLastName() != null ? userDetails.getLastName() : "");
                            locationEditText.setText(userDetails.getLocation() != null ? userDetails.getLocation() : "");
                            phoneNumberEditText.setText(userDetails.getPhoneNumber() != null ? userDetails.getPhoneNumber() : "");
                            emailEditText.setText(userDetails.getEmail() != null ? userDetails.getEmail() : "");
                        } else {
                            Toast.makeText(getActivity(), "No user details found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error loading profile details", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveUserDetails() {
        String userId = auth.getCurrentUser().getUid();

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String email = emailEditText.getText().toString();

        UserDetails userDetails = new UserDetails(firstName, lastName, location, phoneNumber, email);

        firestore.collection("users").document(userId)
                .set(userDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Profile details saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error saving profile details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void checkUserDetails(CheckUserDetailsCallback callback) {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDetails userDetails = task.getResult().toObject(UserDetails.class);

                        if(userDetails != null) {
                            callback.onComplete(
                                    userDetails.getFirstName() != null && !userDetails.getFirstName().isEmpty() &&
                                            userDetails.getLastName() != null && !userDetails.getLastName().isEmpty() &&
                                            userDetails.getLocation() != null && !userDetails.getLocation().isEmpty() &&
                                            userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().isEmpty() &&
                                            userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()
                            );
                        } else {
                            Toast.makeText(getActivity(), "No user details found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error checking profile details", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public interface CheckUserDetailsCallback {
        void onComplete(boolean isComplete);
    }
}
