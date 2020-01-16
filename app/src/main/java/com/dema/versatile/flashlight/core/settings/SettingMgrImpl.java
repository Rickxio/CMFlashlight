package com.dema.versatile.flashlight.core.settings;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dema.versatile.flashlight.MainApplication;

/**
 * Create on 2019/10/11 13:56
 *
 * @author XuChuanting
 */
public class SettingMgrImpl implements ISettingMgr {


    private static final String KEY_SCENE_STATE = "scene_state";
    private static final String KEY_CLOSE_COUNT = "close_count";
    private final SharedPreferences mPreferences;

    public SettingMgrImpl() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.getApplication());
    }

    @Override
    public boolean isSceneOpen() {
        return mPreferences.getBoolean(KEY_SCENE_STATE, true);
    }

    @Override
    public void setSceneState(boolean state) {

        mPreferences.edit()
                .putBoolean(KEY_SCENE_STATE, state)
                .apply();
    }

    @Override
    public int getCloseCount() {
        return mPreferences.getInt(KEY_CLOSE_COUNT, 0);
    }

    @Override
    public int addCloseCount() {
        int count = getCloseCount() + 1;
        mPreferences.edit()
                .putInt(KEY_CLOSE_COUNT, count)
                .apply();
        return count;
    }
}
