package com.dema.versatile.lib.alive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dema.versatile.lib.utils.UtilsLog;

public class AliveStaticReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UtilsLog.aliveLog("static", null);
        UtilsLog.send();
    }
}
