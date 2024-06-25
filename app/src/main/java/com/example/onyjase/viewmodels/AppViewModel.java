package com.example.onyjase.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onyjase.models.User;

public class AppViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>(null);
    private MutableLiveData<String> currentBlogID = new MutableLiveData<>(null);
    private MutableLiveData<String> currentPostID = new MutableLiveData<>(null);

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<String> getCurrentBlogID() {
        return currentBlogID;
    }

    public MutableLiveData<String> getCurrentPostID() {
        return currentPostID;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public void setCurrentBlogID(String blogID) {
        this.currentBlogID.setValue(blogID);
    }

    public void setCurrentPostID(String postID) {
        this.currentPostID.setValue(postID);
    }
}
