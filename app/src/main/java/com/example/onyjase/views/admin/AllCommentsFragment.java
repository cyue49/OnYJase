package com.example.onyjase.views.admin;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onyjase.R;
import com.example.onyjase.adapters.AllCommentsAdapter;
import com.example.onyjase.databinding.FragmentAllCommentsBinding;
import com.example.onyjase.models.Comment;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AllCommentsFragment extends Fragment {

    private FragmentAllCommentsBinding binding;
    private AllCommentsAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new AllCommentsAdapter(getActivity(), new AllCommentsAdapter.OnCommentInteractionListener() {
            @Override
            public void onCommentClick(Comment comment) {
                viewModel.setCurrentComment(comment);
                // You can define an action here if necessary, or navigate to a different fragment
            }

            @Override
            public void onDeleteClick(Comment comment) {
                showDeleteConfirmationDialog(comment);
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        loadAllComments();
    }

    private void loadAllComments() {
        db.collection("comments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = queryDocumentSnapshots.toObjects(Comment.class);
                    adapter.setComments(comments);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void showDeleteConfirmationDialog(Comment comment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.delete_blog_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> deleteCommentFromDb(comment.getCommentID()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        dialogBuilder.create().show();
    }

    private void deleteCommentFromDb(String commentID) {
        db.collection("comments").document(commentID).delete()
                .addOnSuccessListener(aVoid -> {
                    loadAllComments(); // Reload comments after deletion
                    // Show success message
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