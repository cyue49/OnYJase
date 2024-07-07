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

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemAllBlogBinding;
import com.example.onyjase.models.Blog;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AllBlogsAdapter extends RecyclerView.Adapter<AllBlogsAdapter.BlogViewHolder> {

    private List<Blog> blogs;
    private final FragmentActivity context;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    public interface OnBlogInteractionListener {
        void onBlogClick(Blog blog);
        void onEditClick(Blog blog);
        void onDeleteClick(Blog blog);
    }

    private final OnBlogInteractionListener listener;

    public AllBlogsAdapter(FragmentActivity context, OnBlogInteractionListener listener) {
        this.context = context;
        this.listener = listener;
        this.blogs = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        private final ItemAllBlogBinding binding;

        public BlogViewHolder(@NonNull ItemAllBlogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAllBlogBinding binding = ItemAllBlogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BlogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogs.get(position);
        holder.binding.blogTitle.setText(blog.getTitle());

        // Fetch the username using userID from Firestore
        String userId = blog.getUserID();
        if (userId != null && !userId.isEmpty()) {
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            holder.binding.blogUsername.setText(username != null ? username : "Unknown");
                        } else {
                            holder.binding.blogUsername.setText("Unknown");
                        }
                    })
                    .addOnFailureListener(e -> holder.binding.blogUsername.setText("Unknown"));
        } else {
            holder.binding.blogUsername.setText("Unknown");
        }

        // Get the image reference from Firebase Storage using the path stored in Firestore
        if (blog.getImageURL() != null && !blog.getImageURL().isEmpty()) {
            StorageReference imageRef = storage.getReference().child(blog.getImageURL());
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.placeholder_image) // Placeholder image
                        .into(holder.binding.blogImage);
            }).addOnFailureListener(e -> holder.binding.blogImage.setImageResource(R.drawable.placeholder_image)); // Placeholder image
        } else {
            holder.binding.blogImage.setImageResource(R.drawable.placeholder_image); // Placeholder image
        }

        // Set click listeners to navigate to BlogFragment
        View.OnClickListener listener = v -> {
            this.listener.onBlogClick(blog);
        };
        holder.binding.blogImage.setOnClickListener(listener);
        holder.binding.blogTitle.setOnClickListener(listener);

        // Set up more options button
        holder.binding.moreOptionsButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.moreOptionsButton);
            popupMenu.inflate(R.menu.popup_menu); // Make sure to have this menu resource
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit:
                        this.listener.onEditClick(blog);
                        return true;
                    case R.id.action_delete:
                        this.listener.onDeleteClick(blog);
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
        return blogs.size();
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
        notifyDataSetChanged();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}