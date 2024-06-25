package com.example.onyjase.views.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentSignInBinding;
import com.example.onyjase.databinding.FragmentSignUpBinding;
import com.example.onyjase.models.User;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SignInFragment extends Fragment {
    FragmentSignInBinding binding;
    NavController navController;

    Button userSignInBtn, adminSignInBtn, toSignUpBtn;

    AppViewModel viewModel;

    // firebase auth
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        userSignInBtn = binding.button;
        toSignUpBtn = binding.button2;
        adminSignInBtn = binding.button3;
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        // temporary sign in as user
        userSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "test@email.com";
                String password = "Password123";

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mAuth.signOut();
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(mAuth.getCurrentUser().getUid(), "test", email, "user", "", new ArrayList<>(), new ArrayList<>());
                            viewModel.setUser(user);
                            navController.navigate(R.id.action_signInFragment_to_appFragment);
                        }
                    }
                });
            }
        });

        // temporary sign in as admin
        adminSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "testadmin@email.com";
                String password = "Password123";

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mAuth.signOut();
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(mAuth.getCurrentUser().getUid(), "test", email, "admin", "", new ArrayList<>(), new ArrayList<>());
                            viewModel.setUser(user);
                            navController.navigate(R.id.action_signInFragment_to_appFragment);
                        }
                    }
                });
            }
        });

        // sign in fragment to sign up fragment
        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });
    }
}