package com.example.onyjase.views.notifications;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.onyjase.adapters.NotificationsAdapter;
import com.example.onyjase.databinding.FragmentNotificationsBinding;
import com.example.onyjase.models.Notification;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

// Fragment for page displaying notifications
public class NotificationsFragment extends Fragment {
    FragmentNotificationsBinding binding;

    // view model
    AppViewModel viewModel;

    // firebase firestore
    FirebaseFirestore db;

    // recycle view adapter & list
    NotificationsAdapter newNotificationsAdapter;
    ArrayList<Notification> newNotifications;
    NotificationsAdapter oldNotificationsAdapter;
    ArrayList<Notification> oldNotifications;

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
        newNotifications = new ArrayList<>();
        oldNotifications = new ArrayList<>();

        // setting adapter for new notifications
        binding.newNotificationsList.setLayoutManager(new LinearLayoutManager(getContext()));
        newNotificationsAdapter = new NotificationsAdapter(newNotifications, getContext(), db);
        // on click listener for each notification
        newNotificationsAdapter.setOnNotificationClickListener(this::notificationOnClickAction);
        binding.newNotificationsList.setAdapter(newNotificationsAdapter);

        // setting adapter for old notifications
        binding.oldNotificationsList.setLayoutManager(new LinearLayoutManager(getContext()));
        oldNotificationsAdapter = new NotificationsAdapter(oldNotifications, getContext(), db);
        oldNotificationsAdapter.setOnNotificationClickListener(this::notificationOnClickAction);
        binding.oldNotificationsList.setAdapter(oldNotificationsAdapter);

        // set all notifications for this user
        setAllNotifications(viewModel.getUser().getValue().getUserID(), newNotificationsAdapter, true);
        setAllNotifications(viewModel.getUser().getValue().getUserID(), oldNotificationsAdapter, false);

        // =============================================== Buttons Listeners ===============================================
        // clear notifications button
        binding.clearBtn.setOnClickListener(v -> {
            // set all current notifications to not show
            for (Notification notification : newNotifications) {
                notification.setShow(false);
                String notifID = notification.getNotificationID();
                db.collection("notifications").document(notifID)
                        .set(notification);
            }

            // clear list for adapter
            newNotifications.clear();
            newNotificationsAdapter.reload();
        });

        // refresh notifications button
        binding.refreshBtn.setOnClickListener(v -> {
            FragmentTransactionHelper.loadFragment(requireContext(), new NotificationsFragment());
        });

        // tapping on new notifications divider (show/collapse new notifications)
        binding.newNotifsDivider.setOnClickListener(v -> {
            if (binding.newNotificationsList.getVisibility() == View.VISIBLE) {
                binding.newNotificationsList.setVisibility(View.GONE);
            } else {
                binding.newNotificationsList.setVisibility(View.VISIBLE);
            }
        });

        // tapping on old notifications divider (show/collapse old notifications)
        binding.oldNotifsDivider.setOnClickListener(v -> {
            if (binding.oldNotificationsList.getVisibility() == View.VISIBLE) {
                binding.oldNotificationsList.setVisibility(View.GONE);
            } else {
                binding.oldNotificationsList.setVisibility(View.VISIBLE);
            }
        });
    }

    // =============================================== Functions ===============================================
    // set all notifications for user
    private void setAllNotifications(String userID, NotificationsAdapter adapter, boolean isNewNotif) {
        // get all notifications where userID equals current user
        CollectionReference colRef = db.collection("notifications");
        Query query = isNewNotif ? colRef.whereEqualTo("toUserID", userID).whereEqualTo("show", true).whereEqualTo("newNotif", true) : colRef.whereEqualTo("toUserID", userID).whereEqualTo("show", true).whereEqualTo("newNotif", false);
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String notifID = document.getString("notificationID");
                            String fromUserID = document.getString("fromUserID");
                            String blogID = document.getString("blogID");
                            String type = document.getString("type");
                            boolean show = Boolean.TRUE.equals(document.getBoolean("show"));
                            boolean isNew = Boolean.TRUE.equals(document.getBoolean("newNotif"));
                            Date timestamp = Objects.requireNonNull(document.getTimestamp("timestamp")).toDate();


                            Notification notification = new Notification(notifID, fromUserID, userID, blogID, type, show, isNew, timestamp);
                            if (isNewNotif) {
                                newNotifications.add(notification);
                            } else {
                                oldNotifications.add(notification);
                            }
                        }
                        if (isNewNotif) {
                            sortNotificationsByDate(newNotifications);
                        } else {
                            sortNotificationsByDate(oldNotifications);
                        }
                        adapter.reload();
//                        updateNotificationsToOld();
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

    // on click action of a notification
    private void notificationOnClickAction(String blogID) {
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
    }

    // set new notifications to old in database when user opens notification tab
    private void updateNotificationsToOld() {
        for (Notification notification : newNotifications) {
            notification.setNewNotif(false);
            String notifID = notification.getNotificationID();
            db.collection("notifications").document(notifID).set(notification);
        }
    }

    // set all new notifications to old when leaving notif tab
    @Override
    public void onStop() {
        super.onStop();
        updateNotificationsToOld();
    }
}