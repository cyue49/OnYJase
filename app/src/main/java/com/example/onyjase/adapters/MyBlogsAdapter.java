package com.example.onyjase.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemMyBlogBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.example.onyjase.views.blogs.EditFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MyBlogsAdapter extends RecyclerView.Adapter<MyBlogsAdapter.BlogViewHolder> {

    private List<Blog> blogs;
    private AppViewModel viewModel;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public MyBlogsAdapter(AppViewModel viewModel, Activity context) {
        this.viewModel = viewModel;
        this.context = context;
        this.blogs = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyBlogBinding binding;

        public BlogViewHolder(@NonNull ItemMyBlogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyBlogBinding binding = ItemMyBlogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BlogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogs.get(position);
        holder.binding.blogTitle.setText(blog.getTitle());

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
            FragmentTransactionHelper.loadFragment(context, new BlogFragment());
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
                        viewModel.setCurrentBlog(blog);
                        FragmentTransactionHelper.loadFragment(context, new EditFragment());
                        return true;
                    case R.id.action_delete:
                        showDeleteConfirmationDialog(blog);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    private void showDeleteConfirmationDialog(Blog blog) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.delete_blog_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> deleteBlogFromDb(blog.getBlogID()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        dialogBuilder.create().show();
    }

    private void deleteBlogFromDb(String blogID) {
        firestore.collection("blogs").document(blogID).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Blog deleted successfully", Toast.LENGTH_SHORT).show();
                    loadUserPosts(viewModel.getUser().getValue().getUserID()); // Reload user posts
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error deleting blog", Toast.LENGTH_SHORT).show());
    }

    private void loadUserPosts(String userId) {
        firestore.collection("blogs").whereEqualTo("userID", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Blog> posts = queryDocumentSnapshots.toObjects(Blog.class);
                    setBlogs(posts);
                })
                .addOnFailureListener(e -> {
                    // Handle error
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
}