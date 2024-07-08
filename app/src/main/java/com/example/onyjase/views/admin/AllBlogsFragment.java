package com.example.onyjase.views.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.onyjase.R;
import com.example.onyjase.adapters.AllBlogsAdapter;
import com.example.onyjase.databinding.FragmentAllBlogsBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.example.onyjase.views.blogs.BlogsFeedFragment;
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

public class AllBlogsFragment extends Fragment {

    private FragmentAllBlogsBinding binding;
    private AllBlogsAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllBlogsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new AllBlogsAdapter(getActivity(), new AllBlogsAdapter.OnBlogInteractionListener() {
            @Override
            public void onBlogClick(Blog blog) {
                viewModel.setCurrentBlog(blog);
                viewModel.setCurrentBlogID(blog.getBlogID());
                FragmentTransactionHelper.loadFragment(requireContext(), new BlogFragment());
            }

            @Override
            public void onEditClick(Blog blog) {
                viewModel.setCurrentBlog(blog);
                FragmentTransactionHelper.loadFragment(requireContext(), new EditFragment());
            }

            @Override
            public void onDeleteClick(Blog blog) {
                showDeleteConfirmationDialog(blog);
            }
        });

        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);

        loadAllBlogs();
    }

    private void loadAllBlogs() {
        db.collection("blogs").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Blog> blogs = queryDocumentSnapshots.toObjects(Blog.class);
                    adapter.setBlogs(blogs);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void showDeleteConfirmationDialog(Blog blog) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        dialogBuilder.setView(inflater.inflate(R.layout.delete_blog_dialog, null));
        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> {
            deleteBlog(blog);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        dialogBuilder.create().show();
    }

    private void deleteBlog(Blog blog) {
        String blogID = blog.getBlogID();

        // delete all comments of this blog
        deleteAllBlogComments(blogID);

        // delete blog cover image
        deleteBlogImages(blogID);

        // delete this blog from other user's favorites
        DocumentReference docRef = db.collection("blogs").document(blogID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // get list of all user ids of users that liked this blog
                    List<String> likedBy = (List<String>) document.get("likedBy");
                    for (String id : likedBy) { // for each user id, delete blogID from their favorites
                        db.collection("users").document(id)
                                .update("favorites", FieldValue.arrayRemove(blogID)).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        // do nothing
                                    } else {
                                        Toast.makeText(requireContext(), "Error updating favorites.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    // delete the blog from db
                    docRef.delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(requireContext(), "Blog deleted.", Toast.LENGTH_SHORT).show();
                                loadAllBlogs();
                            })
                            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error deleting blog.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // delete all comments of blog
    private void deleteAllBlogComments(String blogID) {
        CollectionReference colRef = db.collection("comments");
        Query query = colRef.whereEqualTo("blogID", blogID);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // delete all comments of this blog
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String commentID = document.getString("commentID");
                    colRef.document(commentID).delete();
                }
            } else {
                Toast.makeText(requireContext(), "Error deleting comments.", Toast.LENGTH_SHORT).show();
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
                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error deleting blog images.", Toast.LENGTH_SHORT).show());
    }
}