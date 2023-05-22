package com.example.traderfinder.ui.confirmed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traderfinder.Job;
import com.example.traderfinder.JobAdapter;
import com.example.traderfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedJobsFragment extends Fragment {
    private RecyclerView confirmedRecyclerView;
    private JobAdapter jobAdapter;
    private FirebaseFirestore db;
    private List<Job> confirmedJobs;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmed_jobs, container, false);

        confirmedRecyclerView = view.findViewById(R.id.confirmed_jobs_recycler_view);
        confirmedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        confirmedJobs = new ArrayList<>();
        jobAdapter = new JobAdapter(getContext(), new ArrayList<>(), "customer"); // Or "tradesman", depending on the type of account
        confirmedRecyclerView.setAdapter(jobAdapter);

        userId = FirebaseAuth.getInstance().getUid(); // get the logged-in user's id

        db = FirebaseFirestore.getInstance();
        loadConfirmedJobs();

        return view;
    }

    private void loadConfirmedJobs() {
        db.collection("jobs")
                .whereEqualTo("jobStatus", "Confirmed")
                .whereEqualTo("userId", userId)
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
