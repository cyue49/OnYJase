package com.example.onyjase.views.blogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.onyjase.adapters.BlogAdapter;
import com.example.onyjase.databinding.FragmentFollowingBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.models.User;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Comparator;
import java.util.List;

public class FollowingFragment extends Fragment {

    private FragmentFollowingBinding binding;
    private BlogAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFollowingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new BlogAdapter(viewModel, getActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        loadFollowingBlogs();
    }

    private void loadFollowingBlogs() {
        User currentUser = viewModel.getUser().getValue();
        if (currentUser != null) {
            List<String> followings = currentUser.getFollowings();
            if (followings != null && !followings.isEmpty()) {
                db.collection("blogs")
                        .whereIn("userID", followings)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<Blog> blogs = queryDocumentSnapshots.toObjects(Blog.class);
                            sortBlogsByDate(blogs);
                            adapter.setBlogs(blogs);
                        })
                        .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error fetching blogs", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(requireContext(), "You are not following anyone", Toast.LENGTH_SHORT).show();
            }
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