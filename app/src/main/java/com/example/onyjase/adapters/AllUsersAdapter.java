package com.example.onyjase.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.databinding.ItemUserBinding;
import com.example.onyjase.models.User;
import com.example.onyjase.viewmodels.AppViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.UserViewHolder> {

    private List<User> users;
    private AppViewModel viewModel;
    private Activity context;
    private FirebaseFirestore firestore;
    private final OnUserInteractionListener listener;

    public interface OnUserInteractionListener {
        void onDeleteClick(User user);
    }

    public AllUsersAdapter(FragmentActivity context, OnUserInteractionListener listener) {
        this.context = context;
        this.listener = listener;
        this.users = List.of(); // Initialize with an empty list
        this.firestore = FirebaseFirestore.getInstance();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.username.setText(user.getUsername());
        holder.binding.userID.setText("User ID: " + user.getUserID());

        // Load profile photo
        if (user.getImageURL() != null && !user.getImageURL().isEmpty()) {
            Glide.with(context)
                    .load(user.getImageURL())
                    .placeholder(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(holder.binding.userProfilePhoto);
        } else {
            holder.binding.userProfilePhoto.setImageResource(R.drawable.ic_user_placeholder);
        }

        // Set up more options button
        holder.binding.moreOptionsButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.moreOptionsButton);
            popupMenu.inflate(R.menu.popup_menu_delete_only); // Ensure this menu resource only has the delete option
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.action_delete) {
                    listener.onDeleteClick(user);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}