//package com.example.onyjase.views.auth;
//
//import static android.content.ContentValues.TAG;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import com.example.onyjase.R;
//import com.example.onyjase.databinding.FragmentSignInBinding;
//import com.example.onyjase.databinding.FragmentSignUpBinding;
//import com.example.onyjase.models.User;
//import com.example.onyjase.viewmodels.AppViewModel;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SignInFragment extends Fragment {
//    FragmentSignInBinding binding;
//    NavController navController;
//
//    Button userSignInBtn, adminSignInBtn, toSignUpBtn;
//
//    AppViewModel viewModel;
//
//    // firebase auth
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        // Check Firebase connection
//        db.collection("test").document("connection").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Log.d(TAG, "Firebase is connected successfully.");
//                } else {
//                    Log.d(TAG, "Failed to connect to Firebase.", task.getException());
//                }
//            }
//        });
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentSignInBinding.inflate(inflater,container,false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);
//
//        userSignInBtn = binding.button;
//        toSignUpBtn = binding.button2;
//        adminSignInBtn = binding.button3;
//        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
//        mAuth = FirebaseAuth.getInstance();
//
//        // temporary sign in as user
//        userSignInBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = "test@email.com";
//                String password = "Password123";
//
//                FirebaseUser currentUser = mAuth.getCurrentUser();
//                if (currentUser != null) {
//                    mAuth.signOut();
//                }
//
//                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//                            User user = new User(mAuth.getCurrentUser().getUid(), "test", email, "user", "", new ArrayList<>(), new ArrayList<>());
//                            viewModel.setUser(user);
//                            navController.navigate(R.id.action_signInFragment_to_appFragment);
//                        }
//                    }
//                });
//            }
//        });
//
//        // temporary sign in as admin
//        adminSignInBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = "testadmin@email.com";
//                String password = "Password123";
//
//                FirebaseUser currentUser = mAuth.getCurrentUser();
//                if (currentUser != null) {
//                    mAuth.signOut();
//                }
//
//                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//                            User user = new User(mAuth.getCurrentUser().getUid(), "test", email, "admin", "", new ArrayList<>(), new ArrayList<>());
//                            viewModel.setUser(user);
//                            navController.navigate(R.id.action_signInFragment_to_appFragment);
//                        }
//                    }
//                });
//            }
//        });
//
//        // sign in fragment to sign up fragment
//        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
//            }
//        });
//    }
//}

package com.example.onyjase.views.auth;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentSignInBinding;
import com.example.onyjase.models.User;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SignInFragment extends Fragment {
    FragmentSignInBinding binding;
    NavController navController;

    Button userSignInBtn, adminSignInBtn, toSignUpBtn;

    AppViewModel viewModel;

    // firebase auth
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check Firebase connection
        db.collection("test").document("connection").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Firebase is connected successfully.");
                } else {
                    Log.d(TAG, "Failed to connect to Firebase.", task.getException());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
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

        userSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                signInUser(email, password, "user");
            }
        });

        adminSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                signInUser(email, password, "admin");
            }
        });

        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });
    }

    private void signInUser(String email, String password, String expectedRole) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String role = document.getString("role");
                                        if (role != null && role.equals(expectedRole)) {
                                            User userModel = new User(user.getUid(), "test", email, role, "", new ArrayList<>(), new ArrayList<>());
                                            viewModel.setUser(userModel);
//                                            if (role.equals("admin")) {
//                                                navController.navigate(R.id.action_signInFragment_to_adminFragment); // Navigate to admin screen
//                                            } else {
                                                navController.navigate(R.id.action_signInFragment_to_appFragment); // Navigate to user screen
//                                            }
                                        } else {
                                            // Handle role mismatch
                                            Log.d(TAG, "Role mismatch.");
                                            mAuth.signOut();
                                            // Show a message to the user about role mismatch
                                        }
                                    } else {
                                        // Handle user document not found
                                        Log.d(TAG, "User document not found.");
                                        mAuth.signOut();
                                        // Show a message to the user about user document not found
                                    }
                                } else {
                                    // Handle task failure
                                    Log.d(TAG, "Failed to get user document.", task.getException());
                                    mAuth.signOut();
                                    // Show a message to the user about task failure
                                }
                            }
                        });
                    }
                } else {
                    // Handle sign-in failure
                    Log.d(TAG, "Sign-in failed.", task.getException());
                    // Show a message to the user about sign-in failure
                }
            }
        });
    }
}
