package com.example.traderfinder.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.traderfinder.Model.TradesmanUserDetails;
import com.example.traderfinder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class TradesmanProfileDetailsFragment extends Fragment {

    private EditText editTextFirstName, editTextLastName, editTextLocation, editTextPhoneNumber, editTextEmail;
    private ImageView imageViewPastJob, imageViewCertification;
    private Button buttonSave;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    // Replace these with actual IDs
    private static final int PICK_IMAGE_REQUEST_PAST_JOB = 1;
    private static final int PICK_IMAGE_REQUEST_CERTIFICATION = 2;

    private Uri pastJobImageUri, certificationImageUri;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tradesman_profile_details, container, false);

        editTextFirstName = view.findViewById(R.id.first_name);
        editTextLastName = view.findViewById(R.id.last_name);
        editTextLocation = view.findViewById(R.id.location);
        editTextPhoneNumber = view.findViewById(R.id.phone_number);
        editTextEmail = view.findViewById(R.id.email);
        imageViewPastJob = view.findViewById(R.id.image_view_past_jobs);
        imageViewCertification = view.findViewById(R.id.image_view_certifications);
        buttonSave = view.findViewById(R.id.save_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        imageViewPastJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_IMAGE_REQUEST_PAST_JOB);
            }
        });

        imageViewCertification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_IMAGE_REQUEST_CERTIFICATION);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
            }
        });

        loadUserDetails();

        return view;
    }

    private void openFileChooser(int requestCode) {
        // This method is used to open the file chooser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    private void saveUserDetails() {
        // This method will save user details to Firestore
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String location = editTextLocation.getText().toString().trim();
        final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();

        final StorageReference pastJobImageRef = storageRef.child("images/" + pastJobImageUri.getLastPathSegment());
        pastJobImageRef.putFile(pastJobImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                pastJobImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Get the URL of the uploaded image
                        final String pastJobImageUrl = uri.toString();

                        final StorageReference certificationImageRef = storageRef.child("images/" + certificationImageUri.getLastPathSegment());
                        certificationImageRef.putFile(certificationImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Handle successful uploads on complete
                                certificationImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Get the URL of the uploaded image
                                        String certificationImageUrl = uri.toString();

                                        // Save the user details
                                        TradesmanUserDetails userDetails = new TradesmanUserDetails(firstName, lastName, location, phoneNumber, email, pastJobImageUrl, certificationImageUrl);
                                        DocumentReference documentReference = firebaseFirestore.collection("TradesmanUserDetails").document(firebaseAuth.getCurrentUser().getUid());

                                        documentReference.set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Profile Saved", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void loadUserDetails() {
        // Load user details from Firestore and set the text of EditTexts and the image of ImageViews

        DocumentReference docRef = firebaseFirestore.collection("TradesmanUserDetails").document(firebaseAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    TradesmanUserDetails userDetails = documentSnapshot.toObject(TradesmanUserDetails.class);

                    editTextFirstName.setText(userDetails.getFirstName());
                    editTextLastName.setText(userDetails.getLastName());
                    editTextLocation.setText(userDetails.getLocation());
                    editTextPhoneNumber.setText(userDetails.getPhoneNumber());
                    editTextEmail.setText(userDetails.getEmail());

                    // Load the images with Glide or Picasso
                    Glide.with(getContext())
                            .load(userDetails.getPastJobImageUrl())
                            .into(imageViewPastJob);

                    Glide.with(getContext())
                            .load(userDetails.getCertificationImageUrl())
                            .into(imageViewCertification);
                } else {
                    Toast.makeText(getActivity(), "No Profile Found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            switch (requestCode) {
                case PICK_IMAGE_REQUEST_PAST_JOB:
                    pastJobImageUri = imageUri;
                    Glide.with(getContext()).load(pastJobImageUri).into(imageViewPastJob);
                    break;

                case PICK_IMAGE_REQUEST_CERTIFICATION:
                    certificationImageUri = imageUri;
                    Glide.with(getContext()).load(certificationImageUri).into(imageViewCertification);
                    break;
            }
        }
    }
}

