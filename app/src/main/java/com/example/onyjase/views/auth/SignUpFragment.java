//package com.example.onyjase.views.auth;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import com.example.onyjase.R;
//import com.example.onyjase.databinding.FragmentSignUpBinding;
//
//public class SignUpFragment extends Fragment {
//    FragmentSignUpBinding binding;
//    NavController navController;
//
//    Button signUpBtn, toSignInBtn;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentSignUpBinding.inflate(inflater,container,false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);
//
//        signUpBtn = binding.signUpBtn;
//        toSignInBtn = binding.toSignInBtn;
//
//        // sign up fragment to sign in fragment on sign up
//        signUpBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
//            }
//        });
//
//        // sign up fragment to sign in fragment
//        toSignInBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
//            }
//        });
//    }
//}
package com.example.onyjase.views.auth;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentSignUpBinding;
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

public class SignUpFragment extends Fragment {
    private static final String TAG = "SignUpFragment";
    FragmentSignUpBinding binding;
    NavController navController;

    EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText;
    Button signUpBtn;
    Button signInBtn;

    AppViewModel viewModel;

    // Firebase auth
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        emailEditText = binding.emailEditText;
        passwordEditText = binding.passwordEditText;
        confirmPasswordEditText = binding.confirmPasswordEditText;
        nameEditText = binding.usernameEditText;
        signUpBtn = binding.signUpBtn;
        signInBtn = binding.toSignInBtn;

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String name = nameEditText.getText().toString();

                if (validateInput(email, password, confirmPassword, name)) {
                    checkIfUserExistsAndSignUp(email, password, name);
                }
            }
        });
    }

    private boolean validateInput(String email, String password, String confirmPassword, String name) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkIfUserExistsAndSignUp(String email, String password, String name) {
        db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        signUpUser(email, password, name);
                    } else {
                        Toast.makeText(getContext(), "User already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to check if user exists", task.getException());
                    Toast.makeText(getContext(), "Failed to check if user exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUpUser(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(firebaseUser.getUid(), name, email, "user", "", new ArrayList<>(), new ArrayList<>());
                        db.collection("users").document(firebaseUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    viewModel.setUser(user);
                                    navController.navigate(R.id.action_signUpFragment_to_appFragment);
                                } else {
                                    Log.e(TAG, "Failed to create user in database", task.getException());
                                    Toast.makeText(getContext(), "Failed to create user in database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Failed to sign up", task.getException());
                    Toast.makeText(getContext(), "Failed to sign up: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
