package com.example.onyjase.views.blogs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentFollowingBinding;

// Fragment for the following section of the home feed for blogs
public class FollowingFragment extends Fragment {
    FragmentFollowingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}