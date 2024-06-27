package com.example.onyjase.models;

import java.util.List;

public class User {
    private String userID;
    private String username;
    private String email;
    private String role; // user or admin
    private String imageURL;
    private List<String> followings; // list of user ids
    private List<String> favorites; // list of blog ids

    // constructor
    // No-argument constructor needed for Firestore
    public User() {
    }

    public User(String userID, String username, String email, String role, String imageURL, List<String> followings, List<String> favorites) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.role = role;
        this.imageURL = imageURL;
        this.followings = followings;
        this.favorites = favorites;
    }

    // getters
    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getImageURL() {
        return imageURL;
    }

    public List<String> getFollowings() {
        return followings;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    // Setters
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setFollowings(List<String> followings) {
        this.followings = followings;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }
}
