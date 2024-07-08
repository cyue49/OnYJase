package com.example.onyjase.views.blogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.onyjase.adapters.BlogAdapter;
import com.example.onyjase.databinding.FragmentFavoritesBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private BlogAdapter blogAdapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
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
        Log.d("FavoritesFragment", "Loading favorites for user: " + userId);
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> favoriteBlogIds = (List<String>) task.getResult().get("favorites");
                if (favoriteBlogIds != null && !favoriteBlogIds.isEmpty()) {
                    Log.d("FavoritesFragment", "Favorite blog IDs: " + favoriteBlogIds);
                    fetchFavoriteBlogs(favoriteBlogIds);
                } else {
                    Log.d("FavoritesFragment", "No favorite blogs found.");
                }
            } else {
                Log.e("FavoritesFragment", "Error fetching favorite blogs", task.getException());
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
                        sortBlogsByDate(favoriteBlogs);
                        blogAdapter.setBlogs(favoriteBlogs);
                    }
                } else {
                    Log.e("FavoritesFragment", "Error fetching blog with ID: " + blogId, task.getException());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // sort list of blog by timestamp, with the latest first
    private void sortBlogsByDate(List<Blog> blogs) {
        blogs.sort(new Comparator<Blog>() {
            @Override
            public int compare(Blog o1, Blog o2) {
                if (o1.getTimestamp().equals(o2.getTimestamp())) return 0;
                return o1.getTimestamp().compareTo(o2.getTimestamp()) * -1;
            }
        });
    }
}