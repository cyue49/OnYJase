package com.example.onyjase.models;

public class Post {
    private String postID;
    private String userID;
    private String title;
    private String content;
    private String imageURL;
    private long timestamp;

    public Post(String postID, String userID, String title, String content, String imageURL, long timestamp) {
        this.postID = postID;
        this.userID = userID;
        this.title = title;
        this.content = content;
        this.imageURL = imageURL;
        this.timestamp = timestamp;
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

    public String getImageURL() {
        return imageURL;
    }

    public long getTimestamp() {
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

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
