package com.example.onyjase.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onyjase.models.User;
import com.example.onyjase.models.Blog;

public class AppViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>(null);
    private MutableLiveData<String> currentBlogID = new MutableLiveData<>(null);
    private MutableLiveData<String> currentPostID = new MutableLiveData<>(null);
    private MutableLiveData<Blog> currentBlog = new MutableLiveData<>(null);

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<String> getCurrentBlogID() {
        return currentBlogID;
    }

    public MutableLiveData<String> getCurrentPostID() {
        return currentPostID;
    }

    public MutableLiveData<Blog> getCurrentBlog() {return currentBlog;}

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public void setCurrentBlogID(String blogID) {
        this.currentBlogID.setValue(blogID);
    }

    public void setCurrentPostID(String postID) {
        this.currentPostID.setValue(postID);
    }

    public void setCurrentBlog(Blog blog) {
        this.currentBlog.setValue(blog);
    }
}
