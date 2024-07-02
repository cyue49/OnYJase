package com.example.onyjase.views.notifications;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.onyjase.R;
import com.example.onyjase.adapters.NotificationsAdapter;
import com.example.onyjase.databinding.FragmentNotificationsBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.models.Notification;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.example.onyjase.views.posts.PostFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

// Fragment for page displaying notifications
public class NotificationsFragment extends Fragment {
    FragmentNotificationsBinding binding;

    // view model
    AppViewModel viewModel;

    // firebase firestore
    FirebaseFirestore db;

    // recycle view adapter & list
    NotificationsAdapter notificationsAdapter;
    ArrayList<Notification> notifications;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        db = FirebaseFirestore.getInstance();
        notifications = new ArrayList<>();

        // setting adapter for notifications
        binding.notificationsList.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsAdapter = new NotificationsAdapter(notifications, getContext(), db);
        // on click listener for each notification
        notificationsAdapter.setOnNotificationClickListener(blogID -> {
            // if current notification is a blog related notification
            if (!blogID.isEmpty()) {
                // check if this blog id still exists in db
                db.collection("blogs").document(blogID)
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // if blog id exists, navigates to corresponding blog page
                                    viewModel.setCurrentBlogID(blogID);
                                    FragmentTransactionHelper.loadFragment(requireContext(), new BlogFragment());
                                } else {
                                    // show confirmation dialog that this blog no longer exists
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
                                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                                    dialogBuilder.setMessage("This blog doesn't exist. It might have been deleted by its owner")
                                            .setNegativeButton("Ok", ((dialog, which) -> dialog.dismiss()));
                                    dialogBuilder.create().show();
                                }
                            }
                        });
            }
        });
        binding.notificationsList.setAdapter(notificationsAdapter);

        // set all notifications for this user
        setAllNotifications(viewModel.getUser().getValue().getUserID(), notificationsAdapter);

        // =============================================== Buttons Listeners ===============================================
        binding.clearBtn.setOnClickListener(v -> {
            // set all current notifications to not show
            for (Notification notification : notifications) {
                notification.setShow(false);
                String notifID = notification.getNotificationID();
                db.collection("notifications").document(notifID)
                        .set(notification);
            }

            // clear list for adapter
            notifications.clear();
            notificationsAdapter.reload();
        });

        binding.refreshBtn.setOnClickListener(v -> {
            FragmentTransactionHelper.loadFragment(requireContext(), new NotificationsFragment());
        });
    }

    // =============================================== Functions ===============================================
    // set all notifications for user
    private void setAllNotifications(String userID, NotificationsAdapter adapter) {
        // get all notifications where userID equals current user
        CollectionReference colRef = db.collection("notifications");
        Query query = colRef.whereEqualTo("toUserID", userID).whereEqualTo("show", true);
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String notifID = document.getString("notificationID");
                            String fromUserID = document.getString("fromUserID");
                            String blogID = document.getString("blogID");
                            String type = document.getString("type");
                            boolean show = document.getBoolean("show");
                            Date timestamp = document.getTimestamp("timestamp").toDate();


                            Notification notification = new Notification(notifID, fromUserID, userID, blogID, type, show, timestamp);
                            notifications.add(notification);
                        }
                        sortNotificationsByDate(notifications);
                        adapter.reload();
                    } else {
                        Toast.makeText(requireContext(), "Error getting user notifications.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // sort notifications by date
    private void sortNotificationsByDate(ArrayList<Notification> notifications) {
        notifications.sort(new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                if (o1.getTimestamp().equals(o2.getTimestamp())) return 0;
                return o1.getTimestamp().compareTo(o2.getTimestamp()) * -1;
            }
        });
    }
}