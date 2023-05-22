package com.example.traderfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;

public class TradesmanProfileDetailsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private TradesmanUserDetails tradesmanUserDetails;

    private TextView firstNameTextView, lastNameTextView, locationTextView, phoneNumberTextView, emailTextView;
    private ImageView pastJobImageView, certificationImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradesman_profile_details);

        firstNameTextView = findViewById(R.id.first_name);
        lastNameTextView = findViewById(R.id.last_name);
        locationTextView = findViewById(R.id.location);
        phoneNumberTextView = findViewById(R.id.phone_number);
        emailTextView = findViewById(R.id.email);
        pastJobImageView = findViewById(R.id.image_view_past_jobs);
        certificationImageView = findViewById(R.id.image_view_certifications);

        firestore = FirebaseFirestore.getInstance();

        String tradesmanId = getIntent().getStringExtra("TRADESMAN_ID");

        firestore.collection("TradesmanUserDetails").document(tradesmanId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            tradesmanUserDetails = documentSnapshot.toObject(TradesmanUserDetails.class);

                            firstNameTextView.setText(tradesmanUserDetails.getFirstName());
                            lastNameTextView.setText(tradesmanUserDetails.getLastName());
                            locationTextView.setText(tradesmanUserDetails.getLocation());
                            phoneNumberTextView.setText(tradesmanUserDetails.getPhoneNumber());
                            emailTextView.setText(tradesmanUserDetails.getEmail());

                            Glide.with(TradesmanProfileDetailsActivity.this)
                                    .load(tradesmanUserDetails.getPastJobImageUrl())
                                    .into(pastJobImageView);

                            Glide.with(TradesmanProfileDetailsActivity.this)
                                    .load(tradesmanUserDetails.getCertificationImageUrl())
                                    .into(certificationImageView);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure here
                    }
                });
    }
}

