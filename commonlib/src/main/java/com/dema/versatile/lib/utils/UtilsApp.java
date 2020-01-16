package com.dema.versatile.lib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class UtilsApp {

    public static int getAppVersionCode(Context context, String strPackageName) {
        if (null == context || TextUtils.isEmpty(strPackageName)) {
            return 0;
        }

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(strPackageName, 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getAppVersionName(Context context, String strPackageName) {
        if (null == context || TextUtils.isEmpty(strPackageName)) {
            return null;
        }

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(strPackageName, 0);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getMyAppPackageName(Context context) {
        if (context == null) {
            return null;
        }

        return context.getPackageName();
    }

    public static int getMyAppVersionCode(Context context) {
        if (context == null) {
            return 0;
        }
        return getAppVersionCode(context, getMyAppPackageName(context));
    }

    public static String getMyAppVersionName(Context context) {
        if (context == null) {
            return null;
        }

        return getAppVersionName(context, getMyAppPackageName(context));
    }

}
