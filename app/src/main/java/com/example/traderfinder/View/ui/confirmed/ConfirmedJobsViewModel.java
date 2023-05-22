package com.example.traderfinder.View.ui.confirmed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfirmedJobsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ConfirmedJobsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Confirmed Jobs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}