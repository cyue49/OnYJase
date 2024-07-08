package com.example.onyjase.views.blogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onyjase.R;
import com.example.onyjase.adapters.BlogAdapter;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Comparator;
import java.util.List;

public class ExploreFragment extends Fragment {

    private AppViewModel viewModel;
    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // Initialize BlogAdapter and set it to the RecyclerView
        blogAdapter = new BlogAdapter(viewModel, requireActivity());
        recyclerView.setAdapter(blogAdapter);

        // Load blogs from Firestore
        loadBlogs();

        return view;
    }

    private void loadBlogs() {
        // Fetch blogs from Firestore and update the adapter
        FirebaseFirestore.getInstance().collection("blogs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Blog> blogs = task.getResult().toObjects(Blog.class);
                        sortBlogsByDate(blogs);
                        blogAdapter.setBlogs(blogs);
                    } else {
                        // Handle the error
                    }
                });
    }

    // sort list of blog by timestamp, with the latest first
    private void sortBlogsByDate(List<Blog> blogs) {
        blogs.sort(new Comparator<Blog>() {
            @Override
            public int compare(Blog o1, Blog o2) {
                if (o1.getTimestamp().equals(o2.getTimestamp())) return 0;
                return o1.getTimestamp().compareTo(o2.getTimestamp()) * -1;
            }
        });
    }
}