package com.example.onyjase.views.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.posts.EditPostFragment;
import com.example.onyjase.views.posts.PostFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AllPostsFragment extends Fragment {

    private FragmentAllPostsBinding binding;
    private AllPostsAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

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
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new AllPostsAdapter(getActivity(), new AllPostsAdapter.OnPostInteractionListener() {
            @Override
            public void onPostClick(Post post) {
                viewModel.setCurrentPost(post);
                viewModel.setCurrentPostID(post.getPostID());
                FragmentTransactionHelper.loadFragment(requireContext(), new PostFragment());
            }

            @Override
            public void onEditClick(Post post) {
                viewModel.setCurrentPost(post);
                FragmentTransactionHelper.loadFragment(requireContext(), new EditPostFragment());
            }

            @Override
            public void onDeleteClick(Post post) {
                showDeleteConfirmationDialog(post);
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
                    Toast.makeText(requireContext(), "Failed to load posts. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void showDeleteConfirmationDialog(Post post) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.delete_blog_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> deletePostFromDb(post.getPostID()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        dialogBuilder.create().show();
    }

    private void deletePostFromDb(String postID) {
        // delete post cover image from storage
        String postImgUrl = "posts/" + postID;
        StorageReference listRef = storage.getReference().child(postImgUrl);
        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()){
                        item.delete();
                    }
                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error deleting post images.", Toast.LENGTH_SHORT).show());

        // delete post from db
        db.collection("posts").document(postID).delete()
                .addOnSuccessListener(aVoid -> {
                    loadAllPosts(); // Reload posts after deletion
                    // Show success message
                    Toast.makeText(requireContext(), "Post deleted.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error deleting post.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
