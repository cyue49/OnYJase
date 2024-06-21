package com.example.onyjase.views.blogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentNewBlogBinding;
import com.example.onyjase.utils.ImageHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

// Fragment for the page for creating a new blog
public class NewBlogFragment extends Fragment {
    FragmentNewBlogBinding binding;

    // ui components variables
    Button cancelBtn, postBtn;
    TextInputEditText titleInput, contentInput;

    // selecting image
    ImageView selectImgBtn, selectImgBox;
    byte[] curImage;

    // firebase firestore
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewBlogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        cancelBtn = binding.cancel;
        postBtn = binding.post;
        titleInput = binding.title;
        contentInput = binding.content;
        selectImgBtn = binding.selectImgBtn;
        selectImgBox = binding.selectedImg;
        db = FirebaseFirestore.getInstance();

        // cancel button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputs();
                loadFragment(new BlogsFeedFragment());
            }
        });

        // post button

        // select image btn
        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        // select image box
        selectImgBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    // clear all inputs
    private void clearInputs() {
        titleInput.setText("");
        contentInput.setText("");
        curImage = null;
        selectImgBox.setImageResource(R.drawable.blue_rectangle_border);
    }

    // picking image
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectImgBox.setImageURI(data.getData());

                        try {
                            curImage = ImageHelper.uriToBytes(requireContext(), data.getData());
                        } catch (IOException e) {
                            // handle exception
                            Toast.makeText(requireContext(), "Error selecting image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            );

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}