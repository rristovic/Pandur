package com.pandurbg.android.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

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

    public static void showLocationPermMissingDialog(final Activity context) {
        new AlertDialog.Builder(context)
                .setTitle("Lokacija onemogucena")
                .setMessage("Kako biste koristili ovu aplikaciju, molimo vas omogucite koriscenje lokacije u podesavanjima.")
                .setPositiveButton("Podesavanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                })
                .setNeutralButton("Odustani", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.finish();
                    }
                })
                .create().show();
    }

    public static void showLocationDissabledDialog(final Activity context) {
        new AlertDialog.Builder(context)
                .setTitle("Lokacija onemogucena")
                .setMessage("Kako biste koristili ovu aplikaciju, molimo vas omogucite koriscenje lokacije u podesavanjima.")
                .setPositiveButton("Podesavanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(viewIntent);
                    }
                })
                .setNeutralButton("Odustani", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.finish();
                    }
                })
                .create().show();
    }
}
