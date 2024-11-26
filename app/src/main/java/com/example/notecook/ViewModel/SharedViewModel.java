package com.example.notecook.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Model.Ingredients;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Ingredients>> liveData = new MutableLiveData<>();

    public LiveData<List<Ingredients>> getData() {
        return liveData;
    }

    public void setData(List<Ingredients> data) {
        liveData.setValue(data);
    }
}

