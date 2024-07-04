package com.example.onyjase.views.posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.onyjase.databinding.FragmentPostsFeedBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PostsFeedFragment extends Fragment {

    private FragmentPostsFeedBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostsFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the ViewPager with the sections adapter.
        binding.viewPager.setAdapter(new SectionsPagerAdapter(this));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Learn");
                            break;
                        case 1:
                            tab.setText("Exam");
                            break;
                        case 2:
                            tab.setText("Bill 96");
                            break;
                        case 3:
                            tab.setText("Other");
                            break;
                    }
                }).attach();
    }

    private static class SectionsPagerAdapter extends FragmentStateAdapter {

        public SectionsPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return PostsCategoryFragment.newInstance("learn");
                case 1:
                    return PostsCategoryFragment.newInstance("exam");
                case 2:
                    return PostsCategoryFragment.newInstance("bill96");
                case 3:
                    return PostsCategoryFragment.newInstance("other");
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}