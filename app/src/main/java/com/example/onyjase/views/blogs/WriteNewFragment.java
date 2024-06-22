package com.example.onyjase.views.blogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentWriteNewBinding;

public class WriteNewFragment extends Fragment {
    FragmentWriteNewBinding binding;

    // ui components variables
    ImageView newBlogBtn, newPostBtn;
    TextView newBlogTxt, newPostTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWriteNewBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        newBlogBtn = binding.newBlogBtn;
        newPostBtn = binding.newPostBtn;
        newBlogTxt = binding.newBlogTxt;
        newPostTxt = binding.newPostTxt;

        // new blog
        newBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to new blog page
                loadFragment(new NewBlogFragment());
            }
        });

        newBlogTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to new blog page
                loadFragment(new NewBlogFragment());
            }
        });

        // new post
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to new post page
                loadFragment(new NewPostFragment());
            }
        });

        newPostTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to new post page
                loadFragment(new NewPostFragment());
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