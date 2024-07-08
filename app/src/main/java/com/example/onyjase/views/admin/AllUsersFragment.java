package com.example.onyjase.views.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onyjase.R;
import com.example.onyjase.adapters.AllUsersAdapter;
import com.example.onyjase.databinding.FragmentAllUsersBinding;
import com.example.onyjase.models.User;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AllUsersFragment extends Fragment {

    private FragmentAllUsersBinding binding;
    private AllUsersAdapter adapter;
    private AppViewModel viewModel;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new AllUsersAdapter(getActivity(), user -> showDeleteConfirmationDialog(user));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        loadAllUsers();
    }

    private void loadAllUsers() {
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    adapter.setUsers(users);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void showDeleteConfirmationDialog(User user) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.delete_blog_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("Delete", (dialog, which) -> deleteUserFromDb(user.getUserID()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        dialogBuilder.create().show();
    }

    private void deleteUserFromDb(String userID) {
        db.collection("users").document(userID).delete()
                .addOnSuccessListener(aVoid -> {
                    loadAllUsers(); // Reload users after deletion
                    // Show success message
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}