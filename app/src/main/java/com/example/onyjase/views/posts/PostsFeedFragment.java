package com.example.onyjase.views.posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentPostsFeedBinding;

// Fragment for the home feed for admin posts
public class PostsFeedFragment extends Fragment {
    FragmentPostsFeedBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostsFeedBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}