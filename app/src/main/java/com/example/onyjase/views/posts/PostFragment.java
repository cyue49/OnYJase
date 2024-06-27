package com.example.onyjase.views.posts;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentPostBinding;
import com.example.onyjase.models.Post;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Fragment for a single post
public class PostFragment extends Fragment {
    FragmentPostBinding binding;

    // view model
    AppViewModel viewModel;

    // firebase firestore
    FirebaseFirestore db;

    // firebase storage
    FirebaseStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // initially set post content to invisible until fetdched content from db
        binding.postContent.setVisibility(View.INVISIBLE);

        // display content from current post of view model
        String currentPostID = viewModel.getCurrentPostID().getValue();
        if (currentPostID != null) {
            // set title and content
            setPostContent(currentPostID);

            // set post cover image
            setPostCoverImage("posts/" + currentPostID);
        } else {
            Toast.makeText(requireContext(), "Error fetching post.", Toast.LENGTH_SHORT).show();
            loadFragment(new PostsFeedFragment());
        }

        // =============================================== Buttons Listeners ===============================================

        // back button
        binding.backBtn.setOnClickListener(v -> loadFragment(new PostsFeedFragment()));

        // edit button
        binding.editBtn.setOnClickListener(v -> loadFragment(new EditPostFragment()));

        // delete button
        binding.deleteBtn.setOnClickListener(v -> {
            // todo: handle delete
            Toast.makeText(requireContext(), "Delete clicked.", Toast.LENGTH_SHORT).show();
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

    // set the title and content of the post
    private void setPostContent(String postID) {
        DocumentReference docRef = db.collection("posts").document(postID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    // if user is current post author
                    if (viewModel.getUser().getValue().getUserID().equals(document.getString("userID"))) {
                        // show edit & delete button
                        binding.editBtn.setVisibility(View.VISIBLE);
                        binding.deleteBtn.setVisibility(View.VISIBLE);

                        // set current post in view model
                        viewModel.setCurrentPost(new Post(postID, document.getString("userID"), document.getString("title"), document.getString("content"), document.getString("tag"), document.getString("imageURL")));
                    }

                    // set post cover image
                    setPostCoverImage(document.getString("imageURL"));

                    // set title, content, and tag
                    binding.title.setText(document.getString("title"));
                    binding.content.setText(document.getString("content"));

                    binding.postContent.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(requireContext(), "Error getting post content.", Toast.LENGTH_SHORT).show();
                loadFragment(new PostsFeedFragment());
            }
        });
    }

    // set the cover image of the post
    private void setPostCoverImage(String imageURL) {
        StorageReference storageRef = storage.getReference();
        storageRef.child(imageURL).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(requireContext())
                            .load(uri)
                            .into(binding.coverImg);
                })
                .addOnFailureListener(e -> binding.coverImg.setImageResource(R.drawable.blue_rectangle_border));
    }
}