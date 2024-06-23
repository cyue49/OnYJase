package com.example.onyjase.views.blogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentNewPostBinding;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class NewPostFragment extends Fragment {
    FragmentNewPostBinding binding;

    // ui components variables
    Button cancelBtn, postBtn;
    TextInputEditText titleInput, contentInput;
    RadioButton learnRadio, examRadio, bill96Radio, otherRadio;
    RadioGroup radioGroup;

    // selecting image
    ImageView selectImgBtn, selectImgBox;
    Uri curImage;

    // firebase firestore
    FirebaseFirestore db;

    // firebase auth
    FirebaseAuth mAuth;

    // firebase storage
    FirebaseStorage storage;

    // view model
    AppViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
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
        learnRadio = binding.learnRadio;
        examRadio = binding.examRadio;
        bill96Radio = binding.bill96Radio;
        otherRadio = binding.otherRadio;
        radioGroup = binding.tagsRadioGroup;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // cancel button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputs();
                if (viewModel.getUser().getValue().getRole().equals("admin")){
                    loadFragment(new WriteNewFragment());
                } else {
                    loadFragment(new BlogsFeedFragment());
                }
            }
        });
    }

    // clear all inputs
    private void clearInputs() {
        titleInput.setText("");
        contentInput.setText("");
        curImage = null;
        selectImgBox.setImageResource(R.drawable.blue_rectangle_border);
        radioGroup.clearCheck();
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
                        curImage = data.getData();
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