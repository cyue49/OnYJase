package com.example.onyjase.views.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentMyCommentsBinding;

// Fragment for the My Comments section in user profile
public class MyCommentsFragment extends Fragment {
    FragmentMyCommentsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyCommentsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}