package com.dema.versatile.scene.core.config;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dema.versatile.lib.utils.UtilsInstall;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsTime;
import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.store.ISceneDataStore;
import com.dema.versatile.scene.utils.UtilsCollection;
import com.dema.versatile.scene.utils.UtilsDate;
import com.dema.versatile.scene.utils.UtilsParse;

/**
 * Created by wangyu on 2019/8/23.
 */
public class SceneItemImpl implements ISceneItem {
    //scene名称
    private String mKey = "default";
    //是否按照配置的时间强制执行
    private boolean mForce = true;
    //触发的scene列表
    private List<String> mSceneList;
    //要触发的时间列表 单位 小时
    private List<Integer> mTimeList;
    //触发条件列表
    private List<@SceneConstants.Trigger String> mTriggerList;
    //after时间 单位：分钟
    private int mRangeTime;
    //sceneList里两个scene之间的保护时间 （只对本SceneItem有用） 单位：分钟
    private int mMutexTime = 10;
    //每个scene之间的保护时间，对所有scene都生效，但是触发的时候各自读取自己的 单位：分钟
    private Map<String, Integer> mProtectTimeMap;
    //是否允许弹notification
    private boolean mNotificationEnable;
    //潜伏时间
    private int mSleepTime = 60;

    private boolean mShowWithAd = true;
    //记录当前item触发到了哪个index，注意这个变量只做临时记录传值用，实际的index是存在dataStore的sp里的
    private int mSceneIndex;
    //记录当前item是通过什么trigger触发的
    private @SceneConstants.Trigger String mTrigger;

    @Override
    public void Deserialization(JSONObject jsonObject, JSONObject defaultConfig) {
        //传过来的两个JsonObject 一个是列表里的配置，另一个是默认配置
        //解析的时候先解析默认配置，给各个变量赋值，然后解析列表的单独配置，进行覆盖
        parse(defaultConfig);
        parse(jsonObject);
    }

    @Override
    public String getKey() {
        if (TextUtils.isEmpty(mKey)) {
            return "unkonwn";
        }
        return mKey;
    }

    @Override
    public boolean isForce() {
        return mForce;
    }

    @Override
    public List<String> getSceneList() {
        return mSceneList;
    }

    @Override
    public List<Integer> getTimeList() {
        return mTimeList;
    }

    @Override
    public List<String> getTriggerList() {
        return mTriggerList;
    }

    @Override
    public long getRangeTime() {
        return mRangeTime * UtilsTime.VALUE_LONG_TIME_ONE_MINUTE;
    }

    @Override
    public long getMutexTime() {
        return mMutexTime * UtilsTime.VALUE_LONG_TIME_ONE_MINUTE;
    }

    @Override
    public long getProtectTime(String scene) {
        Integer integer = mProtectTimeMap.get(scene);
        if (integer == null || integer <= 0) {
            return VALUE_LONG_DEFAULT_PROTECT_TIME;
        } else {
            return integer * UtilsTime.VALUE_LONG_TIME_ONE_MINUTE;
        }
    }

    @Override
    public boolean isInRangeTime() {
        long startTime = UtilsDate.getTimeInMillis(UtilsDate.getCurrentHourOfDay(), 0);
        long currentTime = System.currentTimeMillis();
        long rangeTime = getRangeTime();
        return currentTime - startTime <= rangeTime;
    }

    @Override
    public boolean isInMutexTime() {
        ISceneDataStore iSceneDataStore = CMSceneFactory.getInstance().createInstance(ISceneDataStore.class);
        long lastShowAlertTime = iSceneDataStore.getLastShowAlertTime(getKey());
        return System.currentTimeMillis() - lastShowAlertTime < getMutexTime();
    }

    @Override
    public boolean isInProtectTime(String scene) {
        ISceneDataStore iSceneDataStore = CMSceneFactory.getInstance().createInstance(ISceneDataStore.class);
        long lastSceneTime = iSceneDataStore.getLastSceneTime(scene);
        return System.currentTimeMillis() - lastSceneTime <= getProtectTime(scene);
    }

    @Override
    public boolean isInSleepTime() {
        long installedTime = UtilsInstall.getInstalledTime(CMSceneFactory.getApplication());
        return installedTime <= mSleepTime * UtilsTime.VALUE_LONG_TIME_ONE_MINUTE;
    }

    @Override
    public long getSleepTime() {
        return mSleepTime * UtilsTime.VALUE_LONG_TIME_ONE_MINUTE;
    }

    @Override
    public boolean isShowWithAd() {
        return mShowWithAd;
    }

    @Override
    public boolean supportTime(int hour) {
        if (!UtilsCollection.isEmpty(mTimeList)) {
            return mTimeList.contains(hour);
        }
        return false;
    }

    @Override
    public boolean supportTrigger(@SceneConstants.Trigger String trigger) {
        if (!UtilsCollection.isEmpty(mTriggerList) && !TextUtils.isEmpty(trigger)) {
            return mTriggerList.contains(trigger);
        }
        return false;
    }

    @Override
    public void setSceneIndex(int index) {
        mSceneIndex = index;
    }

    @Override
    public int getSceneIndex() {
        return mSceneIndex;
    }

    @Override
    public boolean isNotificationEnable() {
        return mNotificationEnable;
    }

    @Override
    public void setCurrentTrigger(@SceneConstants.Trigger String trigger) {
        mTrigger = trigger;
    }

    @Override
    public @SceneConstants.Trigger String getCurrentTrigger() {
        return mTrigger;
    }

    private void parse(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        if (jsonObject.has("key")) {
            mKey = UtilsJson.JsonUnserialization(jsonObject, "key", "");
        }
        if (jsonObject.has("force")) {
            mForce = UtilsJson.JsonUnserialization(jsonObject, "force", mForce);
        }
        if (jsonObject.has("sleep_time")) {
            mSleepTime = UtilsJson.JsonUnserialization(jsonObject, "sleep_time", mSleepTime);
        }
        if (jsonObject.has("show_with_ad")) {
            mShowWithAd = UtilsJson.JsonUnserialization(jsonObject, "show_with_ad", mShowWithAd);
        }
        if (jsonObject.has("alarm_notification")) {
            mNotificationEnable = UtilsJson.JsonUnserialization(jsonObject, "alarm_notification", mNotificationEnable);
        }
        if (jsonObject.has("scene")) {
            mSceneList = new ArrayList<>();
            UtilsJson.JsonUnserialization(jsonObject, "scene", mSceneList, String.class, String.class, String.class);
        }
        if (jsonObject.has("time")) {
            mTimeList = new ArrayList<>();
            UtilsJson.JsonUnserialization(jsonObject, "time", mTimeList, Integer.class, Integer.class, Integer.class);
        }
        if (jsonObject.has("trigger")) {
            mTriggerList = new ArrayList<>();
            UtilsJson.JsonUnserialization(jsonObject, "trigger", mTriggerList, String.class, String.class, String.class);
        }
        if (jsonObject.has("range_time")) {
            mRangeTime = UtilsJson.JsonUnserialization(jsonObject, "range_time", mRangeTime);
        }
        if (jsonObject.has("mutex_time")) {
            mMutexTime = UtilsJson.JsonUnserialization(jsonObject, "mutex_time", mMutexTime);
        }
        if (mProtectTimeMap == null) {
            mProtectTimeMap = new HashMap<>();
        }
        UtilsParse.JsonUnserialization(jsonObject, "protect_time", mProtectTimeMap, String.class, Integer.class, Integer.class, Integer.class);
    }

    @Override
    public String toString() {
        return "SceneItemImpl{" +
                "mKey='" + mKey + '\'' +
                ", mForce=" + mForce +
                ", mSceneList=" + mSceneList +
                ", mTimeList=" + mTimeList +
                ", mTriggerList=" + mTriggerList +
                ", mRangeTime=" + mRangeTime +
                ", mMutexTime=" + mMutexTime +
                ", mProtectTimeMap=" + mProtectTimeMap +
                ", mSceneIndex=" + mSceneIndex +
                '}';
    }
}
