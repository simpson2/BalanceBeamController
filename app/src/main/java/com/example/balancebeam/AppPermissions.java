package com.example.balancebeam;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class AppPermissions {
    public static final String TAG = "AppPermissions";

    public static boolean checkPermissions(Context context, String string) {
        boolean query = ContextCompat.checkSelfPermission(context, string) == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "checkPermissions: "+query);
        return query;
    }
}
