package com.example.onyjase.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onyjase.adapters.AllUsersAdapter;
import com.example.onyjase.databinding.FragmentAllUsersBinding;
import com.example.onyjase.models.User;
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

        adapter = new AllUsersAdapter(viewModel, requireActivity());

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
