package com.example.onyjase.views.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.onyjase.R;
import com.example.onyjase.adapters.AllBlogsAdapter;
import com.example.onyjase.databinding.FragmentAllBlogsBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.example.onyjase.views.blogs.EditFragment;
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
                viewModel.setCurrentBlogID(blog.getBlogID());
                FragmentTransactionHelper.loadFragment(requireContext(), new BlogFragment());
            }

            @Override
            public void onEditClick(Blog blog) {
                viewModel.setCurrentBlog(blog);
                FragmentTransactionHelper.loadFragment(requireContext(), new EditFragment());
            }

            @Override
            public void onDeleteClick(Blog blog) {
                showDeleteConfirmationDialog(blog);
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

    private void showDeleteConfirmationDialog(Blog blog) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        dialogBuilder.setView(inflater.inflate(R.layout.delete_blog_dialog, null));
        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> {
            deleteBlog(blog);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        dialogBuilder.create().show();
    }

    private void deleteBlog(Blog blog) {
        db.collection("blogs").document(blog.getBlogID()).delete()
                .addOnSuccessListener(aVoid -> {
                    loadAllBlogs();
                    // Additional cleanup operations if necessary
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });

    }
}