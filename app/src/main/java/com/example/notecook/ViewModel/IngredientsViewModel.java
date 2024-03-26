package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.Closeable;

public class IngredientsViewModel extends ViewModel implements ViewModelProvider.Factory {

    private Context context;
    private Activity appCompatActivity;

    public IngredientsViewModel(Context context, Activity appCompatActivity, @NonNull Closeable... closeables) {
        super(closeables);
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(IngredientsViewModel.class)) {
            return (T) new IngredientsViewModel(context,appCompatActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
