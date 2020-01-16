package com.dema.versatile.lib.alive;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.dema.versatile.lib.utils.AliveHelp;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.utils.UtilsTime;

public class AliveAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UtilsLog.aliveLog("alarm", null);
        UtilsLog.send();
        AliveHelp.startPull("alarm");

        updateAliveAlarm(context);
    }

    public static void registerAliveAlarm(Context context) {
        try {
            if (null == context)
                return;

            Intent intentAlive = new Intent(context, AliveAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intentAlive, PendingIntent.FLAG_NO_CREATE);
            if (null != pi)
                return;

            updateAliveAlarm(context);
        } catch (Exception|Error e) {

        }
    }

    private static void updateAliveAlarm(Context context) {
        if (null == context)
            return;

        Intent intentAlive = new Intent(context, AliveAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intentAlive, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + UtilsTime.VALUE_LONG_TIME_ONE_MINUTE * 5, pi);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + UtilsTime.VALUE_LONG_TIME_ONE_MINUTE * 5, pi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
        }
    }
}
