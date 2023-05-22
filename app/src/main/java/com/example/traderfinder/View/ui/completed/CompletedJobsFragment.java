package com.example.traderfinder.View.ui.completed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traderfinder.Model.Job;
import com.example.traderfinder.Controller.JobAdapter;
import com.example.traderfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompletedJobsFragment extends Fragment {
    private RecyclerView completedRecyclerView;
    private JobAdapter jobAdapter;
    private FirebaseFirestore db;
    private List<Job> completedJobs;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_jobs, container, false);

        completedRecyclerView = view.findViewById(R.id.completed_jobs_recyclerview);
        completedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        completedJobs = new ArrayList<>();
        jobAdapter = new JobAdapter(getContext(), new ArrayList<>(), "general"); // Or "tradesman", depending on the type of account
        completedRecyclerView.setAdapter(jobAdapter);

        userId = FirebaseAuth.getInstance().getUid(); // get the logged-in user's id

        db = FirebaseFirestore.getInstance();
        loadCompletedJobs();

        return view;
    }

    private void loadCompletedJobs() {
        db.collection("jobs")
                .whereEqualTo("jobStatus", "Completed")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        completedJobs.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Job job = document.toObject(Job.class);
                            completedJobs.add(job);
                        }
                        jobAdapter.updateJobs(completedJobs);
                    }
                });
    }
}
