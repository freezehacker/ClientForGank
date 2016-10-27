package com.sysu.sjk.utils;

import android.util.Log;

/**
 * Created by sjk on 16-10-21.
 */
public class Logger {

    private static final String TAG = "vita";

    public static void log(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String that needs to log out is null!");
        }

        Log.d(TAG, s);
    }
}
