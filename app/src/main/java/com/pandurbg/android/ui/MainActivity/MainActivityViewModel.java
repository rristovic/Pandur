package com.pandurbg.android.ui.MainActivity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.pandurbg.android.db.PostRepository;
import com.pandurbg.android.model.Post;

import java.util.List;

/**
 * Created by Radovan Ristovic on 3/20/2018.
 * Quantox.com
 * radovanr995@gmail.com
 */

public class MainActivityViewModel extends AndroidViewModel {
    private LiveData<Post> mUserFeed;
    private PostRepository mRepo;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mRepo = PostRepository.getInstance(application);
    }

    public LiveData<Post> getUserFeed(double latitude, double longitude) {
        if (mUserFeed == null) {
            mUserFeed = mRepo.getUserFeed(latitude, longitude);
        }
        return mUserFeed;
    }

    public void onLocationUpdate(double latitude, double longitude) {

    }
}
