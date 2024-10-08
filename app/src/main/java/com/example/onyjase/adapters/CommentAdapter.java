package com.example.onyjase.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.models.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

// adapter for list of comments in single blog page
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    LinkedList<Comment> comments;
    Context context;
    FirebaseFirestore db;

    public CommentAdapter(LinkedList<Comment> comments, Context context, FirebaseFirestore db) {
        super();
        this.comments = comments;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_blog_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        // set username
        setCommentUsername(holder, comment.getUserID());
        // set date time
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        holder.dateTime.setText(formatter.format(comment.getTimestamp()));
        // set content
        holder.content.setText(comment.getContent());
        // set sticker
        String stickerUrl = comment.getStickerURL();
        if (!stickerUrl.isEmpty()) {
            Glide.with(context).load(stickerUrl).into(holder.sticker);
            holder.sticker.setVisibility(View.VISIBLE);
        } else {
            holder.sticker.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // reload adapter
    @SuppressLint("NotifyDataSetChanged")
    public void reload() {
        notifyDataSetChanged();
    }

    // get comment username
    private void setCommentUsername(CommentViewHolder holder, String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.username.setText(document.getString("username"));
                    } else {
                        holder.username.setText("User");
                    }
                }
            }
        });
    }

    // viwe holder for comments
    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username, dateTime, content;
        ImageView sticker;

        public CommentViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            dateTime = view.findViewById(R.id.dateTime);
            content = view.findViewById(R.id.content);
            sticker = view.findViewById(R.id.sticker);
        }
    }
}
