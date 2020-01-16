package com.dema.versatile.scene.core.store;

import com.dema.versatile.lib.core.in.ICMMgr;

/**
 * 存储持久化数据
 */
public interface ISceneDataStore extends ICMMgr {
    String KEY_DRINK_COUNT = "drink_count";
    String KEY_ALARM_TRIGGER = "alarm_trigger_";
    String KEY_SHOW_ALERT_TIME = "last_show_alert_time_";

    /**
     * 获取当前scene item触发到了哪个index
     *
     * @param sceneKey
     * @return
     */
    int getSceneIndex(String sceneKey);

    void setSceneIndex(String sceneKey, int index);

    int getLastTriggerHour();

    void setTriggerHour(int hour);

    long getLastSceneTime(String scene);

    void setSceneTime(String scene, long time);

    void updateShowAlertTime(String itemKey);

    long getLastShowAlertTime(String itemKey);

    void resetShowAlertTime(String itemKey);

    void setNotificationHour(int hour);

    int getNotificationHour();

    void setAlertCount(int count);

    int getAlertCount();
}
