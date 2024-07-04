package com.example.onyjase.adapters;

// adapter for list of posts in posts home feed
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
import com.example.onyjase.databinding.ItemPostBinding;
import com.example.onyjase.models.Post;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.posts.PostFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private AppViewModel viewModel;
    private Activity context;
    private FirebaseStorage storage;

    public PostAdapter(AppViewModel viewModel, Activity context) {
        this.viewModel = viewModel;
        this.context = context;
        this.posts = List.of(); // Initialize with an empty list
        this.storage = FirebaseStorage.getInstance();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;

        public PostViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.binding.postTitle.setText(post.getTitle());

        // Get the image reference from Firebase Storage using the path stored in Firestore
        if (post.getImageURL() != null && !post.getImageURL().isEmpty()) {
            StorageReference imageRef = storage.getReference().child(post.getImageURL());
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.placeholder_image) // Placeholder image
                        .into(holder.binding.postImage);
            }).addOnFailureListener(e -> holder.binding.postImage.setImageResource(R.drawable.placeholder_image)); // Placeholder image
        } else {
            holder.binding.postImage.setImageResource(R.drawable.placeholder_image); // Placeholder image
        }

        // Set click listeners to navigate to PostFragment
        View.OnClickListener listener = v -> {
            viewModel.setCurrentPostID(post.getPostID());
            loadFragment(new PostFragment());
        };
        holder.binding.postImage.setOnClickListener(listener);
        holder.binding.postTitle.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}