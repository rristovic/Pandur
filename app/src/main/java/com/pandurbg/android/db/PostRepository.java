package com.pandurbg.android.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
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

import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class PostRepository {
    private static final double DEFAULT_RADIUS_KM = 15;
    private static double RADIUS_KM = 0.3;
    private static final double DEFAULT_RADIUS_INCREASE = RADIUS_KM;

    private static PostRepository mInstance;
    private static Context mContext;
    private final GeoFire mGeoFire;
    private final PandurFirebaseDatabase mDatabase;
    private MutableLiveData<Post> mUserFeed;
    private MutableLiveData<Post> mNewlyAdded;
    private GeoQuery mGeoQuery;
    private GeoQueryEventListener mGeoQueryEventListener;

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


    public LiveData<Post> getUserFeed() {
        if (mUserFeed == null) {
            mNewlyAdded = new MutableLiveData<>();
            mUserFeed = new MutableLiveData<>();
        }
        return mUserFeed;
    }

    public void setInitCoordinates(double userLatitude, double userLongitude) {
        getLatestFeed(userLatitude, userLongitude);
    }

    private void getLatestFeed(final double userLatitude, final double userLongitude) {
        mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(userLatitude, userLongitude), DEFAULT_RADIUS_KM);
        mGeoQuery.addGeoQueryEventListener(getGeoQueryListener());
    }

    private void getPostData(String postId) {
        mDatabase.getPostsTable().child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post p = dataSnapshot.getValue(Post.class);
                if (p != null) {
                    mUserFeed.setValue(p);
                    mDatabase.getPostsTable().child(p.getPostId()).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onLocationUpdate(double latitude, double longitude) {
        mGeoQuery.removeAllListeners();
        getLatestFeed(latitude, longitude);
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
//                        mUserFeed.postValue(new ArrayList<Post>(userFeed.values()));
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

    private GeoQueryEventListener getGeoQueryListener() {
        if(mGeoQueryEventListener == null) {
            mGeoQueryEventListener = new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.d("PostRepo", String.format("Key:%s, Latitude: %f, Longitude: %f", key, location.latitude, location.longitude));
                    getPostData(key);
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
//                if (RADIUS_KM <= DEFAULT_RADIUS_KM) {
//                    RADIUS_KM += DEFAULT_RADIUS_INCREASE;
//                    mGeoQuery.setRadius(RADIUS_KM);
//                }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            };
        }

        return mGeoQueryEventListener;
    }


}
