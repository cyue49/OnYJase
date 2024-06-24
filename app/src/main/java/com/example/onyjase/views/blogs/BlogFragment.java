package com.example.onyjase.views.blogs;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentBlogBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;

// Fragment for a single blog
public class BlogFragment extends Fragment {
    FragmentBlogBinding binding;

    // ui components variables
    ImageView backBtn, editBtn, deleteBtn, likeBtn, coverImg;
    TextView titleTxt, dateTimeTxt, authorTxt, contentTxt, likesTxt;
    TextInputEditText commentInput;
    Button clearBtn, submitBtn;
    RecyclerView commentList;

    // view model
    AppViewModel viewModel;

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
        backBtn = binding.backBtn;
        editBtn = binding.editBtn;
        deleteBtn = binding.deleteBtn;
        likeBtn = binding.likeBtn;
        titleTxt = binding.title;
        dateTimeTxt = binding.dateTime;
        authorTxt = binding.username;
        contentTxt = binding.content;
        likesTxt = binding.likes;
        coverImg = binding.coverImage;
        commentInput = binding.commentInput;
        clearBtn = binding.clearBtn;
        submitBtn = binding.submitBtn;
        commentList = binding.commentsList;
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // display content from current blog of view model
        Blog currentBlog = viewModel.getCurrentBlog().getValue();
        if (currentBlog != null) {
            titleTxt.setText(currentBlog.getTitle());
            contentTxt.setText(currentBlog.getContent());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            dateTimeTxt.setText(formatter.format(currentBlog.getTimestamp()));

        } else {
            loadFragment(new BlogsFeedFragment());
        }

    }

    // go to another fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}