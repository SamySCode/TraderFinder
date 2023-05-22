package com.example.traderfinder.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.traderfinder.Model.Job;
import com.example.traderfinder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {
    private EditText cardNumber;
    private EditText cardExpiryDate;
    private EditText cardCvv;
    private Button confirmPaymentButton;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        job = (Job) getIntent().getSerializableExtra("job");

        cardNumber = findViewById(R.id.card_number);
        cardExpiryDate = findViewById(R.id.card_expiry_date);
        cardCvv = findViewById(R.id.card_cvv);
        confirmPaymentButton = findViewById(R.id.confirm_payment_button);

        confirmPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNumberInput = cardNumber.getText().toString();
                String cardExpiryDateInput = cardExpiryDate.getText().toString();
                String cardCvvInput = cardCvv.getText().toString();

                if (cardNumberInput.isEmpty() || cardExpiryDateInput.isEmpty() || cardCvvInput.isEmpty()) {
                    Toast.makeText(PaymentActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("jobs").document(job.getId())
                            .update("jobStatus", "Completed")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "Job status successfully updated to Completed!");
                                    Toast.makeText(PaymentActivity.this, "Payment successful and job status updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firestore", "Error updating job status", e);
                                }
                            });
                }
            }
        });
    }
}
