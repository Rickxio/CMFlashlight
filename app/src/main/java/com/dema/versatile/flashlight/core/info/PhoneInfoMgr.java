package com.dema.versatile.flashlight.core.info;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.Random;

import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.protect.IProtectMgr;
import com.dema.versatile.flashlight.main.function.Function;
import com.dema.versatile.flashlight.utils.CleanUtil;
import com.dema.versatile.flashlight.utils.SystemUtil;
import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.im.CMObserver;
import com.dema.versatile.lib.core.in.ICMThreadPool;
import com.dema.versatile.lib.core.in.ICMThreadPoolListener;

/**
 * 手机信息管理类
 * Create by XuChuanting
 * on 2018/8/16-16:18
 */
public class PhoneInfoMgr extends CMObserver<IPhoneInfoMgr.Listener> implements IPhoneInfoMgr {
    public static final String KEY_CPU_USAGE_VALUE = "KEY_CPU_USAGE_VALUE";
    public static final String KEY_BATTERY_TEMPATURE_VALUE = "KEY_BATTERY_TEMPATURE_VALUE";
    public static final String KEY_STORAGE_VALUE = "KEY_STORAGE_VALUE";
    public static final String KEY_RAM_VALUE = "KEY_RAM_VALUE";

    private static final String TAG = "PhoneInfoMgr";

    private final Context mContext;
    private final ICMThreadPool mICMThreadPool;
    private final int MIN_CPU_VALUE = 2;
    private final int MIN_RAM_VALUE = 1;
    private final int MIN_STORAGE_VALUE = 5;
    private final int MIN_BATTERY_VALUE = 5;
    private final IProtectMgr mProtectMgr;
    private final SharedPreferences mSharedPreferences;
    private double mCpuTotalUsage;
    private int lastCPUUsageValue = -1;
    private int lastRAMUsageValue = -1;
    private int lastStorageUseValue = -1;
    private int lastBatteryTempValue = -1;
    private int mOptimizeValue;

    public PhoneInfoMgr() {
        mContext = CoreFactory.getApplication();
        mICMThreadPool = CMLibFactory.getInstance().createInstance(ICMThreadPool.class);
        mProtectMgr = CoreFactory.getInstance().createInstance(IProtectMgr.class);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

    }


    @Override
    public void getCpuUsageRate(@NonNull final OnCPUsageRateCallback callback) {
        if (mProtectMgr.isUnderProtection(Function.TYPE_BOOST)) {
            callback.callback(mSharedPreferences.getInt(KEY_CPU_USAGE_VALUE, 10));
        } else {
            mICMThreadPool.run(new ICMThreadPoolListener() {
                @Override
                public void onRun() {
                    mCpuTotalUsage = SystemUtil.getCpuTotalUsage();
                }

                @Override
                public void onComplete() {
                    int value = getIntValue(mCpuTotalUsage);
                    mSharedPreferences.edit().putInt(KEY_CPU_USAGE_VALUE, value).apply();
                    callback.callback(value);
                }

                @Override
                public void onMessage(Message msg) {

                }
            });
        }

    }

    @Override
    public int getRamUsageRate() {
        if (mProtectMgr.isUnderProtection(Function.TYPE_BOOST)) {
            return mSharedPreferences.getInt(KEY_RAM_VALUE, 20);
        } else {
            double memoryUseRate = CleanUtil.getMemoryUseRate(mContext);
            int intValue = getIntValue(memoryUseRate);
            mSharedPreferences.edit().putInt(KEY_RAM_VALUE, intValue).apply();
            return intValue;
        }
    }

    private int getIntValue(double rate) {
        return BigDecimal.valueOf(rate).multiply(BigDecimal.valueOf(100d)).intValue();
    }

    @Override
    public int getStorageUseRate() {
        if (mProtectMgr.isUnderProtection(Function.TYPE_CLEAN)) {
            return mSharedPreferences.getInt(KEY_STORAGE_VALUE, 35);
        } else {
            int value = 0;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(sdcardDir.getPath());
                long blockCount = sf.getBlockCountLong();
                if (blockCount <= 0) {
                    return 0;
                }
                long availCount = sf.getAvailableBlocksLong();
                double v = (double) (blockCount - availCount) / blockCount;
                value = getIntValue(v);
            }
            if (value < MIN_STORAGE_VALUE) {
                value = MIN_STORAGE_VALUE;
            }
            mSharedPreferences.edit().putInt(KEY_STORAGE_VALUE, value).apply();
            return value;
        }
    }

    @Override
    public int getBatteryTemperature() {
        if (mProtectMgr.isUnderProtection(Function.TYPE_COOL)) {
            return mSharedPreferences.getInt(KEY_BATTERY_TEMPATURE_VALUE, 15);
        } else {
            int tem = SystemUtil.getBatteryTemperature(mContext) / 10;
            mSharedPreferences.edit().putInt(KEY_BATTERY_TEMPATURE_VALUE, tem).apply();
            return tem;
        }

    }

    @Override
    public double getBatteryRemainRate() {
        int batteryLevel = SystemUtil.getBatteryLevel(mContext);
        int batteryScale = SystemUtil.getBatteryScale(mContext);
        return (double) batteryLevel / batteryScale;
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public int getOptimizeValue(int type) {

        mOptimizeValue = 0;
        final Random random = new Random();

        if (type == Function.TYPE_CLEAN) {
            mOptimizeValue = random.nextInt(50) + 150;
//            int value = mSharedPreferences.getInt(KEY_STORAGE_VALUE, MIN_STORAGE_VALUE);
//            if (value < MIN_STORAGE_VALUE) {
//                value = MIN_STORAGE_VALUE;
//            }
//            mSharedPreferences.edit()
//                    .putInt(KEY_STORAGE_VALUE, value - mOptimizeValue)
//                    .commit();
//                PhoneInfoMgr.this.notify((Dispatcher<Listener>) Listener::onStorageChanged);
//            notifyListener(Listener::onStorageChanged);
        }

        if (type == Function.TYPE_BOOST) {
            mOptimizeValue = random.nextInt(3) + 5;
            int cpuValue = mSharedPreferences.getInt(KEY_CPU_USAGE_VALUE, MIN_CPU_VALUE);
            int ramValue = mSharedPreferences.getInt(KEY_RAM_VALUE, MIN_RAM_VALUE);
            if (cpuValue < mOptimizeValue) {
                mOptimizeValue = 1;
            }
            if (ramValue < mOptimizeValue) {
                mOptimizeValue = 1;
            }
            cpuValue = cpuValue - mOptimizeValue > MIN_CPU_VALUE ? cpuValue - mOptimizeValue : MIN_CPU_VALUE;
            ramValue = ramValue - mOptimizeValue > MIN_RAM_VALUE ? ramValue - mOptimizeValue : MIN_RAM_VALUE;

            mSharedPreferences.edit()
                    .putInt(KEY_CPU_USAGE_VALUE, cpuValue)
                    .putInt(KEY_RAM_VALUE, ramValue)
                    .commit();
            notifyCupRamUsageChanged();
        }

        if (type == Function.TYPE_COOL) {
            mOptimizeValue = random.nextInt(5) + 5;
            int value = mSharedPreferences.getInt(KEY_BATTERY_TEMPATURE_VALUE, MIN_BATTERY_VALUE);
            if (value < mOptimizeValue) {
                mOptimizeValue = 1;
            }
            if (value > 35) {//降温到临界值以下
                int oldValue = value;
                value = 35 - random.nextInt(5) - 2;
                mOptimizeValue = oldValue - value;
            } else {
                value = value - mOptimizeValue > MIN_BATTERY_VALUE ? value - mOptimizeValue : MIN_BATTERY_VALUE;
            }
            mSharedPreferences.edit()
                    .putInt(KEY_BATTERY_TEMPATURE_VALUE, value)
                    .commit();
            notifyBatteryChanged();
        }

        if (type == Function.TYPE_SAVE_BATTERY) {
            mOptimizeValue = random.nextInt(5) + 2;
        }

        if (type == Function.TYPE_NETWORK) {
            mOptimizeValue = 20 + random.nextInt(20);
        }

        return mOptimizeValue;
    }


    @Override
    public void notifyBatteryChanged() {
        notifyListener(Listener::onBatteryChange);
    }

    @Override
    public void notifyCupRamUsageChanged() {
        notifyListener(Listener::onCupRamUsageChanged);
    }


}
