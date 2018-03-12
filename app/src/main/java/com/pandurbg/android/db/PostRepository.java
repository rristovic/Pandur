package com.pandurbg.android.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pandurbg.android.model.Location;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.model.PostCategory;
import com.pandurbg.android.util.DummyData;
import com.pandurbg.android.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class PostRepository {
    private static final double DEFAULT_RADIUS_KM = 10;
    private static double RADIUS_KM = 0.3;
    private static final double DEFAULT_RADIUS_INCREASE = RADIUS_KM;

    private static PostRepository mInstance;
    private static Context mContext;
    private final GeoFire mGeoFire;
    private final PandurFirebaseDatabase mDatabase;
    private MutableLiveData<ArrayList<Post>> mUserFeed;
    private MutableLiveData<Post> mNewlyAdded;

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

    public void addNewPost(final PostCategory category, final String postDescription, final String postStreet, final double postLatitude, final double postLongitude) {
        final DatabaseReference ref = PandurFirebaseDatabase.getInstance().getPostsTable().push();
        mGeoFire.setLocation(ref.getKey(), new GeoLocation(postLatitude, postLongitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                final Post p = new Post();
                p.setLocation(new Location(ref.getKey(), postLatitude, postLongitude));
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


    public LiveData<ArrayList<Post>> getUserFeed(double userLatitude, double userLongitude) {
        if (mUserFeed == null) {
            mNewlyAdded = new MutableLiveData<>();
            mUserFeed = new MutableLiveData<>();
            getLatestFeed(new LinkedHashMap<String, Location>(100), userLatitude, userLongitude);
        }
        return mUserFeed;
    }

    public LiveData<Post> getNewlyAddedPosts() {
        if (mNewlyAdded == null) {
            mNewlyAdded = new MutableLiveData<>();
        }
        return mNewlyAdded;
    }

    private void getLatestFeed(final LinkedHashMap<String, Location> locations, final double userLatitude, final double userLongitude) {
        final GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(userLatitude, userLongitude), RADIUS_KM);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            private boolean initLoaded = false;

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("PostRepo", String.format("Key:%s, Latitude: %f, Longitude: %f", key, location.latitude, location.longitude));
                if (!initLoaded)
                    locations.put(key, new Location(key, location.latitude, location.longitude));
                else {
                    getOnePost(key);
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.d("PostRepo", "onGeoQueryReady() called.");
                if (RADIUS_KM <= DEFAULT_RADIUS_KM) {
                    RADIUS_KM += DEFAULT_RADIUS_INCREASE;
                    geoQuery.setRadius(RADIUS_KM);
                } else {
                    initLoaded = true;
                    getPostByLocation(locations);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getOnePost(String postId) {
        mDatabase.getPostsTable().child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post p = dataSnapshot.getValue(Post.class);
                if (p != null) {
                    mNewlyAdded.postValue(p);
                    mDatabase.getPostsTable().child(p.getPostId()).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getPostByLocation(final LinkedHashMap<String, Location> locations) {
        DatabaseReference posts = mDatabase.getPostsTable();
        final LinkedHashMap<String, Post> userFeed = new LinkedHashMap<>(locations.size());
        for (String key :
                locations.keySet()) {
            userFeed.put(key, null);
        }
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
                        mUserFeed.postValue(new ArrayList<Post>(userFeed.values()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }).start();

        for (String key : locations.keySet()) {
            // Get all posts
            posts.child(locations.get(key).getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post p = dataSnapshot.getValue(Post.class);
                    if (p != null) {
                        userFeed.put(p.getPostId(), p);
                        countDownLatch.countDown();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


}
