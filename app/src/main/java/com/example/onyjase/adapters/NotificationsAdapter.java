package com.example.onyjase.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.models.Notification;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    ArrayList<Notification> notifications;
    Context context;
    FirebaseFirestore db;
    OnNotificationClickListener notificationClickListener;

    public NotificationsAdapter(ArrayList<Notification> notifications, Context context, FirebaseFirestore db) {
        super();
        this.notifications = notifications;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        // set notification icon and text
        switch (notification.getType()) {
            case "like":
                Glide.with(context).load(R.drawable.blue_heart).into(holder.notifIcon);
                holder.notifMessage.setText("liked your blog");
                // set blog title
                setNotificationBlogTitle(holder, notification.getBlogID());
                break;
            case "comment":
                Glide.with(context).load(R.drawable.comment_icon).into(holder.notifIcon);
                holder.notifMessage.setText("commented on your blog");
                // set blog title
                setNotificationBlogTitle(holder, notification.getBlogID());
                break;
            case "follow":
                Glide.with(context).load(R.drawable.follow_icon).into(holder.notifIcon);
                holder.notifMessage.setText("followed you.");
                break;
        }

        // set date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         holder.notifDate.setText(formatter.format(notification.getTimestamp()));

        // set username
        setNotificationUsername(holder, notification.getFromUserID());

        // set notification click listener
        holder.notificationItem.setOnClickListener(v -> {
            notificationClickListener.onClickNotification(notification.getBlogID());
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    // reload adapter
    @SuppressLint("NotifyDataSetChanged")
    public void reload() {
        notifyDataSetChanged();
    }

    // set username for notification
    @SuppressLint("SetTextI18n")
    private void setNotificationUsername(NotificationViewHolder holder, String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    holder.notifFrom.setText(document.getString("username"));
                } else {
                    holder.notifFrom.setText("User");
                }
            } else {
                holder.notifFrom.setText("User");
            }
        });
    }

    // set blog title for notification
    @SuppressLint("SetTextI18n")
    private void setNotificationBlogTitle(NotificationViewHolder holder, String blogID) {
        DocumentReference docRef = db.collection("blogs").document(blogID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    holder.blogTitle.setText(document.getString("title"));
                } else {
                    holder.blogTitle.setText("Blog");
                }
            } else {
                holder.blogTitle.setText("Blog");
            };
        });
    }

    // view holder for notifications
    class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notifIcon;
        TextView notifFrom, notifMessage, blogTitle, notifDate;
        LinearLayout notificationItem;

        public NotificationViewHolder(View view) {
            super(view);
            notifIcon = view.findViewById(R.id.notifIcon);
            notifFrom = view.findViewById(R.id.notifFrom);
            notifMessage = view.findViewById(R.id.notifMessage);
            blogTitle = view.findViewById(R.id.blogTitle);
            notifDate = view.findViewById(R.id.notifDate);
            notificationItem = view.findViewById(R.id.notificationItem);
        }
    }

    // on click listener for each notification
    public interface OnNotificationClickListener {
        void onClickNotification(String blogID);
    }

    // set listener
    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.notificationClickListener = listener;
    }
}
