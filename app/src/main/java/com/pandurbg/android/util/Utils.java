package com.pandurbg.android.util;


import android.content.Context;
import android.widget.Toast;

import com.pandurbg.android.R;
import com.pandurbg.android.model.PostCategory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class Utils {
    public static String getCurrentUTCTimeString() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


}
