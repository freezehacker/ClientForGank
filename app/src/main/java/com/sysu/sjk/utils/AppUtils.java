package com.sysu.sjk.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.sysu.sjk.base.BaseApp;

/**
 * Created by sjk on 16-11-2.
 */
public class AppUtils {

    public static int getAppVersionCode() {
        try {
            return BaseApp.getContext().getPackageManager()
                    .getPackageInfo(BaseApp.getContext().getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
