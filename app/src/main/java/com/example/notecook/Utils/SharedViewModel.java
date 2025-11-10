package com.example.notecook.Utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> showFab = new MutableLiveData<>();

    public void setShowFab(boolean show) {
        showFab.setValue(show);
    }

    public LiveData<Boolean> getShowFab() {
        return showFab;
    }
}