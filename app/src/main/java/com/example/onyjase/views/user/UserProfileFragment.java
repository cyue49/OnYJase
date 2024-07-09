package com.example.onyjase.views.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentUserProfileBinding;
import com.example.onyjase.models.User;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserProfileFragment extends Fragment {
    private FragmentUserProfileBinding binding;
    private AppViewModel viewModel;
    private FirebaseAuth mAuth;
    private NavController navController;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri curImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Set user details
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.editUsername.setText(user.getUsername());
                binding.userEmail.setText(user.getEmail());
                if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
                    Glide.with(requireContext())
                            .load(user.getImageURL())
                            .placeholder(R.drawable.ic_user_placeholder)
                            .circleCrop()
                            .into(binding.profilePhoto);
                }
            }
        });

        // Set up ViewPager with TabLayout
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("My Blogs");
            } else if (position == 1) {
                tab.setText("My Comments");
            }
        }).attach();

        // Click listener for logout button
        binding.logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            viewModel.setUser(null);
            navController.navigate(R.id.action_appFragment_to_signInFragment);
        });

        // Click listener for profile photo to change it
        binding.profilePhoto.setOnClickListener(v -> pickImage());

        // Click listener for edit username button
        binding.editUsernameButton.setOnClickListener(v -> {
            binding.editUsername.setEnabled(true);
            binding.saveProfileButton.setVisibility(View.VISIBLE);
        });

        // Click listener for saving updated profile
        binding.saveProfileButton.setOnClickListener(v -> {
            updateUserProfile();
            binding.editUsername.setEnabled(false);
            binding.saveProfileButton.setVisibility(View.GONE);
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        curImageUri = data.getData();
                        binding.profilePhoto.setImageURI(curImageUri);
                        uploadProfilePhoto(curImageUri);
                    }
                }
            }
    );

    private void uploadProfilePhoto(Uri imageUri) {
        String userID = viewModel.getUser().getValue().getUserID();
        StorageReference storageRef = storage.getReference().child("users/" + userID + "/profile_photo");

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                db.collection("users").document(userID)
                        .update("imageURL", uri.toString())
                        .addOnSuccessListener(aVoid -> Glide.with(requireContext())
                                .load(uri)
                                .placeholder(R.drawable.ic_user_placeholder)
                                .circleCrop()
                                .into(binding.profilePhoto));
            });
        }).addOnFailureListener(e -> {
            // Handle failure
            Toast.makeText(requireContext(), "Failed to update upload profile photo. Please try again.", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserProfile() {
        String newUsername = binding.editUsername.getText().toString();
        String userID = viewModel.getUser().getValue().getUserID();

        db.collection("users").document(userID)
                .update("username", newUsername)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    User updatedUser = viewModel.getUser().getValue();
                    if (updatedUser != null) {
                        updatedUser.setUsername(newUsername);
                        viewModel.setUser(updatedUser);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(requireContext(), "Failed to update username. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new MyBlogsFragment();
            } else {
                return new MyCommentsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}