package com.dema.versatile.flashlight.core.alarm;

import com.dema.versatile.lib.core.in.ICMMgr;

/**
 * Created by wangyu on 2019/7/8.
 */
public interface IAppAlarmMgr extends ICMMgr {

    void init();

    void recordBatteryTime();

    long getLastBatteryTime();
}
