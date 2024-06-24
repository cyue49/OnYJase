package com.example.onyjase.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onyjase.R;
import com.example.onyjase.models.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

// adapter for list of comments in single blog page
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    ArrayList<Comment> comments;
    Context context;

    public CommentAdapter(ArrayList<Comment> comments, Context context) {
        super();
        this.comments = comments;
        this.context = context;
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
//        holder.username.setText(comment.getUserID());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        holder.dateTime.setText(formatter.format(comment.getTimestamp()));
        holder.content.setText(comment.getContent());
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

    // viwe holder for comments
    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username, dateTime, content;

        public CommentViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            dateTime = view.findViewById(R.id.dateTime);
            content = view.findViewById(R.id.content);
        }
    }
}
