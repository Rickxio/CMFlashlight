package com.dema.versatile.scene.core.config;

import org.json.JSONObject;

import java.util.List;

import com.dema.versatile.lib.core.in.ICMObj;
import com.dema.versatile.lib.utils.UtilsTime;
import com.dema.versatile.scene.SceneConstants;

/**
 * Created by wangyu on 2019/8/23.
 */
public interface ISceneItem extends ICMObj {

    long VALUE_LONG_DEFAULT_PROTECT_TIME = UtilsTime.VALUE_LONG_TIME_ONE_HOUR;

    void Deserialization(JSONObject jsonObject, JSONObject defaultConfig);

    String getKey();

    boolean isForce();

    List<String> getSceneList();

    List<Integer> getTimeList();

    List<String> getTriggerList();

    long getRangeTime();

    long getMutexTime();

    long getProtectTime(String scene);

    boolean isInRangeTime();

    boolean isInMutexTime();

    boolean isInProtectTime(String scene);

    boolean isInSleepTime();

    long getSleepTime();

    boolean isShowWithAd();

    /**
     * 是否包含传进来的小时时间
     *
     * @param hour
     * @return
     */
    boolean supportTime(int hour);

    boolean supportTrigger(@SceneConstants.Trigger String trigger);

    void setSceneIndex(int index);

    int getSceneIndex();

    boolean isNotificationEnable();

    void setCurrentTrigger(@SceneConstants.Trigger String trigger);

    @SceneConstants.Trigger String getCurrentTrigger();
}
