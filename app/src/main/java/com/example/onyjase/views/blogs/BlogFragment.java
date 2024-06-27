package com.example.onyjase.views.blogs;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentBlogBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

// Fragment for a single blog
public class BlogFragment extends Fragment {
    private FragmentBlogBinding binding;

    // view model
    private AppViewModel viewModel;

    // firebase firestore
    private FirebaseFirestore db;

    // firebase storage
    private FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBlogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // load comment section fragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.commentSection, new CommentsFragment());
        transaction.commit();

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // Initially set blog content to invisible until fetched content from db
        binding.blogContent.setVisibility(View.INVISIBLE);

        // Display content from current blog of view model
        String currentBlogID = viewModel.getCurrentBlogID().getValue();
        if (currentBlogID != null) {
            // set title, content, date, likes, and author
            setBlogContent(currentBlogID);

            // set blog cover image
            setBlogCoverImage("blogs/" + currentBlogID);

            // set like icon depending on if current user has previously like current blog
            setLikeIcon(currentBlogID);
        } else {
            Toast.makeText(requireContext(), "Error fetching blog.", Toast.LENGTH_SHORT).show();
            FragmentTransactionHelper.loadFragment(requireContext(), new BlogsFeedFragment());
        }

        // =============================================== Buttons Listeners ===============================================

        // Likes button listener
        binding.likeBtn.setOnClickListener(v -> {
            updateBlogLikes(currentBlogID);
        });

        // Back button listener
        binding.backBtn.setOnClickListener(v -> FragmentTransactionHelper.loadFragment(requireContext(), new BlogsFeedFragment()));

        // Edit button listener
        binding.editBtn.setOnClickListener(v -> {
            FragmentTransactionHelper.loadFragment(requireContext(), new EditFragment());
        });

        // Delete button listener
        binding.deleteBtn.setOnClickListener(v -> {
            // TODO: Handle delete blog action
            Toast.makeText(requireContext(), "Delete clicked.", Toast.LENGTH_SHORT).show();
        });
    }

    // =============================================== Functions ===============================================

    // set the title, content, date, and number of likes for the current blog
    private void setBlogContent(String blogID) {
        DocumentReference docRef = db.collection("blogs").document(blogID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    // if user is current blog author
                    if (viewModel.getUser().getValue().getUserID().equals(document.getString("userID"))) {
                        // show edit & delete button
                        binding.editBtn.setVisibility(View.VISIBLE);
                        binding.deleteBtn.setVisibility(View.VISIBLE);

                        // set current blog in view model
                        viewModel.setCurrentBlog(new Blog(blogID, document.getString("userID"), document.getString("title"), document.getString("content"), document.getString("imageURL"), document.getDouble("likes").intValue()));
                    }

                    // set blog cover image
                    setBlogCoverImage(document.getString("imageURL"));

                    // set author username
                    setBlogUserName(document.getString("userID"));

                    // set title, content, date, and number of likes
                    binding.title.setText(document.getString("title"));
                    binding.content.setText(document.getString("content"));
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    binding.dateTime.setText(formatter.format(document.getTimestamp("timestamp").toDate()));
                    binding.likes.setText(String.valueOf(document.getDouble("likes").intValue()));

                    binding.blogContent.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(requireContext(), "Error getting blog content.", Toast.LENGTH_SHORT).show();
                FragmentTransactionHelper.loadFragment(requireContext(), new BlogsFeedFragment());
            }
        });
    }

    // set the username for the author of current blog
    private void setBlogUserName(String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    binding.username.setText(document.getString("username"));
                }
            } else {
                Toast.makeText(requireContext(), "Error getting blog author.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // set the cover image for the current blog
    private void setBlogCoverImage(String imageURL) {
        StorageReference storageRef = storage.getReference();
        storageRef.child(imageURL).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(requireContext())
                            .load(uri)
                            .into(binding.coverImage);
                })
                .addOnFailureListener(e -> binding.coverImage.setImageResource(R.drawable.blue_rectangle_border));
    }

    // set blog like icon depending on if current user like it or not
    private void setLikeIcon(String blogID) {
        String userID = viewModel.getUser().getValue().getUserID();
        db.collection("users").document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> userLikes = (List<String>) document.get("favorites");
                    if (userLikes != null && userLikes.contains(blogID)) {
                        binding.likeIcon.setImageResource(R.drawable.blue_heart);
                    } else {
                        binding.likeIcon.setImageResource(R.drawable.gray_heart);
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Error updating likes.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // update favorites list of current user when clicking like
    private void updateBlogLikes(String blogID){
        String userID = viewModel.getUser().getValue().getUserID();
        DocumentReference docRef = db.collection("users").document(userID);

        // check if already added to favorites
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> userLikes = (List<String>) document.get("favorites");
                    if (userLikes != null && userLikes.contains(blogID)) { // Already in favorites
                        docRef.update("favorites", FieldValue.arrayRemove(blogID)).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                updateBlogLikesCount(blogID, false);
                            } else {
                                Toast.makeText(requireContext(), "Error updating favorites.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else { // Not already in favorites
                        docRef.update("favorites", FieldValue.arrayUnion(blogID)).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                updateBlogLikesCount(blogID, true);
                            } else {
                                Toast.makeText(requireContext(), "Error updating favorites.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    // update likes count for current blog when clicking like
    private void updateBlogLikesCount(String blogID, boolean isAdd) {
        DocumentReference docRef = db.collection("blogs").document(blogID);

        // get current likes count
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int curLikes = document.getDouble("likes").intValue();
                    docRef.update("likes", isAdd ? curLikes + 1 : curLikes - 1).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            binding.likeIcon.setImageResource(isAdd ? R.drawable.blue_heart : R.drawable.gray_heart);
                            binding.likes.setText(String.valueOf(isAdd ? curLikes + 1 : curLikes - 1));
                        } else {
                            Toast.makeText(requireContext(), "Error updating likes count.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(requireContext(), "Error getting current likes count.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}