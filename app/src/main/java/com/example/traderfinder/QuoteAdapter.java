package com.example.traderfinder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private List<Quote> quotes;
    private Context context;
    private FirebaseFirestore firestoreDb;  // reference to the Firestore Database

    private String accountType;

    public interface OnQuoteAcceptedListener {
        void onQuoteAccepted();
    }

    private final OnQuoteAcceptedListener onQuoteAcceptedListener;

    public QuoteAdapter(Context context, List<Quote> quotes, String accountType, OnQuoteAcceptedListener onQuoteAcceptedListener) {
        this.context = context;
        this.quotes = new ArrayList<>(quotes); // Make a copy of the input list
        this.firestoreDb = FirebaseFirestore.getInstance();  // gets instance of Firestore
        this.accountType = accountType;
        this.onQuoteAcceptedListener = onQuoteAcceptedListener;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_card, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quotes.get(position);

        holder.quotePrice.setText(String.valueOf(quote.getPrice()));
        holder.quoteMessage.setText(quote.getMessage());
        holder.quoteEmail.setText(quote.getUserEmail());

        // If the user is a tradesman, hide the accept button
        if ("tradesman".equals(accountType)) {
            holder.quoteAcceptButton.setVisibility(View.GONE);
        } else {
            holder.quoteAcceptButton.setVisibility(View.VISIBLE);
        }

        holder.quoteAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle quote acceptance
                String jobId = quote.getJobId();  // assuming there is a getJobId() method in the Quote class
                String tradesmanId = quote.getUserId();  // gets the tradesman's ID who made the quote
                double acceptedQuotePrice = quote.getPrice();  // get the quote price
                DocumentReference jobRef = firestoreDb.collection("jobs").document(jobId);  // get reference to the job document

                // update the jobStatus and tradesmanId in the Firestore Database
                jobRef.update(
                        "jobStatus", "Confirmed",
                        "tradesmanId", tradesmanId,
                        "acceptedQuotePrice", acceptedQuotePrice
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Call the listener method when the quote has been accepted
                        onQuoteAcceptedListener.onQuoteAccepted();
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start TradesmanProfileActivity
                Intent intent = new Intent(context, TradesmanProfileDetailsActivity.class);
                intent.putExtra("TRADESMAN_ID", quote.getUserId()); // Pass the tradesman's ID
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
        private TextView quotePrice;
        private TextView quoteMessage;
        private TextView quoteEmail;
        private Button quoteAcceptButton;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            quotePrice = itemView.findViewById(R.id.quote_price);
            quoteMessage = itemView.findViewById(R.id.quote_message);
            quoteEmail = itemView.findViewById(R.id.quote_email);
            quoteAcceptButton = itemView.findViewById(R.id.quote_accept_button);
        }
    }
    public void updateQuotes(List<Quote> newQuotes) {
        quotes.clear();
        quotes.addAll(newQuotes);
        notifyDataSetChanged();
    }
}
