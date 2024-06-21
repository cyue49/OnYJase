package com.example.onyjase.views.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentMyPostsBinding;

// Fragment for the My Posts section in user profile
public class MyPostsFragment extends Fragment {
    FragmentMyPostsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyPostsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}