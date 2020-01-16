package com.dema.versatile.flashlight.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author : wanghailong
 * @description:应用卸载
 */
public class SystemUtil {

    public static final String TAG = "whlTest";
    private static final String VALUE_STRING_PROC_STAT_FILE = "/proc/stat";

    /**
     * 卸载指定包名的应用
     *
     * @param packageName
     */
    public static void uninstall(Context context, String packageName) {
        if (hasActivityFinish(context)) {
            return;
        }
        boolean b = checkApplication(context, packageName);
        if (b) {
            Uri packageURI = Uri.parse("package:".concat(packageName));
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(packageURI);
            context.startActivity(intent);
        }
    }

    /**
     * 判断该包名的应用是否安装
     *
     * @param packageName
     * @return
     */
    @TargetApi(24)
    public static boolean checkApplication(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    /**
     * if activity destroy or finishing return true
     * else return false;
     *
     * @return
     */
    public static boolean hasActivityFinish(Context context) {
        if (context == null) {
            return true;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return activity.isFinishing() || activity.isDestroyed();
        }
        return false;
    }

    public static void forceStopPackage(Context context, String packageName) {
        try {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据要杀死的进程id执行Shell命令已达到杀死特定进程的效果
     *
     * @param pid
     */
    public static void killProcessByPid(int pid) {
        String command = "kill -9 " + pid + "\n";
        Runtime runtime = Runtime.getRuntime();
        Process proc;
        try {
            proc = runtime.exec(command);
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println(e);
        }

    }

    public static int getBatteryLevel(Context context) {
        if (null == context)
            return 0;

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (null == intent)
            return 0;

        return intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    }

    public static int getBatteryScale(Context context) {
        if (null == context)
            return 100;

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (null == intent)
            return 100;

        return intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
    }


    public static int getBatteryTemperature(Context context) {
        if (null == context)
            return 0;

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (null == intent)
            return 0;

        return intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
    }

    public static double getCpuTotalUsage() {
        long lCpuTotalTime1 = getCpuTotalTime();
        long lCpuIdleTime1 = getCpuIdleTime();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long lCpuTotalTime2 = getCpuTotalTime();
        long lCpuIdleTime2 = getCpuIdleTime();
        if (lCpuTotalTime2 - lCpuTotalTime1 == 0)
            return 0;

        double dCpuUsageTotal = ((double) ((lCpuTotalTime2 - lCpuTotalTime1) - (lCpuIdleTime2 - lCpuIdleTime1)))
                / (lCpuTotalTime2 - lCpuTotalTime1);
        return dCpuUsageTotal;
    }

    public static long getCpuTotalTime() {
        String strCpuStat = readFileFirstLine(VALUE_STRING_PROC_STAT_FILE);
        if (TextUtils.isEmpty(strCpuStat))
            return 0;

        String[] arraystr = strCpuStat.split("\\s+");
        if (null == arraystr || arraystr.length < 8)
            return 0;

        long lCPUTimeTotal = 0;
        for (int nIndex = 1; nIndex < 8; nIndex++)
            lCPUTimeTotal += Long.parseLong(arraystr[nIndex]);

        return lCPUTimeTotal;
    }

    public static long getCpuIdleTime() {
        String strCpuStat = readFileFirstLine(VALUE_STRING_PROC_STAT_FILE);
        if (TextUtils.isEmpty(strCpuStat))
            return 0;

        String[] arraystr = strCpuStat.split("\\s+");
        if (null == arraystr || arraystr.length < 8)
            return 0;

        return Long.parseLong(arraystr[4]);
    }
    public static String readFileFirstLine(String strFilePath) {
        if (TextUtils.isEmpty(strFilePath))
            return null;

        File file = new File(strFilePath);
        if (!file.exists())
            return null;

        try {
            String str = null;
            BufferedReader br = new BufferedReader(new FileReader(file));
            str = br.readLine();
            br.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
