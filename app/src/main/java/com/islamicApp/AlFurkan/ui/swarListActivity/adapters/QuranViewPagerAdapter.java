package com.islamicApp.AlFurkan.ui.swarListActivity.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.islamicApp.AlFurkan.ui.swarListActivity.fragments.AllSwarFragment;
import com.islamicApp.AlFurkan.ui.swarListActivity.fragments.markedAyat.MarkedAyatFragment;

public class QuranViewPagerAdapter extends FragmentStateAdapter {

    int count = 2;
    public QuranViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new MarkedAyatFragment();
        }
        return new AllSwarFragment();
    }

    @Override
    public int getItemCount() {
        return count;
    }
}
