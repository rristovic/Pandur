package com.pandurbg.android.ui;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pandurbg.android.R;
import com.pandurbg.android.db.PostRepository;
import com.pandurbg.android.model.Location;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.util.DummyData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = findViewById(R.id.btn_send);
        PostRepository.getInstance(this).getUserFeed(44, 21).observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                if (posts != null)
                    Log.d("MainActivity", "Data received, size: " + posts.size());
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostRepository.getInstance(MainActivity.this).
                        addNewPost(DummyData.generatePostCat(), "Novi Post", "Nova Ulica", 44, 21);
            }
        });
    }
}
