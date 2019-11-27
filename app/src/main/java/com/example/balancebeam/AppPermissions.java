package com.example.balancebeam;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class AppPermissions {

    public static void checkPermission(Activity activity, Context context) {

        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission NOT granted", Toast.LENGTH_LONG).show();
        }
    }
}
