package com.pandurbg.android.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pandurbg.android.model.Location;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.model.PostCategory;
import com.pandurbg.android.util.DummyData;
import com.pandurbg.android.util.Utils;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class PostRepository {
    private static final int DEFAULT_RADIUS_KM = 100;

    private static PostRepository mInstance;
    private static Context mContext;
    private final GeoFire mGeoFire;
    private final PandurFirebaseDatabase mDatabase;
    private MutableLiveData<List<Post>> mUserFeed;

    public static PostRepository getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PostRepository.class) {
                if (mInstance == null) {
                    mInstance = new PostRepository(context.getApplicationContext());

                }
            }
        }
        return mInstance;
    }

    private PostRepository(Context context) {
        mDatabase = PandurFirebaseDatabase.getInstance();
        mContext = context.getApplicationContext();
        mGeoFire = new GeoFire(mDatabase.getLocationsTable());
    }

    public void addNewPost(final PostCategory category, final String postDescription, final String postStreet, double postLatitude, double postLongitude) {
        final DatabaseReference ref = PandurFirebaseDatabase.getInstance().getPostsTable().push();
        mGeoFire.setLocation(ref.getKey(), new GeoLocation(postLatitude, postLongitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                final Post p = new Post();
                p.setUser(DummyData.generateUser());
                p.setTime(Utils.getCurrentUTCTimeString());
                p.setPostId(ref.getKey());
                p.setDescription(postDescription);
                p.setCategory(category);
                p.setStreet(postStreet);
                ref.setValue(p);
            }
        });
    }


    public LiveData<List<Post>> getUserFeed(double userLatitude, double userLongitude) {
        if (mUserFeed == null) {
            mUserFeed = new MutableLiveData<>();
        }
        getLatestFeed(userLatitude, userLongitude);
        return mUserFeed;
    }

    private void getLatestFeed(double userLatitude, double userLongitude) {
        final List<Location> locations = new LinkedList<>();
        GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(userLatitude, userLongitude), DEFAULT_RADIUS_KM);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                locations.add(new Location(key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                getPostByLocation(locations);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getPostByLocation(final List<Location> locations) {
        DatabaseReference posts = mDatabase.getPostsTable();
        final List<Post> userFeed = new LinkedList<>();
        final CountDownLatch countDownLatch = new CountDownLatch(locations.size());
        // Start post complete listener
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean done = false;
                while (!done)
                    try {
                        countDownLatch.await();
                        done = true;
                        mUserFeed.postValue(userFeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }).start();
        ;
        for (Location location : locations) {
            // Get all posts
            posts.child(location.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userFeed.add(dataSnapshot.getValue(Post.class));
                    countDownLatch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


}
