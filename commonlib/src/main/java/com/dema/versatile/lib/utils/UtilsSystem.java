package com.dema.versatile.lib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.dema.versatile.lib.utils.emulator.EasyProtectorLib;

public class UtilsSystem {
    private static String sProcessName = null;
//
    public static int getMyPID() {
        return android.os.Process.myPid();
    }

    public static String getMyProcessName(Context context) {
        if (null == context) {
            return null;
        }

        if (!TextUtils.isEmpty(sProcessName)){
            return sProcessName;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listRunningAppProcessesInfo = am.getRunningAppProcesses();
        if (null == listRunningAppProcessesInfo || listRunningAppProcessesInfo.isEmpty())
            return null;

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : listRunningAppProcessesInfo) {
            if (null == runningAppProcessInfo)
                continue;

            if (runningAppProcessInfo.pid == getMyPID()) {
                sProcessName = runningAppProcessInfo.processName;
            }
        }

        return sProcessName;
    }

    public static boolean isMainProcess(Context context) {
        if (null == context)
            return false;

        String strProcessName = getMyProcessName(context);
        String strPackageName = UtilsApp.getMyAppPackageName(context);
        if (TextUtils.isEmpty(strProcessName) || TextUtils.isEmpty(strPackageName))
            return false;

        return TextUtils.equals(strProcessName, strPackageName);
    }

    public static boolean isVpn() {
        try {
            Enumeration<NetworkInterface> enumerationNetworkInterface = NetworkInterface.getNetworkInterfaces();
            if (null == enumerationNetworkInterface)
                return false;

            for (NetworkInterface networkInterface : Collections.list(enumerationNetworkInterface)) {
                if (!networkInterface.isUp() || networkInterface.getInterfaceAddresses().size() == 0)
                    continue;

                if ("tun0".equals(networkInterface.getName()) || "ppp0".equals(networkInterface.getName()))
                    return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isRoot() {
        return EasyProtectorLib.checkIsRoot();
    }

    public static boolean isEmulator() {
        return EasyProtectorLib.checkIsRunningInEmulator();
    }
}
