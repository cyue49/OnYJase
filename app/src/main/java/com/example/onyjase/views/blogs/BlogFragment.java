package com.example.onyjase.views.blogs;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.adapters.CommentAdapter;
import com.example.onyjase.adapters.StickerAdapter;
import com.example.onyjase.databinding.FragmentBlogBinding;
import com.example.onyjase.models.Comment;
import com.example.onyjase.models.stickers.Sticker;
import com.example.onyjase.models.stickers.StickerImage;
import com.example.onyjase.models.stickers.StickerImages;
import com.example.onyjase.models.stickers.Stickers;
import com.example.onyjase.utils.StickersService;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Fragment for a single blog
public class BlogFragment extends Fragment {
    FragmentBlogBinding binding;

    // ui components variables
    ImageView likeIcon, coverImg;
    TextView titleTxt, dateTimeTxt, authorTxt, contentTxt, likesTxt;
    LinearLayout likeBtn, backBtn, editBtn, deleteBtn;
    ScrollView blogContent;

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

        // load comment section fragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.commentSection, new CommentsFragment());
        transaction.commit();

        // initializing variables
        backBtn = binding.backBtn;
        editBtn = binding.editBtn;
        deleteBtn = binding.deleteBtn;
        likeIcon = binding.likeIcon;
        likeBtn = binding.likeBtn;
        titleTxt = binding.title;
        dateTimeTxt = binding.dateTime;
        authorTxt = binding.username;
        contentTxt = binding.content;
        likesTxt = binding.likes;
        coverImg = binding.coverImage;
        blogContent = binding.blogContent;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // initially set blog content to invisible until fetched content from db
        blogContent.setVisibility(View.INVISIBLE);

        // display content from current blog of view model
        String currentBlogID = viewModel.getCurrentBlogID().getValue();
        if (currentBlogID != null) {
            // set title, content, date, likes, author, and cover image
            setBlogContent(currentBlogID);

            // set like icon depending on if current user has previously like current blog
            setLikeIcon(currentBlogID);
        } else {
            Toast.makeText(requireContext(), "Error fetching blog.", Toast.LENGTH_SHORT).show();
            loadFragment(new BlogsFeedFragment());
        }

        // =============================================== Buttons Listeners ===============================================

        // likes button
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBlogLikes(currentBlogID);
            }
        });

        // back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new BlogsFeedFragment());
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
                    Toast.makeText(requireContext(), "Error getting blog author.", Toast.LENGTH_SHORT).show();
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
                        blogContent.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error getting blog cover image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // set the title, content, date, and number of likes for the current blog
    private void setBlogContent(String blogID) {
        DocumentReference docRef = db.collection("blogs").document(blogID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        // show edit & delete button if user is current blog author
                        if (viewModel.getUser().getValue().getUserID().equals(document.getString("userID"))) {
                            editBtn.setVisibility(View.VISIBLE);
                            deleteBtn.setVisibility(View.VISIBLE);
                        }

                        // set blog cover image
                        setBlogCoverImage(document.getString("imageURL"));

                        // set author username
                        setBlogUserName(document.getString("userID"));

                        // set title, content, date, and number of likes
                        titleTxt.setText(document.getString("title"));
                        contentTxt.setText(document.getString("content"));
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        dateTimeTxt.setText(formatter.format(document.getTimestamp("timestamp").toDate()));
                        likesTxt.setText(String.valueOf(document.getDouble("likes").intValue()));
                    }
                } else {
                    Toast.makeText(requireContext(), "Error getting blog content.", Toast.LENGTH_SHORT).show();
                    loadFragment(new BlogsFeedFragment());
                }
            }
        });
    }

    // set blog like icon depending on if current user like it or not
    private void setLikeIcon(String blogID) {
        String userID = viewModel.getUser().getValue().getUserID();
        db.collection("users")
                .document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<String> userLikes = (List<String>) document.get("favorites");
                                if (userLikes.contains(blogID)) {
                                    likeIcon.setImageResource(R.drawable.blue_heart);
                                } else {
                                    likeIcon.setImageResource(R.drawable.gray_heart);
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error updating likes.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // update favorites list of current user when clicking like
    private void updateBlogLikes(String blogID){
        String userID = viewModel.getUser().getValue().getUserID();
        DocumentReference docRef = db.collection("users").document(userID);

        // check if already added to favorites
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> userLikes = (List<String>) document.get("favorites");
                        if (userLikes.contains(blogID)) { // already in favorites
                            // remove blogID from current user's favorites list
                            docRef
                                    .update("favorites", FieldValue.arrayRemove(blogID))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                // update likes count for current blog
                                                updateBlogLikesCount(blogID, false);
                                            } else {
                                                Toast.makeText(requireContext(), "Error updating favorites.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else { // not already in favorites
                            // add blogID to current user's favorites list
                            docRef
                                    .update("favorites", FieldValue.arrayUnion(blogID))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                // update likes count for current blog
                                                updateBlogLikesCount(blogID, true);
                                            } else {
                                                Toast.makeText(requireContext(), "Error updating favorites.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

    // update likes count for current blog when clicking like
    private void updateBlogLikesCount(String blogID, boolean isAdd) {
        DocumentReference docRef = db.collection("blogs").document(blogID);

        // get current likes count
        docRef
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            int curLikes = document.getDouble("likes").intValue();
                            // update likes count + 1 / -1
                            docRef.update("likes", isAdd ? curLikes+1 : curLikes-1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // update ui
                                        likeIcon.setImageResource(isAdd ? R.drawable.blue_heart : R.drawable.gray_heart);
                                        likesTxt.setText(String.valueOf(isAdd ? curLikes+1 : curLikes-1));
                                    } else {
                                        Toast.makeText(requireContext(), "Error updating favorites.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error updating favorites.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}