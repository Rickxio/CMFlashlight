package com.dema.versatile.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

public class UtilsInstall {
    public static final int VALUE_INT_INSTALL_ERROR_TYPE = 0;
    public static final int VALUE_INT_INSTALL_NEW_TYPE = 1;
    public static final int VALUE_INT_INSTALL_UPDATE_TYPE = 2;
    public static final int VALUE_INT_INSTALL_RUN_TYPE = 3;

    private static final int VALUE_INT_DEFAULT_APP_VERSION = -1;

    private static int sInstallType = VALUE_INT_INSTALL_ERROR_TYPE;
    private static int sAppOldVersionCode = -1;

    public static void init(Context context) {
        if (null == context)
            return;

        int nAppNewVersionCode = UtilsApp.getMyAppVersionCode(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sAppOldVersionCode = sp.getInt("app_version", VALUE_INT_DEFAULT_APP_VERSION);
        sp.edit().putInt("app_version", nAppNewVersionCode).apply();
        if (VALUE_INT_DEFAULT_APP_VERSION == sAppOldVersionCode) {
            sInstallType = VALUE_INT_INSTALL_NEW_TYPE;
            sp.edit().putLong("install_time", System.currentTimeMillis()).apply();
            UtilsLog.aliveLog(UtilsLog.VALUE_STRING_LOG_KEY2_NEW, null);
        } else {
            if (nAppNewVersionCode > sAppOldVersionCode) {
                sInstallType = VALUE_INT_INSTALL_UPDATE_TYPE;
                JSONObject jsonObject = new JSONObject();
                UtilsJson.JsonSerialization(jsonObject, UtilsLog.VALUE_STRING_LOG_UPDATE_CONTENT_FROM_VERSION_CODE, sAppOldVersionCode);
                UtilsLog.aliveLog(UtilsLog.VALUE_STRING_LOG_KEY2_UPDATE, jsonObject);
            } else if (nAppNewVersionCode == sAppOldVersionCode) {
                sInstallType = VALUE_INT_INSTALL_RUN_TYPE;
                UtilsLog.aliveLog(UtilsLog.VALUE_STRING_LOG_KEY2_RUN, null);
            }
        }

        UtilsLog.send();
    }

    public static int getInstallType() {
        return sInstallType;
    }

    public static long getInstalledTime(Context context) {
        if (null == context)
            return 0;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lInstallTime = sp.getLong("install_time", 0);
        long lTime = System.currentTimeMillis();
        if (lInstallTime <= 0 || lTime < lInstallTime)
            return 0;

        return lTime - lInstallTime;
    }
}
