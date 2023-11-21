package com.example.notecook.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class Adapter_Vp2_recipeProfil extends FragmentStateAdapter {

    private ArrayList<Fragment> fragmentss;

    public Adapter_Vp2_recipeProfil(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
  
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentss.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentss.size();
    }

    public void setData(ArrayList<Fragment> fragments) {
        this.fragmentss = fragments;
    }
}
