package com.example.traderfinder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private Context context;
    private List<Job> jobs;
    private String accountType;

    public JobAdapter(Context context, List<Job> jobs, String accountType) {
        this.context = context;
        this.jobs = jobs;
        this.accountType = accountType;
    }

    public JobAdapter(Fragment fragment, List<Job> jobs) {
        this.context = fragment.getContext();
        this.jobs = jobs;
    }

    public void updateJobs(List<Job> jobs) {
        this.jobs = jobs;
        notifyDataSetChanged();
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_card, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        final Job job = jobs.get(position);

        Glide.with(holder.itemView.getContext())
                .load(job.getJobImage())
                .into(holder.jobImage);

        holder.jobTitle.setText(job.getJobTitle());
        holder.jobTrade.setText(job.getTrade());
        holder.jobLocation.setText(job.getJobLocation());
        holder.acceptedQuotePrice.setText("£" + job.getAcceptedQuotePrice());

        if (job.getJobStatus().equals("Confirmed")) {
            holder.buttonViewTradesman.setVisibility(View.VISIBLE);
            holder.buttonViewTradesman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TradesmanProfileDetailsActivity.class);
                    intent.putExtra("TRADESMAN_ID", job.getTradesmanId());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.buttonViewTradesman.setVisibility(View.GONE);
        }

        if (job.getJobStatus().equals("Confirmed") && !job.isCompleted()) {
            holder.buttonComplete.setVisibility(View.VISIBLE);
            holder.buttonComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("jobs").document(job.getId())
                            .update("isCompleted", true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                    job.setCompleted(true);
                                    notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firestore", "Error updating document", e);
                                }
                            });
                }
            });
        } else {
            holder.buttonComplete.setVisibility(View.GONE);
        }

        if (job.getJobStatus().equals("Confirmed") && job.isCompleted() && "general".equals(accountType)) {
            holder.buttonPaymentNeeded.setVisibility(View.VISIBLE);
            holder.buttonPaymentNeeded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proceedToPayment(job);
                }
            });
        } else {
            holder.buttonPaymentNeeded.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JobDetailsActivity.class);
                intent.putExtra("job", job);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private ImageView jobImage;
        private TextView jobTrade;
        private TextView jobTitle;
        private TextView jobLocation;
        private TextView acceptedQuotePrice;
        private Button buttonViewTradesman;
        private Button buttonComplete;
        private Button buttonPaymentNeeded;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobImage = itemView.findViewById(R.id.job_card_image);
            jobTitle = itemView.findViewById(R.id.job_card_title);
            jobTrade = itemView.findViewById(R.id.job_card_trade);
            jobLocation = itemView.findViewById(R.id.job_card_location);
            acceptedQuotePrice = itemView.findViewById(R.id.quote_price);
            buttonViewTradesman = itemView.findViewById(R.id.button_view_tradesman);
            buttonComplete = itemView.findViewById(R.id.button_complete);
            buttonPaymentNeeded = itemView.findViewById(R.id.button_payment_needed);
        }
    }

    private void proceedToPayment(Job job) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra("job", job);
        context.startActivity(intent);
    }
}
