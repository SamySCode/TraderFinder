package com.example.traderfinder.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.traderfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CreateJobDialogFragment extends DialogFragment {

    private EditText jobTitleEditText;
    private EditText jobDescriptionEditText;
    private EditText jobStartDateEditText;
    private EditText jobEndDateEditText;
    private EditText jobLocationEditText;
    private Spinner tradeSpinner;
    private ImageView jobImageView;

    private Uri selectedImageUri;
    private String jobId;

    // Initialize Firestore and Storage
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    // Initialize FirebaseAuth
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private static final int IMAGE_PICK_REQUEST_CODE = 100;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_create_job_dialog, null);

        jobTitleEditText = view.findViewById(R.id.edit_text_job_title);
        jobDescriptionEditText = view.findViewById(R.id.edit_text_job_description);
        jobStartDateEditText = view.findViewById(R.id.edit_text_job_start_date);
        jobEndDateEditText = view.findViewById(R.id.edit_text_job_end_date);
        jobLocationEditText = view.findViewById(R.id.edit_text_job_location);
        tradeSpinner = view.findViewById(R.id.spinner_trade);
        jobImageView = view.findViewById(R.id.image_view_job_photos);

        jobImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, IMAGE_PICK_REQUEST_CODE);
            }
        });

        builder.setView(view)
                .setPositiveButton(R.string.create_job, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        createJob();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateJobDialogFragment.this.getDialog().cancel();
                    }
                });

        // Initialize Spinner with the array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.trades_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tradeSpinner.setAdapter(adapter);

        return builder.create();
    }

    private void createJob() {
        Activity activity = getActivity();
        if (activity == null) return;

        // Get user ID
        String userId = firebaseAuth.getCurrentUser().getUid();

        String jobTitle = jobTitleEditText.getText().toString();
        String jobDescription = jobDescriptionEditText.getText().toString();
        String jobStartDate = jobStartDateEditText.getText().toString();
        String jobEndDate = jobEndDateEditText.getText().toString();
        String jobLocation = jobLocationEditText.getText().toString();
        String trade = tradeSpinner.getSelectedItem().toString();

        // Generate the jobId first
        jobId = firestore.collection("jobs").document().getId();

        // Store job data in a Map
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("id", jobId); // Add the job ID to the job data
        jobData.put("userId", userId); // Add the user ID to the job data
        jobData.put("jobTitle", jobTitle);
        jobData.put("jobDescription", jobDescription);
        jobData.put("jobStartDate", jobStartDate);
        jobData.put("jobEndDate", jobEndDate);
        jobData.put("jobLocation", jobLocation);
        jobData.put("trade", trade);
        jobData.put("jobStatus", "open");  // Default status is open

        // Add job data to Firestore
        firestore.collection("jobs")
                .document(jobId)
                .set(jobData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, "Job created successfully",
                            Toast.LENGTH_SHORT).show();
                    uploadJobImage();  // Pass the job ID to the uploadJobImage method
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Error creating job: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadJobImage() {
        // Upload job image to Firebase Storage
        // Note: This assumes you have a Uri for the selected image called "selectedImageUri"
        // You will need to implement image selection logic and get the Uri
        if (selectedImageUri != null) {
            StorageReference jobImageRef = storageRef.child("job_images/" + jobId + ".jpg");

            jobImageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Activity activity = getActivity();
                        if (activity != null) {
                            Toast.makeText(activity, "Job image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }

                        // Get the uploaded image URL and update the Firestore document with the image URL
                        jobImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageURL = uri.toString();
                            firestore.collection("jobs").document(jobId)
                                    .update("jobImage", imageURL)
                                    .addOnSuccessListener(aVoid -> {
                                        Activity activity2 = getActivity();
                                        if (activity2 != null) {
                                            Toast.makeText(activity2, "Job image URL saved to Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Activity activity2 = getActivity();
                                        if (activity2 != null) {
                                            Toast.makeText(activity2, "Error saving job image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Activity activity = getActivity();
                        if (activity != null) {
                            Toast.makeText(activity, "Error uploading job image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            jobImageView.setImageURI(selectedImageUri);
        }
    }
}


