package com.pandurbg.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class Location implements Parcelable {
    public String postId;
    public double latitude;
    public double longitude;

    public Location(){}

    public Location(String postId, double latitude, double longitude) {
        this.latitude = latitude;
        this.postId = postId;
        this.longitude = longitude;
    }

    protected Location(Parcel in) {
        postId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}