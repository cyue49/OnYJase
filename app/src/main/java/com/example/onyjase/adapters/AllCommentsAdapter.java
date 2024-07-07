package com.example.onyjase.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemAllCommentBinding;
import com.example.onyjase.models.Comment;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.CommentsFragment;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class AllCommentsAdapter extends RecyclerView.Adapter<AllCommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private AppViewModel viewModel;
    private Activity context;
    private FirebaseFirestore firestore;

    public AllCommentsAdapter(AppViewModel viewModel, Activity context) {
        this.viewModel = viewModel;
        this.context = context;
        this.comments = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
    }

    public interface OnCommentInteractionListener {
        void onCommentClick(Comment comment);
        void onEditClick(Comment comment);
        void onDeleteClick(Comment comment);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAllCommentBinding binding;

        public CommentViewHolder(@NonNull ItemAllCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAllCommentBinding binding = ItemAllCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.binding.commentContent.setText(comment.getContent());

        // Fetch the username using userID from Firestore
        String userId = comment.getUserID();
        if (userId != null && !userId.isEmpty()) {
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            holder.binding.commentUsername.setText(username != null ? username : "Unknown");
                        } else {
                            holder.binding.commentUsername.setText("Unknown");
                        }
                    })
                    .addOnFailureListener(e -> holder.binding.commentUsername.setText("Unknown"));
        } else {
            holder.binding.commentUsername.setText("Unknown");
        }

        // Set click listeners to navigate to CommentFragment
        View.OnClickListener listener = v -> {
            viewModel.setCurrentComment(comment);
            loadFragment(new CommentsFragment());
        };
        holder.binding.commentContent.setOnClickListener(listener);

        // Set up more options button
        holder.binding.moreOptionsButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.moreOptionsButton);
            popupMenu.inflate(R.menu.popup_menu); // Make sure to have this menu resource
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit:
                        // Handle edit option
                        return true;
                    case R.id.action_delete:
                        // Handle delete option
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
