package com.example.onyjase.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.onyjase.R;
import com.example.onyjase.adapters.AllPostsAdapter;
import com.example.onyjase.databinding.FragmentAllPostsBinding;
import com.example.onyjase.models.Post;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.posts.PostFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AllPostsFragment extends Fragment {

    private FragmentAllPostsBinding binding;
    private AllPostsAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new AllPostsAdapter(getActivity(), new AllPostsAdapter.OnPostInteractionListener() {
            @Override
            public void onPostClick(Post post) {
                viewModel.setCurrentPost(post);
                loadFragment(new PostFragment());
            }

            @Override
            public void onEditClick(Post post) {
                // Handle edit post
            }

            @Override
            public void onDeleteClick(Post post) {
                // Handle delete post
            }
        });

        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);

        loadAllPosts();
    }

    private void loadAllPosts() {
        db.collection("posts").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = queryDocumentSnapshots.toObjects(Post.class);
                    adapter.setPosts(posts);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
