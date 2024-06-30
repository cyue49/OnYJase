package com.example.onyjase.views.notifications;

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

import com.example.onyjase.R;
import com.example.onyjase.adapters.NotificationsAdapter;
import com.example.onyjase.databinding.FragmentNotificationsBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.models.Notification;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.example.onyjase.views.posts.PostFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
        binding.notificationsList.setAdapter(notificationsAdapter);

        // set all notifications for this user
        setAllNotifications(viewModel.getUser().getValue().getUserID(), notificationsAdapter);
    }

    private void setAllNotifications(String userID, NotificationsAdapter adapter) {
        // get all notifications where userID equals current user
        // todo
    }
}