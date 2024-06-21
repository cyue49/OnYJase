package com.example.onyjase.views;

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFragment(new BlogsFeedFragment());

        bottomNav = binding.bottomNav;
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.home:
//                        loadFragment(new BlogsFeedFragment());
//                        return true;
//
//                    case R.id.info:
//                        loadFragment(new PostsFeedFragment());
//                        return true;
//
//                    case R.id.add:
//                        loadFragment(new NewBlogFragment());
//                        return true;
//
//                    case R.id.notification:
//                        loadFragment(new NotificationsFragment());
//                        return true;
//
//                    case R.id.profile:
//                        loadFragment(new UserProfileFragment());
//                        return true;
//                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}