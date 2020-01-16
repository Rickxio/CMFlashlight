package com.dema.versatile.flashlight.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by XuChuanting
 * on 2018/8/16-18:51
 */
public class CleanUtil {

    public static void cleanFileAndEmptyDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        File dir = new File(path);
        if (!dir.exists()) {
            return;
        }

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null ) {
                for (File file : files) {
                    if(file.isFile()){
                        file.delete();
                    }else if(file.isDirectory()){
                        cleanFileAndEmptyDirectory(file.getAbsolutePath());
                    }
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }


    }

    public static double getMemoryUseRate(@NonNull Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return (double) mi.availMem / mi.totalMem;
    }


    /*
     * 採用了新的办法获取APK图标。之前的失败是由于android中存在的一个BUG,通过
     * appInfo.publicSourceDir = apkPath;来修正这个问题，详情參见:
     * http://code.google.com/p/android/issues/detail?id=9151
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }


    public static String getApkName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.loadLabel(pm).toString();
        }
        return null;
    }

    public static boolean isApkInstalled(Context context, String apkPath) {

        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            try {
                PackageInfo localPackageInfo = pm.getPackageInfo(info.packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
                return true;
            } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
                return false;
            }
        }
        return false;

    }

    public static List<String> getInstalledAppPackageName(Context context) {
        if (null == context)
            return null;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> listApplicationInfo = pm.getInstalledApplications(0);
        ArrayList<String> listResult = new ArrayList<String>();
        int nCount = listApplicationInfo.size();
        for (int nIndex = 0; nIndex < nCount; nIndex++)
            listResult.add(listApplicationInfo.get(nIndex).packageName);

        return listResult;
    }



}
