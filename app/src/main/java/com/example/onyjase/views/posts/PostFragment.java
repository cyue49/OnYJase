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

    // ui components variables
    ImageView coverImg;
    TextView titleTxt, contentTxt;
    LinearLayout backBtn, editBtn, deleteBtn;
    ScrollView postContent;

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
        coverImg = binding.coverImg;
        titleTxt = binding.title;
        contentTxt = binding.content;
        backBtn = binding.backBtn;
        editBtn = binding.editBtn;
        deleteBtn = binding.deleteBtn;
        postContent = binding.postContent;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // initially set post content to invisible until fetdched content from db
        postContent.setVisibility(View.INVISIBLE);

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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PostsFeedFragment());
            }
        });

        // edit button
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo
                Toast.makeText(requireContext(), "Edit clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        // delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo
                Toast.makeText(requireContext(), "Delete clicked.", Toast.LENGTH_SHORT).show();
            }
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
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        // show edit & delete button if user is current post author
                        if (viewModel.getUser().getValue().getUserID().equals(document.getString("userID"))) {
                            editBtn.setVisibility(View.VISIBLE);
                            deleteBtn.setVisibility(View.VISIBLE);
                        }

                        // set title, content, and tag
                        titleTxt.setText(document.getString("title"));
                        contentTxt.setText(document.getString("content"));

                        postContent.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(requireContext(), "Error getting post content.", Toast.LENGTH_SHORT).show();
                    loadFragment(new PostsFeedFragment());
                }
            }
        });
    }

    // set the cover image of the post
    private void setPostCoverImage(String imageURL) {
        StorageReference storageRef = storage.getReference();
        storageRef.child(imageURL).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(requireContext())
                                .load(uri)
                                .into(coverImg);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error getting post cover image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}