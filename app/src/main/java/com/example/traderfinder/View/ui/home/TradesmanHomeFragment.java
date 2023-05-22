package com.example.traderfinder.View.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traderfinder.Model.Job;
import com.example.traderfinder.Controller.JobAdapter;
import com.example.traderfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TradesmanHomeFragment extends Fragment implements FilterDialogFragment.FilterDialogListener {

    private Button buttonFilter;
    private RecyclerView recyclerViewJobs;
    private JobAdapter jobAdapter;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String currentTradeFilter = "";
    private String currentLocationFilter = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tradesman_home, container, false);

        buttonFilter = view.findViewById(R.id.filter_button);
        recyclerViewJobs = view.findViewById(R.id.jobs_recycler_view);

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialogFragment filterDialogFragment = new FilterDialogFragment(TradesmanHomeFragment.this);
                filterDialogFragment.show(getParentFragmentManager(), "filter_dialog");
            }
        });

        // Set up the RecyclerView
        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(getActivity()));
        jobAdapter = new JobAdapter(getContext(), new ArrayList<>(), "tradesman");
        recyclerViewJobs.setAdapter(jobAdapter);

        // Load all jobs initially
        loadJobCards();

        return view;
    }

    private void loadJobCards() {
        firestore.collection("jobs")
                .whereEqualTo("jobStatus", "open") // add this line
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Job> jobs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Job job = document.toObject(Job.class);
                            if ((currentTradeFilter.isEmpty() || job.getTrade().equals(currentTradeFilter)) &&
                                    (currentLocationFilter.isEmpty() || job.getJobLocation().equalsIgnoreCase(currentLocationFilter))) {
                                jobs.add(job);
                            }
                        }
                        jobAdapter.setJobs(jobs);
                    }
                });
    }

    @Override
    public void onFilterApplied(String trade, String location) {
        currentTradeFilter = trade;
        currentLocationFilter = location;

        // Refresh the job cards
        loadJobCards();
    }
}
