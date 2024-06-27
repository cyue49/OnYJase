package com.example.onyjase.views.blogs;

import android.net.Uri;
import android.os.Bundle;

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

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentEditBlogBinding;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditBlogFragment extends Fragment {
    FragmentEditBlogBinding binding;

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
        binding = FragmentEditBlogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // current blog
        String currentBlogID = viewModel.getCurrentBlogID().getValue();

        // set input fields with current blog content
        if (currentBlogID == null) {
            Toast.makeText(requireContext(), "Error fetching blog.", Toast.LENGTH_SHORT).show();
            loadFragment(new BlogsFeedFragment());
        } else {
            // set title and content
            setBlogContent(currentBlogID);

            // set cover image
            setBlogCoverImage("blogs/" + currentBlogID);
        }

        // cancel button
        binding.cancel.setOnClickListener(v -> {
            clearInputs();
            loadFragment(new BlogFragment());
        });

        // update button
        binding.update.setOnClickListener(v -> {
            if (binding.title.getText() == null || binding.content.getText() == null || binding.title.getText().toString().isEmpty() || binding.content.getText().toString().isEmpty() || curImage == null) {
                Toast.makeText(requireContext(), "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
            } else {
                // update in db
                // todo
            }
        });

        // select image btn
        binding.selectImgBtn.setOnClickListener(v -> {
            // todo: handle image selection
        });

        // select image box
        binding.selectedImg.setOnClickListener(v -> {
            // todo: handle image selection
        });
    }

    // =============================================== Functions ===============================================

    // go to another fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    // clear all inputs
    private void clearInputs() {
        binding.title.setText("");
        binding.content.setText("");
        curImage = null;
        binding.selectedImg.setImageResource(R.drawable.blue_rectangle_border);
    }

    // set title and content input with current blog data
    private void setBlogContent(String blogID) {
        DocumentReference docRef = db.collection("blogs").document(blogID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    binding.title.setText(document.getString("title"));
                    binding.content.setText(document.getString("content"));
                }
            } else {
                Toast.makeText(requireContext(), "Error getting blog content.", Toast.LENGTH_SHORT).show();
                loadFragment(new BlogFragment());
            }
        });
    }

    // set cover image of current blog
    private void setBlogCoverImage(String imageURL) {
        StorageReference storageRef = storage.getReference();
        storageRef.child(imageURL).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(requireContext())
                            .load(uri)
                            .into(binding.selectedImg);
                    curImage = uri;
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error getting blog cover image.", Toast.LENGTH_SHORT).show());
    }
}