package com.example.notecook.Activity;

import android.app.Application;

import com.example.notecook.Utils.Constants;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Constants.handleDbChange(this);
    }
}

