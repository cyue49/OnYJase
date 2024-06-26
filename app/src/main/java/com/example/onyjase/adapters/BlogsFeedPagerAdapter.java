package com.example.onyjase.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.onyjase.views.blogs.ExploreFragment;
import com.example.onyjase.views.blogs.FavoritesFragment;
import com.example.onyjase.views.blogs.FollowingFragment;

public class BlogsFeedPagerAdapter extends FragmentStateAdapter {

    public BlogsFeedPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FollowingFragment();
            case 1:
                return new ExploreFragment();
            case 2:
                return new FavoritesFragment();
            default:
                return new ExploreFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
