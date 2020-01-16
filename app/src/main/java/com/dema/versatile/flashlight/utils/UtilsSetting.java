package com.dema.versatile.flashlight.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UtilsSetting {
    public static boolean isLightOnStartup(Context context) {
        if (null == context)
            return false;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("is_light_on_startup", false);
    }

    public static void setLightOnStartup(Context context, boolean bIsOn) {
        if (null == context)
            return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("is_light_on_startup", bIsOn).apply();
    }
}
