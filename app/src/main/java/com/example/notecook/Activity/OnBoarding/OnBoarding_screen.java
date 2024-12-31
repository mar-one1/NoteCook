package com.example.notecook.Activity.OnBoarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Activity.MainActivity;
import com.example.notecook.R;
import com.example.notecook.Utils.SecurePreferences;

public class OnBoarding_screen extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private Button nextButton;
    private static final int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen);

        viewPager = findViewById(R.id.viewPager);
        nextButton = findViewById(R.id.nextButton);

        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == NUM_PAGES - 1) {
                    nextButton.setText("Finish");
                } else {
                    nextButton.setText("Next");
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < NUM_PAGES - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    completeOnboarding();
                }
            }
        });
    }

    private void completeOnboarding() {
//        SecurePreferences.savePrefData("isFirstRun",false);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstRun", false);
        editor.apply();
        Intent intent = new Intent(OnBoarding_screen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}