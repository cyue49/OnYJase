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

    // recycle view adapters
    CommentAdapter commentAdapter;
    StickerAdapter stickerAdapter;

    // list of comments and stickers for adapters
    LinkedList<Comment> comments;
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
        comments = new LinkedList<>();
        stickers = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // get current blog id from view model
        String currentBlogID = viewModel.getCurrentBlogID().getValue();

        // =============================================== RecycleView Adapters ===============================================

        // setting adapter for comments recycle view
        binding.commentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(comments, getContext(), db);
        binding.commentsList.setAdapter(commentAdapter);

        // set all comments for current blog
        setAllBlogComments(currentBlogID, commentAdapter);

        // setting adapter for stickers recycle view
        binding.stickersList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        stickerAdapter = new StickerAdapter(stickers, getContext());
        stickerAdapter.setOnStickersClickListener(url -> {
            Glide.with(requireContext()).load(url).into(binding.selectedSticker);
            selectedStickerUrl = url;
        });
        binding.stickersList.setAdapter(stickerAdapter);

        // =============================================== Buttons Listeners ===============================================

        // submit comment button
        binding.submitBtn.setOnClickListener(v -> {
            if (binding.commentInput.getText() == null || binding.commentInput.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Empty comment.", Toast.LENGTH_SHORT).show();
            } else {
                // save comment to db
                saveCommentToDB(binding.commentInput.getText().toString(), commentAdapter);
            }
        });

        // clear button
        binding.clearBtn.setOnClickListener(v -> clearInputs());

        // sort comments by newest button
        binding.newestBtn.setOnClickListener(v -> {
            // updating buttons ui
            binding.newestBtn.setTypeface(binding.newestBtn.getTypeface(), Typeface.BOLD);
            binding.newestBtn.setAlpha(1.0f);

            binding.oldestBtn.setTypeface(binding.oldestBtn.getTypeface(), Typeface.NORMAL);
            binding.oldestBtn.setAlpha(0.5f);

            // updating comments
            sortCommentsByDate(comments, true);
            commentAdapter.reload();
        });

        // sort comments by oldest button
        binding.oldestBtn.setOnClickListener(v -> {
            // updating buttons ui
            binding.oldestBtn.setTypeface(binding.oldestBtn.getTypeface(), Typeface.BOLD);
            binding.oldestBtn.setAlpha(1.0f);

            binding.newestBtn.setTypeface(binding.newestBtn.getTypeface(), Typeface.NORMAL);
            binding.newestBtn.setAlpha(0.5f);

            // updating comments
            sortCommentsByDate(comments, false);
            commentAdapter.reload();
        });

        // select sticker button
        binding.stickerBtn.setOnClickListener(v -> {
            if (binding.stickerDisplay.getVisibility() == View.VISIBLE) {
                binding.stickerDisplay.setVisibility(View.GONE);
                clearStickerSelection();
            } else {
                binding.stickerDisplay.setVisibility(View.VISIBLE);
            }
        });

        // search sticker button
        binding.stickerSearchBtn.setOnClickListener(v -> {
            if (binding.stickerSearchInput.getText() == null || binding.stickerSearchInput.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter something to search for stickers.", Toast.LENGTH_SHORT).show();
            } else {
                stickersApiCall(stickerAdapter, binding.stickerSearchInput.getText().toString());
            }
        });
    }

    // =============================================== Functions ===============================================

    // clear all inputs
    private void clearInputs() {
        binding.commentInput.setText("");
        clearStickerSelection();
    }

    // clear sticker selection
    private void clearStickerSelection() {
        binding.stickerSearchInput.setText("");
        selectedStickerUrl = "";
        binding.selectedSticker.setImageResource(R.drawable.gray_rectangle);
        stickers.clear();
        stickerAdapter.reload();
    }

    // set all comments for the current blog
    private void setAllBlogComments(String blogID, CommentAdapter adapter){
        // get all comments where blogID equals current blogID
        CollectionReference colRef = db.collection("comments");
        Query query = colRef.whereEqualTo("blogID", blogID);
        query.get()
                .addOnCompleteListener(task -> {
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
                        binding.commentsCount.setText(String.valueOf(count));
                    } else {
                        Toast.makeText(requireContext(), "Error getting blog comments.", Toast.LENGTH_SHORT).show();
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
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "New comment posted!", Toast.LENGTH_SHORT).show();

                    comments.addFirst(comment);
                    adapter.reload();
                    int count = Integer.parseInt(binding.commentsCount.getText().toString());
                    binding.commentsCount.setText(String.valueOf(count+1));
                    clearInputs();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error posting new comment.", Toast.LENGTH_SHORT).show());
    }

    // stickers api call
    private void stickersApiCall(StickerAdapter adapter, String query) {
        // getting the api key
        DocumentReference docRef = db.collection("apis").document("giphy");
        docRef.get().addOnCompleteListener(task -> {
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