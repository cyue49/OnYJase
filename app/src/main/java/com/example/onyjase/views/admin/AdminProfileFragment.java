package com.example.onyjase.views.admin;

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
import com.example.onyjase.databinding.FragmentAdminProfileBinding;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class AdminProfileFragment extends Fragment {
    private FragmentAdminProfileBinding binding;
    private AppViewModel viewModel;
    private FirebaseAuth mAuth;
    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);

        // Set admin details
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.adminUsername.setText(user.getUsername());
                binding.adminUserID.setText("Admin ID: " + user.getUserID());
                if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
                    Glide.with(requireContext())
                            .load(user.getImageURL())
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(binding.adminProfilePhoto);
                }
            }
        });

        // Set up logout button
        binding.adminLogoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            viewModel.setUser(null);
            navController.navigate(R.id.action_appFragment_to_signInFragment);
        });

        // Set up ViewPager with TabLayout
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        binding.adminViewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.adminTabLayout, binding.adminViewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Users");
            } else if (position == 1) {
                tab.setText("Blogs");
            } else if (position == 2) {
                tab.setText("Posts");
            }else if (position == 3) {
                tab.setText("Comments");
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
            switch (position) {
                case 0:
                    return new AllUsersFragment();
                case 1:
                    return new AllBlogsFragment();
                case 2:
                    return new AllBlogsFragment(); // To be replaced
                case 3:
                    return new AllBlogsFragment(); // To be replaced
                default:
                    return new AllUsersFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
