package com.example.traderfinder.ui.confirmed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.traderfinder.Job;
import com.example.traderfinder.JobAdapter;
import com.example.traderfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TradesmanConfirmedFragment extends Fragment {
    private RecyclerView tradesmanConfirmedJobsRecyclerView;
    private JobAdapter jobAdapter;
    private FirebaseFirestore db;
    private List<Job> confirmedJobs;
    private String tradesmanId;

    public TradesmanConfirmedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tradesman_confirmed, container, false);

        tradesmanConfirmedJobsRecyclerView = view.findViewById(R.id.tradesman_confirmed_jobs_recycler_view);
        tradesmanConfirmedJobsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        confirmedJobs = new ArrayList<>();
        jobAdapter = new JobAdapter(getContext(), new ArrayList<>(), "tradesman");
        tradesmanConfirmedJobsRecyclerView.setAdapter(jobAdapter);

        tradesmanId = FirebaseAuth.getInstance().getUid(); // get the logged-in tradesman's id

        db = FirebaseFirestore.getInstance();
        loadConfirmedJobs();

        return view;
    }

    private void loadConfirmedJobs() {
        db.collection("jobs")
                .whereEqualTo("jobStatus", "Confirmed")
                .whereEqualTo("tradesmanId", tradesmanId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        confirmedJobs.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Job job = document.toObject(Job.class);
                            confirmedJobs.add(job);
                        }
                        jobAdapter.updateJobs(confirmedJobs);
                    }
                });
    }
}
