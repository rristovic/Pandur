package com.pandurbg.android.util;

import android.os.Handler;
import android.os.Looper;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Radovan Ristovic on 3/21/2018.
 * Quantox.com
 * radovanr995@gmail.com
 */

public class AppExecutor {
    private static Executor mPool = Executors.newFixedThreadPool(5);
    private LinkedList<Runnable> taskQueue = new LinkedList<>();

    public static void runTask(Runnable r) {
        mPool.execute(r);
    }

    public static void runOnUI(Runnable r) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(r);
    }

    public void enqueue(Runnable r) {
        taskQueue.add(r);
    }

    public void executeQueue() {
        for (Runnable r :
                taskQueue) {
            runOnUI(r);
        }
    }
}
