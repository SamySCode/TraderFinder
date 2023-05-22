package com.example.traderfinder.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.traderfinder.Model.Job;
import com.example.traderfinder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView jobTitleTextView;
    private TextView jobDescriptionTextView;
    private TextView jobLocationTextView;
    private TextView jobStartDateTextView;
    private TextView jobEndDateTextView;
    private TextView jobTradeTextView;
    private Button deleteButton;
    private Job job;
    private Button quoteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        jobTitleTextView = findViewById(R.id.job_details_title);
        jobDescriptionTextView = findViewById(R.id.job_details_description);
        jobLocationTextView = findViewById(R.id.job_details_location);
        jobStartDateTextView = findViewById(R.id.job_details_start_date);
        jobEndDateTextView = findViewById(R.id.job_details_end_date);
        jobTradeTextView = findViewById(R.id.job_details_trade);
        deleteButton = findViewById(R.id.job_delete_button);
        quoteButton = findViewById(R.id.quote_button);
        quoteButton.setText("Quotes");
        quoteButton.setVisibility(View.VISIBLE);
        quoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open QuoteActivity, passing the current job
                Intent intent = new Intent(JobDetailsActivity.this, QuoteActivity.class);
                intent.putExtra("job", job);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteJob();
            }
        });

        // Retrieve the Job object from the Intent
        job = (Job) getIntent().getSerializableExtra("job");

        getCurrentUserRole(new UserAccountTypeCallback() {
            @Override
            public void onCallback(String accountType) {
                if ("tradesman".equals(accountType)) {
                    deleteButton.setVisibility(View.GONE);
                } else {
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }
        });

        if ("Confirmed".equals(job.getJobStatus())) {
            quoteButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else {
            quoteButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }


        if (job != null) {
            jobTitleTextView.setText(job.getJobTitle());
            jobDescriptionTextView.setText(job.getJobDescription());
            jobLocationTextView.setText(job.getJobLocation());
            jobStartDateTextView.setText(job.getJobStartDate());
            jobEndDateTextView.setText(job.getJobEndDate());
            jobTradeTextView.setText(job.getTrade());

            // Load the image into the ImageView
            ImageView jobImageView = findViewById(R.id.job_details_image);
            Glide.with(this)
                    .load(job.getJobImage())
                    .into(jobImageView);
        }
    }

    public void getCurrentUserRole(final UserAccountTypeCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.child("accountType").getValue(String.class);
                    callback.onCallback(role);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
    }

    public interface UserAccountTypeCallback {
        void onCallback(String accountType);
    }

    public void deleteJob() {
        if (job != null) {
            FirebaseFirestore.getInstance().collection("jobs")
                    .document(job.getId()) // Make sure Job class has a getId() method to return the Firestore document ID
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(JobDetailsActivity.this, "Job deleted", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(JobDetailsActivity.this, "Error deleting job", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void start(Context context, Job job) {
        Intent starter = new Intent(context, JobDetailsActivity.class);
        starter.putExtra("job", job);
        context.startActivity(starter);
    }
}
