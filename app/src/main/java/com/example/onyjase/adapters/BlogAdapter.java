package com.example.onyjase.adapters;

// adapter for list of blogs in blogs home feed
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemBlogBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<Blog> blogs;
    private AppViewModel viewModel;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public BlogAdapter(AppViewModel viewModel, Activity context) {
        this.viewModel = viewModel;
        this.context = context;
        this.blogs = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        private final ItemBlogBinding binding;

        public BlogViewHolder(@NonNull ItemBlogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBlogBinding binding = ItemBlogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BlogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogs.get(position);
        holder.binding.blogTitle.setText(blog.getTitle());

        // Fetch the username using userID from Firestore
        String userId = blog.getUserID();
        if (userId != null && !userId.isEmpty()) {
            // Fetch the username using userID from Firestore
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
            viewModel.setCurrentBlogID(blog.getBlogID());
            loadFragment(new BlogFragment());
        };
        holder.binding.blogImage.setOnClickListener(listener);
        holder.binding.blogTitle.setOnClickListener(listener);
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
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}