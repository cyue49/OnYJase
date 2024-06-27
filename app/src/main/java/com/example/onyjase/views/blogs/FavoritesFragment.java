package com.example.onyjase.views.blogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.onyjase.adapters.BlogAdapter;
import com.example.onyjase.databinding.FragmentExploreBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private FragmentExploreBinding binding; // Reusing the same layout for simplicity
    private BlogAdapter blogAdapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        db = FirebaseFirestore.getInstance();

        blogAdapter = new BlogAdapter(viewModel, requireActivity());
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(blogAdapter);

        loadFavoriteBlogs();
    }

    private void loadFavoriteBlogs() {
        String userId = viewModel.getUser().getValue().getUserID();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> favoriteBlogIds = (List<String>) task.getResult().get("favorites");
                if (favoriteBlogIds != null && !favoriteBlogIds.isEmpty()) {
                    fetchFavoriteBlogs(favoriteBlogIds);
                }
            }
        });
    }

    private void fetchFavoriteBlogs(List<String> blogIds) {
        List<Blog> favoriteBlogs = new ArrayList<>();
        for (String blogId : blogIds) {
            db.collection("blogs").document(blogId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Blog blog = task.getResult().toObject(Blog.class);
                    if (blog != null) {
                        favoriteBlogs.add(blog);
                    }
                    if (favoriteBlogs.size() == blogIds.size()) {
                        blogAdapter.setBlogs(favoriteBlogs);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}