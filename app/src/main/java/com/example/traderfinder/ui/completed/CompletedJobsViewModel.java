package com.example.traderfinder.ui.completed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CompletedJobsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CompletedJobsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Completed Job fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}