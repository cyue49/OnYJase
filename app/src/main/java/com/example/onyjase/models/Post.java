package com.example.onyjase.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {
    private String postID;
    private String userID;
    private String title;
    private String content;
    private String tag; // learn, exam, bill96, or other
    private String imageURL;
    private @ServerTimestamp Date timestamp;

    public Post(String postID, String userID, String title, String content, String tag, String imageURL) {
        this.postID = postID;
        this.userID = userID;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.imageURL = imageURL;
    }

    // No-argument constructor needed for Firestore
    public Post() {
    }

    // getters
    public String getPostID() {
        return postID;
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

    public String getTag() {
        return tag;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // setters
    public void setPostID(String postID) {
        this.postID = postID;
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

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
