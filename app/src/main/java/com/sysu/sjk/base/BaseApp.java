package com.sysu.sjk.base;

import android.app.Application;
import android.content.Context;

import com.sysu.sjk.utils.FileUtils;
import com.sysu.sjk.utils.Logger;

/**
 * Created by sjk on 16-10-21.
 */
public class BaseApp extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log("1");
        mContext = this;

        FileUtils.directory = getFilesDir().getAbsolutePath();
    }
}
