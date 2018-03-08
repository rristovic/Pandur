package com.pandurbg.android.model;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class Post {
    private String postId;
    private PostCategory category;
    private Location location;
    private String description;
    private String time;
    private User user;

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public PostCategory getCategory() {
        return category;
    }

    public void setCategory(PostCategory category) {
        this.category = category;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
