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
import com.example.onyjase.models.Notification;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    ArrayList<Notification> notifications;
    Context context;
    FirebaseFirestore db;

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
        if (notification.getType().equals("like")) {
            Glide.with(context).load(R.drawable.blue_heart).into(holder.notifIcon);
            holder.notifMessage.setText("liked your blog");
        } else {
            Glide.with(context).load(R.drawable.comment_icon).into(holder.notifIcon);
            holder.notifMessage.setText("commented on your blog");
        }

        // set date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         holder.notifDate.setText(formatter.format(notification.getTimestamp()));

        // set username
        setNotificationUsername(holder, notification.getFromUserID());

        // set blog
        setNotificationBlogTitle(holder, notification.getBlogID());
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
    private void setNotificationUsername(NotificationViewHolder holder, String userID) {
        // todo
    }

    // set blog title for notification
    private void setNotificationBlogTitle(NotificationViewHolder holder, String blogID) {
        // todo
    }

    // view holder for notifications
    class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notifIcon;
        TextView notifFrom, notifMessage, blogTitle, notifDate;

        public NotificationViewHolder(View view) {
            super(view);
            notifIcon = view.findViewById(R.id.notifIcon);
            notifFrom = view.findViewById(R.id.notifFrom);
            notifMessage = view.findViewById(R.id.notifMessage);
            blogTitle = view.findViewById(R.id.blogTitle);
            notifDate = view.findViewById(R.id.notifDate);
        }
    }
}
