package com.example.enduranceacademyapp.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter2 extends FragmentStateAdapter {

    public ViewPagerAdapter2(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AddRoutineFragment();
            default:
                return new AddRoutineFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 1; // Número de fragmentos
    }
}
