package com.example.onyjase.views.blogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.onyjase.databinding.FragmentEditBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditFragment extends Fragment {
    private FragmentEditBinding binding;
    private AppViewModel viewModel;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri curImageUri;
    private boolean imageChanged;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageChanged = false;

        // Load initial data
        Blog currentBlog = viewModel.getCurrentBlog().getValue();
        if (currentBlog != null) {
            binding.title.setText(currentBlog.getTitle());
            binding.content.setText(currentBlog.getContent());
            setBlogCoverImage(currentBlog.getImageURL());
            viewModel.setCurrentBlogID(currentBlog.getBlogID());
        }

        binding.selectImgBtn.setOnClickListener(v -> pickImage());
        binding.selectedImg.setOnClickListener(v -> pickImage());
        binding.cancel.setOnClickListener(v -> FragmentTransactionHelper.loadFragment(requireContext(), new BlogFragment()));
        binding.update.setOnClickListener(v -> updateBlog());
    }

    // set blog cover image
    private void setBlogCoverImage(String imageURL) {
        if (imageURL != null && !imageURL.isEmpty()) {
            StorageReference storageRef = storage.getReference().child(imageURL);
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.placeholder_image) // Placeholder image
                        .into(binding.selectedImg);
                curImageUri = uri;
            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error getting blog cover image.", Toast.LENGTH_SHORT).show());
        }
    }

    // update blog in db
    private void updateBlog() {
        String title = binding.title.getText().toString();
        String content = binding.content.getText().toString();

        if (title.isEmpty() || content.isEmpty() || curImageUri == null) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Blog blog = viewModel.getCurrentBlog().getValue();
        if (blog != null) {
            String blogID = blog.getBlogID();
            String userID = blog.getUserID();
            String imageURL = "blogs/" + blogID + "/cover"; // Assuming the image is stored under blogs/{blogID}/cover

            // get most recent likes count and liked by before saving to db
            DocumentReference docRef = db.collection("blogs").document(blogID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int likesCount = document.getDouble("likes").intValue();
                        List<String> likedBy = (List<String>) document.get("likedBy");

                        Blog updatedBlog = new Blog(blogID, userID, title, content, imageURL, likesCount, likedBy);

                        // update blog in db
                        db.collection("blogs").document(blogID).set(updatedBlog).addOnSuccessListener(aVoid -> {
                            // update cover image in db if changed
                            if (imageChanged) {
                                StorageReference imageRef = storage.getReference().child(imageURL);
                                imageRef.putFile(curImageUri).addOnSuccessListener(taskSnapshot -> {
                                    Toast.makeText(requireContext(), "Blog updated successfully", Toast.LENGTH_SHORT).show();
                                    FragmentTransactionHelper.loadFragment(requireContext(), new BlogFragment());
                                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error uploading image.", Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(requireContext(), "Blog updated successfully", Toast.LENGTH_SHORT).show();
                                FragmentTransactionHelper.loadFragment(requireContext(), new BlogFragment());
                            }
                        }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error updating blog.", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        curImageUri = data.getData();
                        binding.selectedImg.setImageURI(curImageUri);
                        imageChanged = true;
                    }
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
