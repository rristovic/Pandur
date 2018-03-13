package com.pandurbg.android.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.pandurbg.android.R;
import com.pandurbg.android.model.Post;
import com.pandurbg.android.util.PostFeedAdapter;

import java.util.ArrayList;

public class PostFeedActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Post> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed);

        mDataset = getIntent().getParcelableArrayListExtra("data");

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        if(mDataset != null) {
            mAdapter = new PostFeedAdapter(mDataset);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            Log.d("AdapterError", "null");
        }

    }


    public static void startActivity(Context context, ArrayList<Post> posts) {
        Intent i = new Intent(context, PostFeedActivity.class);
        i.putParcelableArrayListExtra("data", posts);
        context.startActivity(i);
    }

}
