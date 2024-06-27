package com.example.onyjase.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Blog {
    private String blogID;
    private String userID;
    private String title;
    private String content;
    private String imageURL;
    private int likes;
    //private Date timestamp;
    private @ServerTimestamp Date timestamp;

    // No-argument constructor needed for Firestore
    public Blog() {
    }

    // constructor
    public Blog(String blogID, String userID, String title, String content, String imageURL, int likes) {
        this.blogID = blogID;
        this.userID = userID;
        this.title = title;
        this.content = content;
        this.imageURL = imageURL;
        this.likes = likes;
    }

    // getters
    public String getBlogID() {
        return blogID;
    }

    public String getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getLikes() {
        return likes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // setters
    public void setBlogID(String blogID) {
        this.blogID = blogID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
