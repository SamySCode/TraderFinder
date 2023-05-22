package com.example.traderfinder.View;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.traderfinder.Model.Job;
import com.example.traderfinder.Model.Quote;
import com.example.traderfinder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddQuoteFragment extends DialogFragment {

    private EditText quotePriceInput;
    private EditText quoteMessageInput;
    private Button addQuoteButton;
    private Job job;
    private String jobId;

    public AddQuoteFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            jobId = getArguments().getString("jobId");
        }
        return inflater.inflate(R.layout.fragment_add_quote, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quotePriceInput = view.findViewById(R.id.quote_price_input);
        quoteMessageInput = view.findViewById(R.id.quote_message_input);
        addQuoteButton = view.findViewById(R.id.add_quote_button);

        addQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuote();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                // Custom actions here
                dismiss();
            }
        };
    }

    public void addQuote() {
        String price = quotePriceInput.getText().toString().trim();
        String message = quoteMessageInput.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!TextUtils.isEmpty(price) && !TextUtils.isEmpty(message) && user != null) {
            String quoteId = FirebaseFirestore.getInstance().collection("quotes").document().getId();
            Quote quote = new Quote(quoteId, user.getEmail(), message, Double.parseDouble(price), jobId, user.getUid());

            FirebaseFirestore.getInstance().collection("quotes")
                    .document(quoteId)
                    .set(quote)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Quote added", Toast.LENGTH_SHORT).show();
                            dismiss();  // Dismiss the dialog
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error adding quote", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}

