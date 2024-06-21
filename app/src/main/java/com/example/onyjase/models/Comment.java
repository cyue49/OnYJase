package com.example.onyjase.models;

public class Comment {
    private String commentID;
    private String userID;
    private String blogID;
    private String content;
    private String stickerURL;
    private long timestamp;

    // constructor
    public Comment(String commentID, String userID, String blogID, String content, String stickerURL, long timestamp) {
        this.commentID = commentID;
        this.userID = userID;
        this.blogID = blogID;
        this.content = content;
        this.stickerURL = stickerURL;
        this.timestamp = timestamp;
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

    public long getTimestamp() {
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

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
