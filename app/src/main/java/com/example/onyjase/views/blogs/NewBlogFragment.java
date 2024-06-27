package com.example.onyjase.views.blogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentNewBlogBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.UUID;

// Fragment for the page for creating a new blog
public class NewBlogFragment extends Fragment {
    FragmentNewBlogBinding binding;

    // selecting image
    Uri curImage;

    // firebase firestore
    FirebaseFirestore db;

    // firebase storage
    FirebaseStorage storage;

    // view model
    AppViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewBlogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // cancel button listener
        binding.cancel.setOnClickListener(v -> {
            clearInputs();
            loadFragment(new BlogsFeedFragment());
        });

        // post button
        binding.post.setOnClickListener(v -> {
            if (binding.title.getText() == null || binding.content.getText() == null || binding.title.getText().toString().isEmpty() || binding.content.getText().toString().isEmpty() || curImage == null) {
                Toast.makeText(requireContext(), "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
            } else {
                // save blog to db
                saveBlogToDB(binding.title.getText().toString(), binding.content.getText().toString());
            }
        });

        // select image btn
        binding.selectImgBtn.setOnClickListener(v -> pickImage());

        // select image box
        binding.selectedImg.setOnClickListener(v -> pickImage());
    }

    // clear all inputs
    private void clearInputs() {
        binding.title.setText("");
        binding.content.setText("");
        curImage = null;
        binding.selectedImg.setImageResource(R.drawable.blue_rectangle_border);
    }

    // =============================================== Functions ===============================================

    // picking image
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        binding.selectedImg.setImageURI(data.getData());
                        curImage = data.getData();
                    }
                }
            }
            );

    // go to another fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    // save blog to db
    private void saveBlogToDB(String title, String content) {
        String userID = viewModel.getUser().getValue().getUserID();
        String blogID = UUID.randomUUID().toString().replace("-", "");
        Blog blog = new Blog(blogID, userID, title, content, "blogs/" + blogID + "/cover", 0);

        db.collection("blogs")
                .document(blogID)
                .set(blog)
                .addOnSuccessListener(unused -> saveImageToStorage(blogID, curImage)) // save image to storage
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error posting new blog.", Toast.LENGTH_SHORT).show());
    }

    // save image to storage
    private void saveImageToStorage(String blogID, Uri image) {
        StorageReference storageRef = storage.getReference();
        StorageReference blogImgRef = storageRef.child("blogs/" + blogID + "/cover");
        blogImgRef.putFile(image).addOnSuccessListener(taskSnapshot -> {
            // toast success message
            Toast.makeText(requireContext(), "New blog posted.", Toast.LENGTH_SHORT).show();

            // update view model current blog
            viewModel.setCurrentBlogID(blogID);

            // go to blog page
            loadFragment(new BlogFragment());
        }).addOnFailureListener(e -> {
            // saving image to storage failed, delete blog from database
            db.collection("blogs").document(blogID).delete();

            // toast error message
            Toast.makeText(requireContext(), "Error posting new blog.", Toast.LENGTH_SHORT).show();
        });
    }
}