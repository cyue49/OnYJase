package com.example.onyjase.views.posts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentEditPostBinding;
import com.example.onyjase.models.Post;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditPostFragment extends Fragment {
    FragmentEditPostBinding binding;

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
        binding = FragmentEditPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // load initial data
        Post currentPost = viewModel.getCurrentPost().getValue();
        if (currentPost == null) {
            Toast.makeText(requireContext(), "Error getting post.", Toast.LENGTH_SHORT).show();
            FragmentTransactionHelper.loadFragment(requireContext(), new PostFragment());
        } else {
            // set post title, content, and tag
            binding.title.setText(currentPost.getTitle());
            binding.content.setText(currentPost.getContent());
            switch (currentPost.getTag()) {
                case "learn":
                    binding.learnRadio.toggle();
                    break;
                case "exam":
                    binding.examRadio.toggle();
                    break;
                case "bill96":
                    binding.bill96Radio.toggle();
                    break;
                default:
                    binding.otherRadio.toggle();
                    break;
            }

            // set post cover image
            setPostCoverImage(currentPost.getImageURL());
        }

        // =============================================== Buttons listeners ===============================================

        // select image buttons
        binding.selectedImg.setOnClickListener(v -> pickImage());
        binding.selectImgBtn.setOnClickListener(v -> pickImage());

        // cancel button
        binding.cancel.setOnClickListener(v -> FragmentTransactionHelper.loadFragment(requireContext(), new PostFragment()));

        // update button
        binding.update.setOnClickListener(v -> {
            if (binding.title.getText() == null || binding.content.getText() == null || binding.title.getText().toString().isEmpty() || binding.content.getText().toString().isEmpty() || curImage == null || binding.tagsRadioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(requireContext(), "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
            } else {
                if (binding.learnRadio.isChecked()) {
                    updatePostInDb("learn");
                } else if (binding.examRadio.isChecked()) {
                    updatePostInDb("exam");
                } else if (binding.bill96Radio.isChecked()) {
                    updatePostInDb("bill96");
                } else if (binding.otherRadio.isChecked()) {
                    updatePostInDb("other");
                } else {
                    Toast.makeText(requireContext(), "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    // set post cover image
    private void setPostCoverImage(String imageURL){
        StorageReference storageRef = storage.getReference();
        storageRef.child(imageURL).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(requireContext())
                            .load(uri)
                            .placeholder(R.drawable.placeholder_image)
                            .into(binding.selectedImg);
                    curImage = uri;
                })
                .addOnFailureListener(e -> binding.selectedImg.setImageResource(R.drawable.placeholder_image));
    }

    // update a post in database
    private void updatePostInDb(String tag){
        String userID = viewModel.getUser().getValue().getUserID();
        String postID = viewModel.getCurrentPost().getValue().getPostID();
        String title = binding.title.getText().toString();
        String content = binding.content.getText().toString();
        String imageURL = "posts/" + postID + "/cover";

        // new updated post
        Post newPost = new Post(postID, userID, title, content, tag, imageURL);

        // save image to storage
        StorageReference imageRef = storage.getReference().child(imageURL);
        imageRef.putFile(curImage).addOnSuccessListener(taskSnapshot -> {
            // save post to database
            db.collection("posts")
                    .document(postID)
                    .set(newPost)
                    .addOnSuccessListener(unused -> {
                        // toast success message
                        Toast.makeText(requireContext(), "Admin post updated.", Toast.LENGTH_SHORT).show();

                        // back to post page
                        FragmentTransactionHelper.loadFragment(requireContext(), new PostFragment());
                    }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error updating admin post", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error updating admin post", Toast.LENGTH_SHORT).show());
    }
}