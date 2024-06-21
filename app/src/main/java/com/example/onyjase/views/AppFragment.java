package com.example.onyjase.views;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentAppBinding;
import com.example.onyjase.views.blogs.BlogsFeedFragment;
import com.example.onyjase.views.blogs.NewBlogFragment;
import com.example.onyjase.views.notifications.NotificationsFragment;
import com.example.onyjase.views.posts.PostsFeedFragment;
import com.example.onyjase.views.user.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

// Fragment for the app once signed in
public class AppFragment extends Fragment {
    FragmentAppBinding binding;
    BottomNavigationView bottomNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFragment(new BlogsFeedFragment());

        bottomNav = binding.bottomNav;

        bottomNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.home:
                    loadFragment(new BlogsFeedFragment());
                    break;

                case R.id.info:
                    loadFragment(new PostsFeedFragment());
                    break;

                case R.id.add:
                    loadFragment(new NewBlogFragment());
                    break;

                case R.id.notification:
                    loadFragment(new NotificationsFragment());
                    break;

                case R.id.profile:
                    loadFragment(new UserProfileFragment());
                    break;
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}