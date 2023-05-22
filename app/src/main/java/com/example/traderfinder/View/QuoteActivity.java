package com.example.traderfinder.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.traderfinder.Controller.QuoteAdapter;
import com.example.traderfinder.Model.Job;
import com.example.traderfinder.Model.Quote;
import com.example.traderfinder.Model.TradesmanUserDetails;
import com.example.traderfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuoteActivity extends AppCompatActivity implements QuoteAdapter.OnQuoteAcceptedListener {

    private RecyclerView quotesRecyclerView;
    private FloatingActionButton addQuoteButton;
    private QuoteAdapter quoteAdapter;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String jobId; // this will be passed in through an intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        Job job = (Job) getIntent().getSerializableExtra("job");
        if (job != null) {
            jobId = job.getId();
        } else {
            Toast.makeText(this, "No job data received", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore = FirebaseFirestore.getInstance();

        addQuoteButton = findViewById(R.id.add_quote_button);
        quotesRecyclerView = findViewById(R.id.quotes_recycler_view);
        quotesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getCurrentUserRole(new UserAccountTypeCallback() {
            @Override
            public void onCallback(String accountType) {
                if ("tradesman".equals(accountType)) {
                    addQuoteButton.setVisibility(View.VISIBLE);
                } else {
                    addQuoteButton.setVisibility(View.GONE);
                }
                quoteAdapter = new QuoteAdapter(QuoteActivity.this, new ArrayList<>(), accountType, QuoteActivity.this); // Pass this as OnQuoteAcceptedListener
                quotesRecyclerView.setAdapter(quoteAdapter);
                loadQuotesForJob(jobId);
            }
        });

        addQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current user
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Ensure user is logged in
                if (currentUser != null) {
                    DocumentReference docRef = firestore.collection("TradesmanUserDetails").document(currentUser.getUid());

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    TradesmanUserDetails userDetails = document.toObject(TradesmanUserDetails.class);
                                    // Check if all profile fields have been filled
                                    if (userDetails.getFirstName() != null && !userDetails.getFirstName().isEmpty()
                                            && userDetails.getLastName() != null && !userDetails.getLastName().isEmpty()
                                            && userDetails.getLocation() != null && !userDetails.getLocation().isEmpty()
                                            && userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().isEmpty()
                                            && userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()
                                            && userDetails.getPastJobImageUrl() != null && !userDetails.getPastJobImageUrl().isEmpty()
                                            && userDetails.getCertificationImageUrl() != null && !userDetails.getCertificationImageUrl().isEmpty()) {
                                        // If all fields are filled, allow the user to add a quote
                                        AddQuoteFragment addQuoteFragment = new AddQuoteFragment();
                                        Bundle args = new Bundle();
                                        args.putString("jobId", jobId);
                                        addQuoteFragment.setArguments(args);
                                        addQuoteFragment.show(getSupportFragmentManager(), "AddQuote");
                                    } else {
                                        // If not all fields are filled, show a message
                                        Toast.makeText(QuoteActivity.this, "Please complete your profile before adding a quote", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If the document does not exist, show a message
                                    Toast.makeText(QuoteActivity.this, "Please create a profile before adding a quote", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle errors
                                Log.d("ProfileCheck", "get failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    // If user is not logged in, provide appropriate response
                    Toast.makeText(QuoteActivity.this, "You need to be logged in to perform this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load quotes for job
        loadQuotesForJob(jobId);
    }

    public interface UserAccountTypeCallback {
        void onCallback(String accountType);
    }

    public void getCurrentUserRole(UserAccountTypeCallback callback) {
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

    private void loadQuotesForJob(String jobId) {
        firestore.collection("quotes")
                .whereEqualTo("jobId", jobId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Quote> quotesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Quote quote = document.toObject(Quote.class);
                            quotesList.add(quote);
                        }
                        updateQuotes(quotesList); // Update RecyclerView with new data
                    } else {
                        Toast.makeText(this, "Error loading quotes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateQuotes(List<Quote> newQuotes) {
        quoteAdapter.updateQuotes(newQuotes);
        quoteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onQuoteAccepted() {
        // Close this activity and return to the previous activity
        finish();
    }
}
