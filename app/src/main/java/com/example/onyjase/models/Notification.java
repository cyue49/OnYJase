package com.example.onyjase.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Notification {
    private String notificationID;
    private String fromUserID;
    private String toUserID;
    private String blogID;
    private String type; // like or comment
    private @ServerTimestamp Date timestamp;

    // constructor
    public Notification(String notificationID, String fromUserID, String toUserID, String blogID, String type) {
        this.notificationID = notificationID;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.blogID = blogID;
        this.type = type;
    }

    public Notification(String notificationID, String fromUserID, String toUserID, String blogID, String type, Date timestamp) {
        this.notificationID = notificationID;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.blogID = blogID;
        this.type = type;
        this.timestamp = timestamp;
    }

    // getters
    public String getNotificationID() {
        return notificationID;
    }

    public String getFromUserID() {
        return fromUserID;
    }

    public String getToUserID() {
        return toUserID;
    }

    public String getBlogID() {
        return blogID;
    }

    public String getType() {
        return type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // setters
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public void setBlogID(String blogID) {
        this.blogID = blogID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
