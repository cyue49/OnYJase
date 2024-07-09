package com.example.onyjase.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemAllCommentBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.models.Comment;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AllCommentsAdapter extends RecyclerView.Adapter<AllCommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private AppViewModel viewModel;
    private Activity context;
    private FirebaseFirestore firestore;
    private final OnCommentInteractionListener listener;

    public interface OnCommentInteractionListener {
        void onCommentClick(Comment comment);
        void onDeleteClick(Comment comment);
    }

    public AllCommentsAdapter(FragmentActivity context, AppViewModel viewModel, OnCommentInteractionListener listener) {
        this.context = context;
        this.viewModel = viewModel;
        this.listener = listener;
        this.comments = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
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

        // Set click listeners to navigate to the original blog
        holder.binding.getRoot().setOnClickListener(v -> navigateToBlog(comment.getBlogID()));

        // Set up more options button
        holder.binding.moreOptionsButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.moreOptionsButton);
            popupMenu.inflate(R.menu.popup_menu_delete_only); // Ensure this menu resource only has the delete option
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.action_delete) {
                    listener.onDeleteClick(comment);
                    return true;
                }
                return false;
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

    private void navigateToBlog(String blogID) {
        firestore.collection("blogs").document(blogID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Blog blog = documentSnapshot.toObject(Blog.class);
                        if (blog != null) {
                            viewModel.setCurrentBlogID(blogID);
                            viewModel.setCurrentBlog(blog);
                            loadFragment(new BlogFragment());
                        }
                    } else {
                        Toast.makeText(context, "Blog not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error fetching blog", Toast.LENGTH_SHORT).show());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}