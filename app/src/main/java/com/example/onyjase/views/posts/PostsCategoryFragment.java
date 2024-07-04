package com.example.onyjase.views.posts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onyjase.R;
import com.example.onyjase.adapters.PostAdapter;
import com.example.onyjase.models.Post;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostsCategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;
    private String category;

    public static PostsCategoryFragment newInstance(String category) {
        PostsCategoryFragment fragment = new PostsCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new PostAdapter(viewModel, requireActivity());
        recyclerView.setAdapter(adapter);

        loadPosts();
    }

    private void loadPosts() {
        db.collection("posts").whereEqualTo("tag", category).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = queryDocumentSnapshots.toObjects(Post.class);
                    adapter.setPosts(posts);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}
