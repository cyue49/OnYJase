package com.example.onyjase.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemMyPostBinding;
import com.example.onyjase.models.Blog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.PostViewHolder> {

    private List<Blog> posts = new ArrayList<>();
    private final OnPostInteractionListener listener;
    private final FirebaseStorage storage;

    public interface OnPostInteractionListener {
        void onEditClick(Blog post);
        void onDeleteClick(Blog post);
        void onPostClick(Blog post);
    }

    public MyPostsAdapter(OnPostInteractionListener listener) {
        this.listener = listener;
        this.storage = FirebaseStorage.getInstance();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyPostBinding binding;

        public PostViewHolder(@NonNull ItemMyPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyPostBinding binding = ItemMyPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Blog post = posts.get(position);
        holder.binding.postTitle.setText(post.getTitle());

        // Get the image reference from Firebase Storage using the path stored in Firestore
        if (post.getImageURL() != null && !post.getImageURL().isEmpty()) {
            StorageReference imageRef = storage.getReference().child(post.getImageURL());
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(holder.binding.postImage.getContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(holder.binding.postImage);
            }).addOnFailureListener(e -> holder.binding.postImage.setImageResource(R.drawable.ic_user_placeholder)); // Placeholder image
        } else {
            holder.binding.postImage.setImageResource(R.drawable.ic_user_placeholder); // Placeholder image
        }

        holder.binding.moreOptionsButton.setOnClickListener(view -> showPopupMenu(view, post));

        holder.binding.postImage.setOnClickListener(view -> listener.onPostClick(post));
    }

    private void showPopupMenu(View view, Blog post) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.popup_menu);
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    listener.onEditClick(post);
                    return true;
                case R.id.action_delete:
                    listener.onDeleteClick(post);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Blog> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}