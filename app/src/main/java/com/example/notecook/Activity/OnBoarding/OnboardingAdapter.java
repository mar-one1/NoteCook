package com.example.notecook.Activity.OnBoarding;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OnBoardingFragmentA();
            case 1:
                return new OnBoardingFragmentB();
            case 2:
                return new OnboardingFragmentC();
            default:
                return new OnBoardingFragmentA();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of onboarding screens
    }
}
