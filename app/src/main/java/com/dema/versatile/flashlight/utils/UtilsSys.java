package com.dema.versatile.flashlight.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Message;
import android.text.TextUtils;

import java.util.List;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMThreadPool;
import com.dema.versatile.lib.core.in.ICMThreadPoolListener;
import com.dema.versatile.lib.utils.UtilsApp;

/**
 * Created by banana on 2019/4/16.
 */
public class UtilsSys {

    public static void startActivity(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    public static void call(Context context) {
        startIntent(context, Intent.ACTION_CALL);
    }

    public static void sms(Context context) {
        startIntent(context, Intent.ACTION_SEND);
    }

    private static void camera(Context context) {
        startIntent(context, Intent.ACTION_CAMERA_BUTTON);
    }

    private static void startIntent(Context context, String action) {
        if (context == null) {
            return;
        }
        Intent dialIntent = new Intent(action);//跳转到拨号界面
        if (!(context instanceof Activity)) {
            dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(dialIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getTotalMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.totalMem;
    }

    public static long getAvailableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /**
     * 获取内存占用率
     *
     * @param context
     * @return
     */
    public static float getMemRate(Context context) {
        return 1 - getAvailableMemory(context) * 1f / getTotalMemory(context);
    }

    public interface Listener {
        void onComplete();
    }

    public static void killProcess(Context context, Listener listener) {
        ICMThreadPool icmThreadPool = CMLibFactory.getInstance().createInstance(ICMThreadPool.class);
        icmThreadPool.run(new ICMThreadPoolListener() {
            @Override
            public void onRun() {
                killBackgroundProcess(context);
            }

            @Override
            public void onComplete() {
                if (listener != null) {
                    listener.onComplete();
                }
            }

            @Override
            public void onMessage(Message msg) {

            }
        });
    }

    private static void killBackgroundProcess(Context context) {
        if (context == null) {
            return;
        }
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ApplicationInfo> list = context.getPackageManager().getInstalledApplications(0);
            for (ApplicationInfo ai : list) {
                if (null == ai || TextUtils.isEmpty(ai.packageName))
                    continue;

                // 忽略系统应用
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                    continue;

                if (ai.packageName.contains("google") || ai.packageName.contains("android")
                        || ai.packageName.contains("facebook")) {
                    continue;
                }

                // 忽略自身应用
                if (ai.packageName.contains(UtilsApp.getMyAppPackageName(context)))
                    continue;

                am.killBackgroundProcesses(ai.packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLauncherActivityName(Context context) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN, null);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(launcherIntent, 0);
        if (appList == null || appList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo resolveInfo = appList.get(i);
            String packageStr = resolveInfo.activityInfo.packageName;
            if (TextUtils.equals(packageStr, context.getPackageName())) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }
}
