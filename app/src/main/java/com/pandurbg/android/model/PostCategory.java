package com.pandurbg.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class PostCategory implements Parcelable {
    private int _id;
    private String name;
    private String slug;

    public PostCategory() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    protected PostCategory(Parcel in) {
        _id = in.readInt();
        name = in.readString();
        slug = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(name);
        dest.writeString(slug);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PostCategory> CREATOR = new Parcelable.Creator<PostCategory>() {
        @Override
        public PostCategory createFromParcel(Parcel in) {
            return new PostCategory(in);
        }

        @Override
        public PostCategory[] newArray(int size) {
            return new PostCategory[size];
        }
    };
}