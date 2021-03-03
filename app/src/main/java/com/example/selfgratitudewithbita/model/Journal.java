package com.example.selfgratitudewithbita.model;

import android.net.Uri;

import com.google.firebase.Timestamp;


public class Journal {
    private String title;
    private String thought;
    private String imageUrl;
    private String userId;
    private String username;
    private Timestamp dateCreated;

    public Journal() {}

    public Journal(String title, String thought, String imageUrl, String userId, String username, Timestamp dateCreated) {
        this.title = title;
        this.thought = thought;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.username = username;
        this.dateCreated = dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }
}
