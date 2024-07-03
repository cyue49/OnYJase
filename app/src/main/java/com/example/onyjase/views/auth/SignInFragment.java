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
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;

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
//        adminSignInBtn = binding.button3;
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        userSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                signInUser(email, password, "user");
            }
        });

//        adminSignInBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = binding.emailEditText.getText().toString();
//                String password = binding.passwordEditText.getText().toString();
//                signInUser(email, password, "admin");
//            }
//        });

        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });
    }

    private void signInUser(String email, String password, String expectedRole) {
        if (email.isEmpty() || password.isEmpty()) {
            Log.d(TAG, "Email or password is empty.");
            return;
        }

        db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    // User exists, proceed to sign in with Firebase Auth
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    // Check user role
                                    db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    String role = document.getString("role");
                                                    if (role != null && role.equals(expectedRole)) {
                                                        User userModel = new User(firebaseUser.getUid(), "test", email, role, "", new ArrayList<>(), new ArrayList<>());
                                                        viewModel.setUser(userModel);
                                                        navController.navigate(R.id.action_signInFragment_to_appFragment); // Navigate to user screen
                                                    } else {

                                                        Log.d(TAG, "Role mismatch.");
                                                        mAuth.signOut();
                                                        Toast.makeText(getContext(), "Role mismatch. Please check your credentials.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {

                                                    Log.d(TAG, "User document not found.");
                                                    mAuth.signOut();
                                                    Toast.makeText(getContext(), "User document not found.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {

                                                Log.d(TAG, "Failed to get user document.", task.getException());
                                                mAuth.signOut();
                                                Toast.makeText(getContext(), "Failed to get user document.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Log.d(TAG, "Sign-in failed.", task.getException());
                                Toast.makeText(getContext(), "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "User does not exist in Firestore database.");
                    Toast.makeText(getContext(), "User does not exist. Please register first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
