package com.example.onyjase.views.blogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentBlogBinding;
import com.google.android.material.textfield.TextInputEditText;

// Fragment for a single blog
public class BlogFragment extends Fragment {
    FragmentBlogBinding binding;

    // ui components variables
    TextInputEditText commentInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBlogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        commentInput = binding.commentInput;

    }
}