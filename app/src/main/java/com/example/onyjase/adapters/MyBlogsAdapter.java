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
import com.example.onyjase.databinding.ItemMyBlogBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
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
            FragmentTransactionHelper.loadFragmentFullScreen(context, new BlogFragment());
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
        return blogs.size();
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
        notifyDataSetChanged();
    }
}