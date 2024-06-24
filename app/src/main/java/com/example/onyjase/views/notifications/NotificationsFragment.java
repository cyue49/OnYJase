package com.example.onyjase.views.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onyjase.R;
import com.example.onyjase.databinding.FragmentNotificationsBinding;
import com.example.onyjase.models.Blog;
import com.example.onyjase.viewmodels.AppViewModel;
import com.example.onyjase.views.blogs.BlogFragment;
import com.example.onyjase.views.posts.PostFragment;

import java.util.Date;

// Fragment for page displaying notifications
public class NotificationsFragment extends Fragment {
    FragmentNotificationsBinding binding;

    // view model
    AppViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing variables
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        binding.tempBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Blog tempBlog = new Blog("187385f6f3554765934e005793cd0b55", "4h38WnyDA2Ue4Kpvlo2oB8FTPJo1", "Some Tips About Learning French", "First, let's look at the definition of the work French from Wikipedia; French (français, French: [fʁɑ̃sɛ], or langue française, French: [lɑ̃ɡ fʁɑ̃sɛːz], or by some speakers, French: [lɑ̃ŋ fʁɑ̃sɛ]) is a Romance language of the Indo-European family. It descended from the Vulgar Latin of the Roman Empire, as did all Romance languages. French evolved from Gallo-Romance, the Latin spoken in Gaul, and more specifically in Northern Gaul. Its closest relatives are the other langues d'oïl—languages historically spoken in northern France and in southern Belgium, which French (Francien) largely supplanted. French was also influenced by native Celtic languages of Northern Roman Gaul like Gallia Belgica and by the (Germanic) Frankish language of the post-Roman Frankish invaders. Today, owing to the French colonial empire, there are numerous French-based creole languages, most notably Haitian Creole. A French-speaking person or nation may be referred to as Francophone in both English and French.", "blogs/187385f6f3554765934e005793cd0b55", 0, new Date());
                viewModel.setCurrentBlog(tempBlog);
                loadFragment(new BlogFragment());
            }
        });

        binding.tempBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PostFragment());
            }
        });
    }

    // go to another fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}