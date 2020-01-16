package com.dema.versatile.flashlight.core.protect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.config.intf.ICloudConfig;
import com.dema.versatile.flashlight.core.config.intf.ISceneConfig;
import com.dema.versatile.flashlight.main.function.Function;
import com.dema.versatile.lib.core.im.CMObserver;


/**
 * Create by XuChuanting
 * on 2018/8/17-10:47
 */
public class OptimizeProtectMgr extends CMObserver implements IProtectMgr {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;
    private final ICloudConfig mICloudConfig;

    public OptimizeProtectMgr() {
        mContext = CoreFactory.getApplication();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mICloudConfig = CoreFactory.getInstance().createInstance(ICloudConfig.class);
    }

    @Override
    public void updateOptimizeTime(int type) {
        long timeMillis = System.currentTimeMillis();
        mSharedPreferences
                .edit()
                .putLong(getKey(type), timeMillis)
                .apply();

    }

    @Override
    public boolean isUnderProtection(int type) {
        ISceneConfig sceneConfig = mICloudConfig.getSceneConfig(getKey(type));
        long protectTime;
        if (sceneConfig == null) {
            protectTime = ISceneConfig.VALUE_LONG_DEFAULT_PROTECT_TIME;
        } else {
            protectTime = sceneConfig.getProtectTime();
        }
        long lastOptimizeTime = getLastOptimizeTime(type);
        return System.currentTimeMillis() - lastOptimizeTime < protectTime;
    }


    @Override
    public long getLastOptimizeTime(int type) {
        return mSharedPreferences.getLong(getKey(type), 0);
    }

    @Override
    public List<Integer> getRecommendList() {
        List<Integer> recommendList = new ArrayList<>();
        List<String> sceneList = mICloudConfig.getSceneList();
        if (sceneList == null) {
            return recommendList;
        }
        for (String scene : sceneList) {
            int type = getType(scene);
            if (TextUtils.isEmpty(scene) || isUnderProtection(type)) {
                continue;
            }
            recommendList.add(type);
        }
        return recommendList;
    }

    private String getKey(int type) {
        String key = "";

        switch (type) {
            case Function.TYPE_CLEAN:
                key = Function.TEXT_CLEAN;
                break;
            case Function.TYPE_BOOST:
                key = Function.TEXT_BOOST;
                break;
            case Function.TYPE_COOL:
                key = Function.TEXT_COOL;
                break;
            case Function.TYPE_SAVE_BATTERY:
                key = Function.TEXT_BATTERY;
                break;
            case Function.TYPE_NETWORK:
            default:
                key = Function.TEXT_NETWORK;
        }
        return key;
    }

    private int getType(String scene) {
        int type = Function.TYPE_BOOST;
        switch (scene) {
            case Function.TEXT_BATTERY:
                type = Function.TYPE_SAVE_BATTERY;
                break;
            case Function.TEXT_CLEAN:
                type = Function.TYPE_CLEAN;
                break;
            case Function.TEXT_BOOST:
                type = Function.TYPE_BOOST;
                break;
            case Function.TEXT_COOL:
                type = Function.TYPE_COOL;
                break;
            case Function.TEXT_NETWORK:
                type = Function.TYPE_NETWORK;
                break;
        }
        return type;
    }
}
