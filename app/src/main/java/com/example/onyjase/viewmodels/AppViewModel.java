package com.example.onyjase.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onyjase.models.Blog;
import com.example.onyjase.models.Post;
import com.example.onyjase.models.User;

public class AppViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>(null);
    private MutableLiveData<Blog> currentBlog = new MutableLiveData<>(null);
    private MutableLiveData<Post> currentPost = new MutableLiveData<>(null);

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<Blog> getCurrentBlog() {
        return currentBlog;
    }

    public MutableLiveData<Post> getCurrentPost() {
        return currentPost;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public void setCurrentBlog(Blog blog) {
        this.currentBlog.setValue(blog);
    }

    public void setCurrentPost(Post post) {
        this.currentPost.setValue(post);
    }
}
