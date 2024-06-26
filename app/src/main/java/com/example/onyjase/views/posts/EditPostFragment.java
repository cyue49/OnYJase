package com.example.onyjase.views.posts;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentEditPostBinding;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class EditPostFragment extends Fragment {
    FragmentEditPostBinding binding;

    // ui components variables
    Button cancelBtn, updateBtn;
    TextInputEditText titleInput, contentInput;
    RadioButton learnRadio, examRadio, bill96Radio, otherRadio;
    RadioGroup radioGroup;

    // selecting image
    ImageView selectImgBtn, selectImgBox;
    Uri curImage;

    // firebase firestore
    FirebaseFirestore db;

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
        binding = FragmentEditPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        cancelBtn = binding.cancel;
        updateBtn = binding.update;
        titleInput = binding.title;
        contentInput = binding.content;
        selectImgBtn = binding.selectImgBtn;
        selectImgBox = binding.selectedImg;
        learnRadio = binding.learnRadio;
        examRadio = binding.examRadio;
        bill96Radio = binding.bill96Radio;
        otherRadio = binding.otherRadio;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);


    }
}