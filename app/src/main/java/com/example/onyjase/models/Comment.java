package com.example.onyjase.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {
    private String commentID;
    private String userID;
    private String blogID;
    private String content;
    private String stickerURL;
    private @ServerTimestamp Date timestamp;
    // constructor
    public Comment(String commentID, String userID, String blogID, String content, String stickerURL) {
        this.commentID = commentID;
        this.userID = userID;
        this.blogID = blogID;
        this.content = content;
        this.stickerURL = stickerURL;
    }

    // getters
    public String getCommentID() {
        return commentID;
    }

    public String getUserID() {
        return userID;
    }

    public String getBlogID() {
        return blogID;
    }

    public String getContent() {
        return content;
    }

    public String getStickerURL() {
        return stickerURL;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // setters
    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setBlogID(String blogID) {
        this.blogID = blogID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStickerURL(String stickerURL) {
        this.stickerURL = stickerURL;
    }
}
