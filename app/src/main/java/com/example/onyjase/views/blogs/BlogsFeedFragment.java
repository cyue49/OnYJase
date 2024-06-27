package com.example.onyjase.views.blogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.onyjase.R;
import com.example.onyjase.adapters.BlogsFeedPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BlogsFeedFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BlogsFeedPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blogs_feed, container, false);

        // Initialize TabLayout and ViewPager
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set up the ViewPager with the sections adapter.
        pagerAdapter = new BlogsFeedPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Attach TabLayout and ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Following");
                    break;
                case 1:
                    tab.setText("Explore");
                    break;
                case 2:
                    tab.setText("Favorites");
                    break;
            }
        }).attach();

        return view;
    }
}