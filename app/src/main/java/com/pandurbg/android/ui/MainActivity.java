package com.pandurbg.android.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pandurbg.android.R;
import com.pandurbg.android.model.Location;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.util.DummyData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("pandur");
        final GeoFire geoFire = new GeoFire(myRef.child("locations"));

        final ArrayList<Location> locations = new ArrayList<>();
        btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(43, 21), 100);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        locations.add(new Location(key, location.latitude, location.longitude));
                        counter++;
                        Log.d("LOCATION", location.latitude + " : " + location.longitude);
                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {
                        DatabaseReference posts = myRef.child("posts");
                        for (Location location : locations) {
                            posts.child(location.postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("TEST", "PostId:" + dataSnapshot.getValue(Post.class).getPostId() + " Counter: " + (--counter));
                                    if (counter == 0) {
                                        Intent i = new Intent(MainActivity.this, MapsActivity.class);
                                        i.putParcelableArrayListExtra("data", locations);
                                        startActivity(i);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
//                        Intent i = new Intent(MainActivity.this, MapsActivity.class);
//                        i.putParcelableArrayListExtra("data", locations);
//                        startActivity(i);
                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });
            }
        });


    }
}
