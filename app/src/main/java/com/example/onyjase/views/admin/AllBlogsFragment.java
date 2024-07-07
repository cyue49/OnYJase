package com.example.onyjase.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.onyjase.adapters.AllBlogsAdapter;
import com.example.onyjase.databinding.FragmentAllBlogsBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AllBlogsFragment extends Fragment {

    private FragmentAllBlogsBinding binding;
    private AllBlogsAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllBlogsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new AllBlogsAdapter(getActivity(), new AllBlogsAdapter.OnBlogInteractionListener() {
            @Override
            public void onBlogClick(Blog blog) {
                viewModel.setCurrentBlog(blog);
                FragmentTransactionHelper.loadFragment(requireContext(), new BlogFragment());
            }

            @Override
            public void onEditClick(Blog blog) {
                // Implement edit functionality here
            }

            @Override
            public void onDeleteClick(Blog blog) {
                // Implement delete functionality here
            }
        });

        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);

        loadAllBlogs();
    }

    private void loadAllBlogs() {
        db.collection("blogs").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Blog> blogs = queryDocumentSnapshots.toObjects(Blog.class);
                    adapter.setBlogs(blogs);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}