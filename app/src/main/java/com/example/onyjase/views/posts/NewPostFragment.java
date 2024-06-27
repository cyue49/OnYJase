package com.example.onyjase.views.posts;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentNewPostBinding;
import com.example.onyjase.models.Post;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.posts.PostFragment;
import com.example.onyjase.views.posts.PostsFeedFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.UUID;

public class NewPostFragment extends Fragment {
    FragmentNewPostBinding binding;

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
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // cancel button
        binding.cancel.setOnClickListener(v -> {
            clearInputs();
            loadFragment(new PostsFeedFragment());
        });

        // post button
        binding.post.setOnClickListener(v -> {
            if (binding.title.getText() == null || binding.content.getText() == null || binding.title.getText().toString().isEmpty() || binding.content.getText().toString().isEmpty() || curImage == null || binding.tagsRadioGroup.getCheckedRadioButtonId() == -1){
                Toast.makeText(requireContext(), "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
            } else {
                // save post to db
                if (binding.learnRadio.isChecked()) {
                    savePostToDB(binding.title.getText().toString(), binding.content.getText().toString(), "learn");
                } else if (binding.examRadio.isChecked()) {
                    savePostToDB(binding.title.getText().toString(), binding.content.getText().toString(), "exam");
                } else if (binding.bill96Radio.isChecked()) {
                    savePostToDB(binding.title.getText().toString(), binding.content.getText().toString(), "bill96");
                } else if (binding.otherRadio.isChecked()) {
                    savePostToDB(binding.title.getText().toString(), binding.content.getText().toString(), "other");
                } else {
                    Toast.makeText(requireContext(), "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // select image btn
        binding.selectImgBtn.setOnClickListener(v -> pickImage());

        // select image box
        binding.selectedImg.setOnClickListener(v -> pickImage());
    }

    // =============================================== Functions ===============================================

    // clear all inputs
    private void clearInputs() {
        binding.title.setText("");
        binding.content.setText("");
        curImage = null;
        binding.selectedImg.setImageResource(R.drawable.blue_rectangle_border);
        binding.tagsRadioGroup.clearCheck();
    }

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

    // save post to db
    private void savePostToDB(String title, String content, String tag) {
        String userID = viewModel.getUser().getValue().getUserID();
        String postID = UUID.randomUUID().toString().replace("-", "");
        Post post = new Post(postID, userID, title, content, tag, "posts/" + postID);

        db.collection("posts")
                .document(postID)
                .set(post)
                .addOnSuccessListener(unused -> saveImageToStorage(postID, curImage)) // save image to storage
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error posting new admin post.", Toast.LENGTH_SHORT).show());
    }

    // save image to storage
    private void saveImageToStorage(String postID, Uri image) {
        StorageReference storageRef = storage.getReference();
        StorageReference postImgRef = storageRef.child("posts/" + postID);
        postImgRef.putFile(image).addOnSuccessListener(taskSnapshot -> {
            // toast success message
            Toast.makeText(requireContext(), "New admin post posted.", Toast.LENGTH_SHORT).show();

            // update view model current post
            viewModel.setCurrentPostID(postID);

            // go to post page
            loadFragment(new PostFragment());
        }).addOnFailureListener(e -> {
            // saving image to storage failed, delete post from database
            db.collection("posts").document(postID).delete();

            // toast error message
            Toast.makeText(requireContext(), "Error posting new admin post.", Toast.LENGTH_SHORT).show();
        });
    }
}