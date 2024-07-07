package com.example.onyjase.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentAppBinding;
import com.example.onyjase.utils.FragmentTransactionHelper;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.admin.AdminProfileFragment;
import com.example.onyjase.views.blogs.BlogsFeedFragment;
import com.example.onyjase.views.blogs.NewBlogFragment;
import com.example.onyjase.views.posts.NewPostFragment;
import com.example.onyjase.views.notifications.NotificationsFragment;
import com.example.onyjase.views.posts.PostsFeedFragment;
import com.example.onyjase.views.user.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Fragment for the app once signed in
public class AppFragment extends Fragment {
    FragmentAppBinding binding;
    BottomNavigationView bottomNav;

    // view model
    AppViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // start on blogs home feed page
        FragmentTransactionHelper.loadFragment(requireContext(), new BlogsFeedFragment());

        // initializing variables
        bottomNav = binding.bottomNav;
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // check if keyboard is visible
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                view.getWindowVisibleDisplayFrame(rect);
                int screenHeight = view.getRootView().getHeight();
                int keyboardHeight = screenHeight - rect.bottom;

                if (keyboardHeight > 100) { // keyboard visible
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            }
        });

        // listeners for bottom navigation toolbar
        bottomNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.home:
                    FragmentTransactionHelper.loadFragment(requireContext(), new BlogsFeedFragment());
                    break;

                case R.id.info:
                    FragmentTransactionHelper.loadFragment(requireContext(), new PostsFeedFragment());
                    break;

                case R.id.add:
                    if (viewModel.getUser().getValue().getRole().equals("admin")){
                        showDialog();
                    } else {
                        FragmentTransactionHelper.loadFragment(requireContext(), new NewBlogFragment());
                    }
                    break;

                case R.id.notification:
                    FragmentTransactionHelper.loadFragment(requireContext(), new NotificationsFragment());
                    break;

                case R.id.profile:
                    if (viewModel.getUser().getValue().getRole().equals("admin")){
                        FragmentTransactionHelper.loadFragment(requireContext(), new AdminProfileFragment());
                    } else {
                        FragmentTransactionHelper.loadFragment(requireContext(), new UserProfileFragment());
                    }
                    break;
            }
            return true;
        });
    }

    // show bottom sheet dialog for choosing between new blog or new post
    private void showDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.new_post_bottom_sheet_layout);

        LinearLayout optionBlog = dialog.findViewById(R.id.writeNewBlog);
        LinearLayout optionPost = dialog.findViewById(R.id.writeNewPost);

        // new blog
        optionBlog.setOnClickListener(v -> {
            FragmentTransactionHelper.loadFragment(requireContext(), new NewBlogFragment());
            dialog.dismiss();
        });

        // new post
        optionPost.setOnClickListener(v -> {
            FragmentTransactionHelper.loadFragment(requireContext(), new NewPostFragment());
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}