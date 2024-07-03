package com.example.onyjase.views.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentUserProfileBinding;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfileFragment extends Fragment {
    private FragmentUserProfileBinding binding;
    private AppViewModel viewModel;
    private FirebaseAuth mAuth;
    NavController navController;

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
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);

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

        // Set up ViewPager with TabLayout
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("My Blogs");
            } else if (position == 1) {
                tab.setText("My Comments");
            }
        }).attach();

        // click listener for logout button
        binding.logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            viewModel.setUser(null);
            navController.navigate(R.id.action_appFragment_to_signInFragment);
        });
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new MyBlogsFragment();
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