package com.dema.versatile.flashlight.core.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dema.versatile.flashlight.utils.DateUtil;
import com.dema.versatile.lib.CMLibFactory;

public class AlarmMgrImpl implements IAppAlarmMgr {

    private SharedPreferences mPreferences;

    @Override
    public void init() {
        Context mContext = CMLibFactory.getApplication();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void recordBatteryTime() {
        mPreferences.edit().putLong("battery_time", System.currentTimeMillis()).apply();
    }

    @Override
    public long getLastBatteryTime() {
        return mPreferences.getLong("battery_time", 0);
    }

    private long getWakeupTime() {
        long wakeupTime = DateUtil.getTimeInMillis(0, 0);
        wakeupTime = DateUtil.getTimeInMillisForToday(wakeupTime);
        return wakeupTime;
    }


    private long getSleepTime() {
        long sleepTime = DateUtil.getTimeInMillis(0, 0);
        sleepTime = DateUtil.getTimeInMillisForNextDay(sleepTime);
        return sleepTime;
    }
}
