package com.example.traderfinder.View.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traderfinder.Model.Job;
import com.example.traderfinder.Controller.JobAdapter;
import com.example.traderfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button buttonCreateJob;
    private RecyclerView recyclerViewJobs;
    private JobAdapter jobAdapter;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buttonCreateJob = view.findViewById(R.id.create_job_button);
        recyclerViewJobs = view.findViewById(R.id.jobs_recycler_view);
        mAuth = FirebaseAuth.getInstance();

        buttonCreateJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserDetails(new CheckUserDetailsCallback() {
                    @Override
                    public void onComplete(boolean isComplete) {
                        if (!isComplete) {
                            Toast.makeText(getActivity(), "Please complete your profile details first!", Toast.LENGTH_SHORT).show();
                        } else {
                            CreateJobDialogFragment createJobDialogFragment = new CreateJobDialogFragment();
                            createJobDialogFragment.show(getParentFragmentManager(), "create_job_dialog");
                        }
                    }
                });
            }
        });

        // Set up the RecyclerView
        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(getActivity()));
        jobAdapter = new JobAdapter(getContext(), new ArrayList<>(), "customer"); // Or "tradesman", depending on the type of account
        recyclerViewJobs.setAdapter(jobAdapter);

        loadJobCards();

        return view;
    }

    private void loadJobCards() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            firestore.collection("jobs")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Job> jobs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Job job = document.toObject(Job.class);
                                job.setId(document.getId());

                                if ("open".equals(job.getJobStatus())) {
                                    jobs.add(job);
                                }
                            }
                            jobAdapter.setJobs(jobs);
                        } else {
                            Toast.makeText(getActivity(), "Error loading jobs: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void checkUserDetails(CheckUserDetailsCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        callback.onComplete(true);
                    } else {
                        callback.onComplete(false);
                    }
                } else {
                    Toast.makeText(getActivity(), "Error checking user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onComplete(false);
                }
            });
        } else {
            Toast.makeText(getActivity(), "No current user found!", Toast.LENGTH_SHORT).show();
            callback.onComplete(false);
        }
    }

    public interface CheckUserDetailsCallback {
        void onComplete(boolean isComplete);
    }
}

