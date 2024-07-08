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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
        // delete all comments of this blog
        deleteAllBlogComments(blogID);

        // delete blog cover image
        deleteBlogImages(blogID);

        // delete this blog from other user's favorites
        DocumentReference docRef = firestore.collection("blogs").document(blogID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // get list of all user ids of users that liked this blog
                    List<String> likedBy = (List<String>) document.get("likedBy");
                    for (String id : likedBy) { // for each user id, delete blogID from their favorites
                        firestore.collection("users").document(id)
                                .update("favorites", FieldValue.arrayRemove(blogID)).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        // do nothing
                                    } else {
                                        Toast.makeText(context, "Error updating favorites.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    // delete the blog from db
                    docRef.delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(context, "Blog deleted.", Toast.LENGTH_SHORT).show();
                                loadUserPosts(viewModel.getUser().getValue().getUserID()); // Reload user posts
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Error deleting blog.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // delete all comments of blog
    private void deleteAllBlogComments(String blogID) {
        CollectionReference colRef = firestore.collection("comments");
        Query query = colRef.whereEqualTo("blogID", blogID);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // delete all comments of this blog
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String commentID = document.getString("commentID");
                    colRef.document(commentID).delete();
                }
            } else {
                Toast.makeText(context, "Error deleting comments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // delete images of this blog
    private void deleteBlogImages(String blogID) {
        String blogImgUrl = "blogs/" + blogID;
        StorageReference listRef = storage.getReference().child(blogImgUrl);
        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()){
                        item.delete();
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, "Error deleting blog images.", Toast.LENGTH_SHORT).show());
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