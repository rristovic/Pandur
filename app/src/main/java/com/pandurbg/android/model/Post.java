package com.pandurbg.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class Post implements Parcelable {
    private String postId;
    private PostCategory category;
    private Location location;
    private String description;
    private String street;
    private String time;
    private User user;
    private int approvals;



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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getApprovals() {
        return approvals;
    }

    public void setApprovals(int approvals) {
        this.approvals = approvals;
    }

    protected Post(Parcel in) {
        postId = in.readString();
        category = (PostCategory) in.readValue(PostCategory.class.getClassLoader());
        location = (Location) in.readValue(Location.class.getClassLoader());
        description = in.readString();
        street = in.readString();
        time = in.readString();
        user = (User) in.readValue(User.class.getClassLoader());
        approvals = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postId);
        dest.writeValue(category);
        dest.writeValue(location);
        dest.writeString(description);
        dest.writeString(street);
        dest.writeString(time);
        dest.writeValue(user);
        dest.writeInt(approvals);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}