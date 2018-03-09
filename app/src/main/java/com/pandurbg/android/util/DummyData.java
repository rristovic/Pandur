package com.pandurbg.android.util;

import android.content.Context;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.pandurbg.android.db.PandurFirebaseDatabase;
import com.pandurbg.android.model.Location;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.model.PostCategory;
import com.pandurbg.android.model.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class DummyData {
    private static List<User> dummyUsers = new LinkedList<>();
    private static PostCategory postCategory;

    public void pushDummyDataToFirebase() {
        PandurFirebaseDatabase.getInstance().getLocationsTable().setValue(null);
        PandurFirebaseDatabase.getInstance().getPostsTable().setValue(null);
        GeoFire geoFire = new GeoFire(PandurFirebaseDatabase.getInstance().getLocationsTable());
        for (int i = 0; i < 1000; i++) {
            final DatabaseReference ref = PandurFirebaseDatabase.getInstance().getPostsTable().push();
            final Post p = generatePost(ref.getKey());
            geoFire.setLocation(ref.getKey(), new GeoLocation(p.getLocation().getLatitude(), p.getLocation().getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    ref.setValue(p);
                }
            });
        }
    }

    private Post generatePost(String postId) {
        Post p = new Post();
        p.setPostId(postId);
        p.setCategory(generatePostCat());
        p.setDescription("Lorem Ipsum");
        p.setLocation(generateLocation(postId));
        p.setTime("13/2/2018 12:12:00");
        p.setUser(generateUser());
        return p;
    }

    public static Location generateLocation(String postId) {
        Location location = new Location(postId, 42 + (Math.random() * ((46 - 42) + 1)), 19.38 + (Math.random() * ((22.16 - 19.38) + 1)));
        return location;
    }

    public static User generateUser() {
        if (dummyUsers.size() == 0) {
            for (int i = 0; i < 10; i++) {
                User u = new User();
                u.set_id(String.valueOf(String.valueOf(i)));
                u.setEmail(String.format("email_test_%d@pandur.com", i));
                u.setImageLink("https://upload.wikimedia.org/wikipedia/commons/c/ce/HH_Polizeihauptmeister_MZ.jpg");
                u.setUserName("Lorem Ipsum " + i);
                dummyUsers.add(u);
            }
        }

        return dummyUsers.get(new Random().nextInt(10));
    }

    public static PostCategory generatePostCat() {
        if (postCategory == null) {
            postCategory = new PostCategory();
            postCategory.set_id(0);
            postCategory.setName("Police Alert");
            postCategory.setSlug("police");
        }
        return postCategory;
    }
}
