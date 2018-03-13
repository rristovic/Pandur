package com.pandurbg.android.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pandurbg.android.R;
import com.pandurbg.android.db.PandurFirebaseDatabase;
import com.pandurbg.android.db.PostRepository;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.util.DummyData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    public ArrayList<Post> mPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = findViewById(R.id.btn_send);

        PostRepository.getInstance(this).getUserFeed(44, 21).observe(this, new Observer<ArrayList<Post>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Post> posts) {
                if (posts != null)
                    mPosts = posts;
                Log.d("MainActivity", "Data received, size: " + posts.size());
            }
        });

        PostRepository.getInstance(this).getNewlyAddedPosts().observe(this, new Observer<Post>() {
            @Override
            public void onChanged(@Nullable Post post) {
                Log.d("NewlyAdded", post.toString());
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostRepository.getInstance(MainActivity.this).
                        addNewPost(DummyData.generatePostCat(), "Novi Post", "Nova Ulica", 44, 21);
            }
        });

        findViewById(R.id.btn_view_locations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosts != null)
                    MapsActivity.startActivity(MainActivity.this, mPosts);
                else {
                    Toast.makeText(MainActivity.this, "Posts are stil loading..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_reset_db).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DummyData d = new DummyData();
                d.pushDummyDataToFirebase();
            }
        });

        findViewById(R.id.bAddPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , AddPostActivity.class);
                startActivity(intent);

            }
        });

        findViewById(R.id.bOpenPostFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPosts!=null)
                    PostFeedActivity.startActivity(MainActivity.this, mPosts);
            }
        });
    }
}
