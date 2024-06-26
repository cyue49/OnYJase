package com.example.onyjase.views.blogs;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.adapters.CommentAdapter;
import com.example.onyjase.adapters.StickerAdapter;
import com.example.onyjase.databinding.FragmentCommentsBinding;
import com.example.onyjase.models.Comment;
import com.example.onyjase.models.stickers.Sticker;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Fragment for the comment section of a single blog
public class CommentsFragment extends Fragment {
    FragmentCommentsBinding binding;

    // ui components variables
    ImageView selectedSticker;
    TextView commentsCount, newestBtn, oldestBtn;
    TextInputEditText commentInput, stickerInput;
    Button clearBtn, submitBtn;
    LinearLayout stickerBtn, stickerDisplay, stickerSearchBtn;
    RecyclerView commentList, stickersList;

    // recycle view adapters
    CommentAdapter commentAdapter;
    StickerAdapter stickerAdapter;

    // list of all comments for the blog
    LinkedList<Comment> comments;

    // list of stickers when searching up stickers
    ArrayList<Sticker> stickers;

    // url of currently selected sticker
    String selectedStickerUrl = "";

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
        binding = FragmentCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        commentsCount = binding.commentsCount;
        commentInput = binding.commentInput;
        clearBtn = binding.clearBtn;
        submitBtn = binding.submitBtn;
        newestBtn = binding.newestBtn;
        oldestBtn = binding.oldestBtn;
        commentList = binding.commentsList;
        stickerBtn = binding.stickerBtn;
        stickerDisplay = binding.stickerDisplay;
        selectedSticker = binding.selectedSticker;
        stickersList = binding.stickersList;
        stickerInput = binding.stickerSearchInput;
        stickerSearchBtn = binding.stickerSearchBtn;
        comments = new LinkedList<>();
        stickers = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // get current blog id from view model
        String currentBlogID = viewModel.getCurrentBlogID().getValue();

        // =============================================== RecycleView Adapters ===============================================

        // setting adapter for comments recycle view
        commentList.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(comments, getContext(), db);
        commentList.setAdapter(commentAdapter);

        // set all comments for current blog
        setAllBlogComments(currentBlogID, commentAdapter);

        // setting adapter for stickers recycle view
        stickersList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        stickerAdapter = new StickerAdapter(stickers, getContext());
        stickerAdapter.setOnStickersClickListener(new StickerAdapter.OnStickersClickListener() { // set listener
            @Override
            public void onClickSticker(String url) {
                Glide.with(requireContext()).load(url).into(selectedSticker);
                selectedStickerUrl = url;
            }
        });
        stickersList.setAdapter(stickerAdapter);

        // =============================================== Buttons Listeners ===============================================

        // submit comment button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentInput.getText() == null || commentInput.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Empty comment.", Toast.LENGTH_SHORT).show();
                } else {
                    // save comment to db
                    saveCommentToDB(commentInput.getText().toString(), commentAdapter);
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

        // sort comments by newest button
        newestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // updating buttons ui
                newestBtn.setTypeface(newestBtn.getTypeface(), Typeface.BOLD);
                newestBtn.setAlpha(1.0f);

                oldestBtn.setTypeface(oldestBtn.getTypeface(), Typeface.NORMAL);
                oldestBtn.setAlpha(0.5f);

                // updating comments
                sortCommentsByDate(comments, true);
                commentAdapter.reload();
            }
        });

        // sort comments by oldest button
        oldestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // updating buttons ui
                oldestBtn.setTypeface(oldestBtn.getTypeface(), Typeface.BOLD);
                oldestBtn.setAlpha(1.0f);

                newestBtn.setTypeface(newestBtn.getTypeface(), Typeface.NORMAL);
                newestBtn.setAlpha(0.5f);

                // updating comments
                sortCommentsByDate(comments, false);
                commentAdapter.reload();
            }
        });

        // select sticker button
        stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stickerDisplay.getVisibility() == View.VISIBLE) {
                    stickerDisplay.setVisibility(View.GONE);
                    clearStickerSelection();
                } else {
                    stickerDisplay.setVisibility(View.VISIBLE);
                }
            }
        });

        // search sticker button
        stickerSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stickerInput.getText() == null || stickerInput.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Enter something to search for stickers.", Toast.LENGTH_SHORT).show();
                } else {
                    stickersApiCall(stickerAdapter, stickerInput.getText().toString());
                }
            }
        });
    }

    // =============================================== Functions ===============================================

    // clear all inputs
    private void clearInputs() {
        commentInput.setText("");
        clearStickerSelection();
    }

    // clear sticker selection
    private void clearStickerSelection() {
        stickerInput.setText("");
        selectedStickerUrl = "";
        selectedSticker.setImageResource(R.drawable.gray_rectangle);
        stickers.clear();
        stickerAdapter.reload();
    }

    // set all comments for the current blog
    private void setAllBlogComments(String blogID, CommentAdapter adapter){
        // get all comments where blogID equals current blogID
        CollectionReference colRef = db.collection("comments");
        Query query = colRef.whereEqualTo("blogID", blogID);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String commentID = document.getString("commentID");
                                String userID = document.getString("userID");
                                String blogId = document.getString("blogID");
                                String content = document.getString("content");
                                String stickerURL = document.getString("stickerURL");
                                Date timestamp = document.getTimestamp("timestamp").toDate();

                                Comment comment = new Comment(commentID, userID, blogId, content, stickerURL, timestamp);
                                comments.add(comment);
                                sortCommentsByDate(comments, true);
                                count++;
                            }
                            adapter.reload();
                            commentsCount.setText(String.valueOf(count));
                        } else {
                            Toast.makeText(requireContext(), "Error getting blog comments.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // save new comment to db
    private void saveCommentToDB(String content, CommentAdapter adapter) {
        String userID = viewModel.getUser().getValue().getUserID();
        String commentID = UUID.randomUUID().toString().replace("-", "");
        String blogID = viewModel.getCurrentBlogID().getValue();
        Date dateTime = new Date();

        Comment comment = new Comment(commentID, userID, blogID, content, selectedStickerUrl, dateTime);

        db.collection("comments")
                .document(commentID)
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(), "New comment posted!", Toast.LENGTH_SHORT).show();

                        comments.addFirst(comment);
                        adapter.reload();
                        int count = Integer.parseInt(commentsCount.getText().toString());
                        commentsCount.setText(String.valueOf(count+1));
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

    // stickers api call
    private void stickersApiCall(StickerAdapter adapter, String query) {
        // getting the api key
        DocumentReference docRef = db.collection("apis").document("giphy");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String apiKey = document.getString("apiKey");

                        // setting up api call
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://api.giphy.com/v1/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        StickersService stickersService = retrofit.create(StickersService.class);

                        // making api call
                        Call<Stickers> call = stickersService.getStickers(apiKey, query);

                        // handling the response
                        call.enqueue(new Callback<Stickers>() {
                            @Override
                            public void onResponse(Call<Stickers> call, Response<Stickers> response) {
                                if (!response.isSuccessful() && response.body() == null) {
                                    Toast.makeText(requireContext(), "Error fetching stickers.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Stickers responseStickers = response.body();
                                if (responseStickers != null) {
                                    stickers.clear();
                                    stickers.addAll(responseStickers.getData());
                                    adapter.reload();
                                }
                            }

                            @Override
                            public void onFailure(Call<Stickers> call, Throwable throwable) {
                                Toast.makeText(requireContext(), "Error fetching stickers.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(requireContext(), "Error fetching stickers.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // sort list of comments by date
    private void sortCommentsByDate(LinkedList<Comment> comments, Boolean newestFirst) {
        comments.sort(new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                if (o1.getTimestamp().equals(o2.getTimestamp())) return 0;
                return newestFirst ? o1.getTimestamp().compareTo(o2.getTimestamp()) * -1 : o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
    }
}