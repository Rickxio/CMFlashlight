package com.dema.versatile.scene.core.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dema.versatile.scene.core.CMSceneFactory;

/**
 * Created by wangyu on 2019/7/30.
 */
public class SceneDataStore implements ISceneDataStore {
    private final SharedPreferences mPreferences;

    public SceneDataStore() {
        mPreferences = CMSceneFactory.getApplication().getSharedPreferences("scene_data", Context.MODE_PRIVATE);
    }

    @Override
    public int getSceneIndex(String sceneKey) {
        if (TextUtils.isEmpty(sceneKey)) {
            return -1;
        }
        return mPreferences.getInt(sceneKey + "scene_index", -1);
    }

    @Override
    public void setSceneIndex(String sceneKey, int index) {
        if (TextUtils.isEmpty(sceneKey)) {
            return;
        }
        mPreferences.edit().putInt(sceneKey + "scene_index", index).apply();
    }

    @Override
    public int getLastTriggerHour() {
        return mPreferences.getInt("trigger_hour", -1);
    }

    @Override
    public void setTriggerHour(int hour) {
        mPreferences.edit().putInt("trigger_hour", hour).apply();
    }

    @Override
    public long getLastSceneTime(String scene) {
        if (TextUtils.isEmpty(scene)) {
            return 0;
        }
        return mPreferences.getLong(scene, 0);
    }

    @Override
    public void setSceneTime(String scene, long targetTime) {
        if (TextUtils.isEmpty(scene)) {
            return;
        }
        mPreferences.edit().putLong(scene, targetTime).apply();
    }


    @Override
    public void updateShowAlertTime(String itemKey) {
        mPreferences.edit().putLong(itemKey, System.currentTimeMillis()).apply();
    }


    @Override
    public long getLastShowAlertTime(String itemKey) {
        return mPreferences.getLong(itemKey, 0);
    }

    @Override
    public void resetShowAlertTime(String itemKey) {
        mPreferences.edit().putLong(itemKey, 0).apply();
    }

    @Override
    public void setNotificationHour(int hour) {
        mPreferences.edit().putInt("notification_time", hour).apply();
    }

    @Override
    public int getNotificationHour() {
        return mPreferences.getInt("notification_time", -1);
    }

    @Override
    public void setAlertCount(int count) {
        mPreferences.edit().putInt("alert_count",count).apply();
    }

    @Override
    public int getAlertCount() {
        return mPreferences.getInt("alert_count", 0);
    }

}
