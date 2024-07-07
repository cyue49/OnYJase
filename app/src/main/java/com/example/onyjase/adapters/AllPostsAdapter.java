package com.example.onyjase.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemAllPostBinding;
import com.example.onyjase.models.Post;
import com.example.onyjase.viewmodels.AppViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AllPostsAdapter extends RecyclerView.Adapter<AllPostsAdapter.PostViewHolder> {

    private List<Post> posts;
    private AppViewModel viewModel;
    private FragmentActivity context;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private OnPostInteractionListener listener;

    public AllPostsAdapter(FragmentActivity context, OnPostInteractionListener listener) {
        this.listener = listener;
        this.context = context;
        this.posts = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public interface OnPostInteractionListener {
        void onPostClick(Post post);
        void onEditClick(Post post);
        void onDeleteClick(Post post);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ItemAllPostBinding binding;

        public PostViewHolder(@NonNull ItemAllPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAllPostBinding binding = ItemAllPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.binding.postTitle.setText(post.getTitle());

        // Fetch the username using userID from Firestore
        String userId = post.getUserID();
        if (userId != null && !userId.isEmpty()) {
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            holder.binding.postUsername.setText(username != null ? username : "Unknown");
                        } else {
                            holder.binding.postUsername.setText("Unknown");
                        }
                    })
                    .addOnFailureListener(e -> holder.binding.postUsername.setText("Unknown"));
        } else {
            holder.binding.postUsername.setText("Unknown");
        }

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
            this.listener.onPostClick(post);
        };
        holder.binding.postImage.setOnClickListener(listener);
        holder.binding.postTitle.setOnClickListener(listener);

        // Set up more options button
        holder.binding.moreOptionsButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.moreOptionsButton);
            popupMenu.inflate(R.menu.popup_menu); // Make sure to have this menu resource
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit:
                        this.listener.onEditClick(post);
                        return true;
                    case R.id.action_delete:
                        this.listener.onDeleteClick(post);
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
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}
