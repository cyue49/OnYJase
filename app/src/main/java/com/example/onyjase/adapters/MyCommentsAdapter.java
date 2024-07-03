package com.example.onyjase.adapters;

// adapter for list of user comments in profile page
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemMyCommentBinding;
import com.example.onyjase.models.Comment;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyCommentsAdapter extends RecyclerView.Adapter<MyCommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private Activity context;
    private FirebaseFirestore firestore;
    private AppViewModel viewModel;

    public MyCommentsAdapter(AppViewModel viewModel, Activity context) {
        this.viewModel = viewModel;
        this.context = context;
        this.comments = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyCommentBinding binding;

        public CommentViewHolder(@NonNull ItemMyCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyCommentBinding binding = ItemMyCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.binding.commentContent.setText(comment.getContent());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        holder.binding.commentDateTime.setText(formatter.format(comment.getTimestamp()));

        holder.binding.moreOptionsButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.moreOptionsButton);
            popupMenu.inflate(R.menu.popup_menu); // Make sure to have this menu resource
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_edit:
                        showEditCommentDialog(comment);
                        return true;
                    case R.id.action_delete:
                        showDeleteConfirmationDialog(comment);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    private void showEditCommentDialog(Comment comment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_comment, null);
        dialogBuilder.setView(dialogView);

        EditText editCommentEditText = dialogView.findViewById(R.id.editCommentEditText);
        editCommentEditText.setText(comment.getContent());

        dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            String updatedCommentContent = editCommentEditText.getText().toString();
            if (!updatedCommentContent.isEmpty()) {
                comment.setContent(updatedCommentContent);
                firestore.collection("comments").document(comment.getCommentID()).set(comment)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Comment updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Error updating comment", Toast.LENGTH_SHORT).show());
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(Comment comment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage("Are you sure you want to delete this comment?");
        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> {
            firestore.collection("comments").document(comment.getCommentID()).delete()
                    .addOnSuccessListener(aVoid -> {
                        comments.remove(comment);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error deleting comment", Toast.LENGTH_SHORT).show());
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}