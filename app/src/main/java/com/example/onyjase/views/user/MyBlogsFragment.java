package com.example.onyjase.views.user;

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
import com.example.onyjase.adapters.MyBlogsAdapter;
import com.example.onyjase.databinding.FragmentMyPostsBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyBlogsFragment extends Fragment {

    private FragmentMyPostsBinding binding;
    private MyBlogsAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new MyBlogsAdapter(
                viewModel,
                getActivity()
        );

        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);

        String userId = viewModel.getUser().getValue().getUserID();
        loadUserPosts(userId);
    }

    private void loadUserPosts(String userId) {
        db.collection("blogs").whereEqualTo("userID", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Blog> posts = queryDocumentSnapshots.toObjects(Blog.class);
                    adapter.setBlogs(posts);
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