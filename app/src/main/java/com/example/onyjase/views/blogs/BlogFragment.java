package com.example.onyjase.views.blogs;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.adapters.CommentAdapter;
import com.example.onyjase.databinding.FragmentBlogBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.models.Comment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

// Fragment for a single blog
public class BlogFragment extends Fragment {
    FragmentBlogBinding binding;

    // ui components variables
    ImageView backBtn, editBtn, deleteBtn, likeBtn, coverImg;
    TextView titleTxt, dateTimeTxt, authorTxt, contentTxt, likesTxt;
    TextInputEditText commentInput;
    Button clearBtn, submitBtn;
    RecyclerView commentList;

    // arraylist of all comments for the blog
    ArrayList<Comment> comments;

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
        binding = FragmentBlogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        backBtn = binding.backBtn;
        editBtn = binding.editBtn;
        deleteBtn = binding.deleteBtn;
        likeBtn = binding.likeBtn;
        titleTxt = binding.title;
        dateTimeTxt = binding.dateTime;
        authorTxt = binding.username;
        contentTxt = binding.content;
        likesTxt = binding.likes;
        coverImg = binding.coverImage;
        commentInput = binding.commentInput;
        clearBtn = binding.clearBtn;
        submitBtn = binding.submitBtn;
        commentList = binding.commentsList;
        comments = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // display content from current blog of view model
        Blog currentBlog = viewModel.getCurrentBlog().getValue();
        if (currentBlog != null) {
            // set blog cover image
            setBlogCoverImage(currentBlog.getImageURL());

            // set author username
            setBlogUserName(currentBlog.getUserID());

            // set title, content, and date
            titleTxt.setText(currentBlog.getTitle());
            contentTxt.setText(currentBlog.getContent());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            dateTimeTxt.setText(formatter.format(currentBlog.getTimestamp()));
        } else {
            loadFragment(new BlogsFeedFragment());
        }

        // setting adapter for comments recycle view
        commentList.setLayoutManager(new LinearLayoutManager(getContext()));
        CommentAdapter adapter = new CommentAdapter(comments, getContext(), db);
        commentList.setAdapter(adapter);

        // submit comment button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentInput.getText() == null || commentInput.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Empty comment.", Toast.LENGTH_SHORT).show();
                } else {
                    // save comment to db
                    saveCommentToDB(commentInput.getText().toString(), adapter);
                }
            }
        });

        // clear button
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputs();
            }
        });
    }

    // clear all inputs
    private void clearInputs() {
        commentInput.setText("");
    }

    // go to another fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    // set the username for the author of current blog
    private void setBlogUserName(String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        authorTxt.setText(document.getString("username"));
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed reading blog author.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // set the cover image for the current blog
    private void setBlogCoverImage(String imageURL){
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
                        Toast.makeText(requireContext(), "Failed reading blog cover image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // save new comment to db
    private void saveCommentToDB(String content, CommentAdapter adapter) {
        String userID = viewModel.getUser().getValue().getUserID();
        String commentID = UUID.randomUUID().toString().replace("-", "");
        String blogID = viewModel.getCurrentBlog().getValue().getBlogID();
        Date dateTime = new Date();

        Comment comment = new Comment(commentID, userID, blogID, content, "", dateTime);

        db.collection("comments")
                .document(commentID)
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(), "New comment posted!", Toast.LENGTH_SHORT).show();

                        comments.add(comment);
                        adapter.reload();
                        clearInputs();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error posting new comment.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}