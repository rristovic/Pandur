package com.pandurbg.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class
User implements Parcelable {
    private String _id;
    private String userName;
    private String email;
    private String imageLink;

    public User() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    protected User(Parcel in) {
        _id = in.readString();
        userName = in.readString();
        email = in.readString();
        imageLink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(imageLink);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}