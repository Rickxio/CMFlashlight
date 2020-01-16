package com.dema.versatile.flashlight.core.info;

import androidx.annotation.NonNull;

import com.dema.versatile.flashlight.main.function.Function;
import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.lib.core.in.ICMObserver;

/**
 * Create by XuChuanting
 * on 2018/8/16-16:13
 */
public interface IPhoneInfoMgr extends ICMMgr, ICMObserver<IPhoneInfoMgr.Listener> {

    /**
     * 获取cpu使用率
     *
     * @return 0~100
     */
    void getCpuUsageRate(@NonNull OnCPUsageRateCallback callback);


    /**
     * 运行内存使用率1~100
     *
     * @return
     */
    int getRamUsageRate();


    int getStorageUseRate();

    /**
     * 获取电池温度
     *
     * @return
     */
    int getBatteryTemperature();


    /**
     * 电池剩余电量比
     *
     * @return
     */
    double getBatteryRemainRate();


    /**
     * 获取优化的数值
     * @return
     */
    int getOptimizeValue(@Function.Type int type);


    void notifyBatteryChanged();

    void notifyCupRamUsageChanged();

    interface Listener {
        //电池发生变化
        default void onBatteryChange(){

        }

        //CpuRam使用率变化，杀进程后
        default void onCupRamUsageChanged(){

        }

        default void onStorageChanged(){

        }
    }

    interface OnCPUsageRateCallback {
        void callback(int rate);
    }


}
