package com.example.notecook.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AdapterFragment extends FragmentStateAdapter {

    private static ArrayList<Fragment> fragmentss;

    public AdapterFragment(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        return fragmentss.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentss.size();
    }

    public static void setData(ArrayList<Fragment> fragments) {
        fragmentss = fragments;
    }
}
