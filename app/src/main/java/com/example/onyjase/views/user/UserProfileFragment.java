package com.example.onyjase.views.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentUserProfileBinding;
import com.example.onyjase.models.User;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.user.MyPostsFragment;
import com.example.onyjase.views.user.MyCommentsFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class UserProfileFragment extends Fragment {
    private FragmentUserProfileBinding binding;
    private AppViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // Set user details
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.username.setText(user.getUsername());
                binding.userID.setText(user.getUserID());
                if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
                    Glide.with(requireContext())
                            .load(user.getImageURL())
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(binding.profilePhoto);
                }
            }
        });

        // Set up logout button
        /*binding.logoutButton.setOnClickListener(v -> {
            viewModel.setUser(null);
            NavHostFragment.findNavController(this).navigate(R.id.action_userProfileFragment_to_signInFragment);
        });*/

        // Set up ViewPager with TabLayout
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("My Posts");
            } else if (position == 1) {
                tab.setText("My Comments");
            }
        }).attach();
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new MyPostsFragment();
            } else {
                return new MyCommentsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}