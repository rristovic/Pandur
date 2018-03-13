package com.pandurbg.android.db;

import android.content.Context;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class PandurFirebaseDatabase {
    private static final String DB_REF_NAME = "pandur_db";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_POSTS = "posts";

    private static PandurFirebaseDatabase mInstance;
    private final FirebaseDatabase mDatabase;
    private final DatabaseReference mDbReference;

    public static PandurFirebaseDatabase getInstance() {
        if (mInstance == null) {
            synchronized (PostRepository.class) {
                if (mInstance == null) {
                    mInstance = new PandurFirebaseDatabase();
                }
            }
        }
        return mInstance;
    }

    private PandurFirebaseDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mDbReference = mDatabase.getReference(DB_REF_NAME);
        mDbReference.keepSynced(true);
    }

    public DatabaseReference getLocationsTable() {
        return mDbReference.child(TABLE_LOCATIONS);
    }

    public DatabaseReference getPostsTable() {
        return mDbReference.child(TABLE_POSTS);
    }
}
