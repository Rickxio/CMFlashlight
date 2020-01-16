package com.dema.versatile.lib.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.dema.versatile.lib.alive.AliveAccountSyncService;
import com.dema.versatile.lib.alive.AliveAlarmReceiver;
import com.dema.versatile.lib.alive.AliveJobService;
import com.dema.versatile.lib.alive.AlivePixelActivity;
import com.dema.versatile.lib.alive.AliveWindow;
import com.dema.versatile.lib.alive.AliveWorker;
import com.dema.versatile.lib.core.in.ICMPull;

public class AliveHelp {
    private static BroadcastReceiver sBR = null;
    private static List<ICMPull> sListCMPull = new ArrayList<>();

    public static void init(Context context) {
        if (context == null) {
            return;
        }

        startListen(context);
        AliveAlarmReceiver.registerAliveAlarm(context);
        AliveJobService.jobSchedule(context);
        AliveWorker.startAliveWorker(context);
        AliveAccountSyncService.startAccountSync(context);
        AliveWindow.showPixelWindow(context);
    }

    private static void startListen(Context context) {
        if (context == null || sBR != null) {
            return;
        }

        sBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UtilsLog.aliveLog("dynamic", null);
                UtilsLog.send();
                AliveHelp.startPull("dynamic");

                if (intent == null) {
                    return;
                }
                if (TextUtils.equals(intent.getAction(),Intent.ACTION_SCREEN_ON)) {
                    UtilsLog.aliveLog("screen_on", null);
                    UtilsLog.send();

                    // 亮屏关闭1像素Activity
                    AlivePixelActivity.finishAliveActivity();
                } else if (TextUtils.equals(intent.getAction(),Intent.ACTION_SCREEN_OFF)) {
                    UtilsLog.aliveLog("screen_off", null);
                    UtilsLog.send();

                    // 灭屏打开1像素Activity
                    AlivePixelActivity.startAliveActivity(context);
                } else if (TextUtils.equals(intent.getAction(),Intent.ACTION_USER_PRESENT)) {
                    UtilsLog.aliveLog("user_present", null);
                    UtilsLog.send();

                    // 解锁关闭1像素Activity
                    AlivePixelActivity.finishAliveActivity();
                }else if (TextUtils.equals(intent.getAction(),Intent.ACTION_POWER_CONNECTED)) {
                    UtilsLog.aliveLog("power_connected", null);
                    UtilsLog.send();
                } else if (TextUtils.equals(intent.getAction(),Intent.ACTION_POWER_DISCONNECTED)) {
                    UtilsLog.aliveLog("power_disconnected", null);
                    UtilsLog.send();
                } else if (TextUtils.equals(intent.getAction(),ConnectivityManager.CONNECTIVITY_ACTION)) {
                    UtilsLog.aliveLog("connectivity_action", null);
                    UtilsLog.send();
                } else if (TextUtils.equals(intent.getAction(),Intent.ACTION_PACKAGE_ADDED)) {
                    UtilsLog.aliveLog("app_install", null);
                    UtilsLog.send();
                } else if (TextUtils.equals(intent.getAction(),Intent.ACTION_PACKAGE_REMOVED)) {
                    UtilsLog.aliveLog("app_uninstall", null);
                    UtilsLog.send();
                } else if (TextUtils.equals(intent.getAction(),Intent.ACTION_PACKAGE_REPLACED)) {
                    UtilsLog.aliveLog("app_update", null);
                    UtilsLog.send();
                }
            }
        };
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(sBR, intentFilter);

            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addDataScheme("package");
            context.registerReceiver(sBR, intentFilter);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    public static void startPull(String strScene) {
        if (null == sListCMPull) {
            return;
        }

        for (ICMPull iCMPull : sListCMPull) {
            if (iCMPull == null){
                continue;
            }
            iCMPull.pull(strScene);
        }
    }
}
