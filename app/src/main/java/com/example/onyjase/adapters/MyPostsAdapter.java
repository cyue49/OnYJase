package com.example.onyjase.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onyjase.databinding.ItemMyPostBinding;
import com.example.onyjase.models.Post;

import java.util.Collections;
import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.PostViewHolder> {

    private List<Post> posts = Collections.emptyList();

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        final ItemMyPostBinding binding;

        public PostViewHolder(ItemMyPostBinding binding) {
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
        Post post = posts.get(position);
        // Load the post image using Glide
        if (post.getImageURL() != null && !post.getImageURL().isEmpty()) {
            Glide.with(holder.binding.postImage.getContext())
                    .load(post.getImageURL())
                    .placeholder(com.example.onyjase.R.drawable.placeholder_image) // Placeholder image
                    .into(holder.binding.postImage);
        } else {
            holder.binding.postImage.setImageResource(com.example.onyjase.R.drawable.placeholder_image); // Placeholder image
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}