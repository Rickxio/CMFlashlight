package com.dema.versatile.scene.core.scene;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;
import com.dema.versatile.lib.tool.AppStatusTool;
import com.dema.versatile.lib.utils.UtilsInstall;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.mediation.core.in.IMediationMgrListener;
import com.dema.versatile.scene.BuildConfig;
import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.alert.AlertMgrImpl;
import com.dema.versatile.scene.core.alert.IAlertMgr;
import com.dema.versatile.scene.core.config.ISceneItem;
import com.dema.versatile.scene.core.notification.INotificationMgr;
import com.dema.versatile.scene.core.store.ISceneDataStore;
import com.dema.versatile.scene.receiver.SceneReceiver;
import com.dema.versatile.scene.ui.AlertUiManager;
import com.dema.versatile.scene.ui.NotificationUiManager;
import com.dema.versatile.scene.utils.SceneLog;
import com.dema.versatile.scene.utils.UtilsAlarm;
import com.dema.versatile.scene.utils.UtilsCollection;
import com.dema.versatile.scene.utils.UtilsDate;
import com.dema.versatile.scene.utils.UtilsScene;
import com.dema.versatile.scene.utils.UtilsScreen;

/**
 * Created by wangyu on 2019/8/22.
 */
public class SceneMgrImpl implements ISceneMgr {
    private Context mContext;
    private ISceneCallback mCallback;
    private IMediationMgr mIMediationMgr;
    private ISceneDataStore mISceneDataStore;
    private INotificationMgr mINotificationMgr;
    private Map<Integer, List<ISceneItem>> mSceneMap = new HashMap<>();
    private Map<String, Boolean> mAdRequestStateMap = new HashMap<>();
    private Map<String, ISceneItem> mSceneItemMap = new HashMap<>();

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;
        List<ISceneItem> listSceneItem = new ArrayList<>();

        try {
            JSONObject defaultConfig = null;
            try {
                defaultConfig = jsonObject.getJSONObject("default");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray sceneList = null;
            try {
                sceneList = jsonObject.getJSONArray("scene_list");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (sceneList == null || sceneList.length() == 0) {
                ISceneItem instance = CMSceneFactory.getInstance().createInstance(ISceneItem.class);
                instance.Deserialization(null, defaultConfig);
                listSceneItem.add(instance);
            } else {
                for (int i = 0; i < sceneList.length(); i++) {
                    ISceneItem instance = CMSceneFactory.getInstance().createInstance(ISceneItem.class);
                    instance.Deserialization(sceneList.getJSONObject(i), defaultConfig);
                    listSceneItem.add(instance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 将后台配置的json解析为按照时间排列的map
         * 若配置为空也更新配置
         */

        mSceneMap = new HashMap<>();
        if (!UtilsCollection.isEmpty(listSceneItem)) {
            for (int i = 0; i < 24; i++) {
                List<ISceneItem> itemList = new ArrayList<>();
                for (ISceneItem item : listSceneItem) {
                    if (item != null && item.supportTime(i)) {
                        itemList.add(item);
                    }
                }

                mSceneMap.put(i, itemList);
            }
        }
    }

    private List<ISceneItem> getSceneItemList(int hour) {
        if (mSceneMap != null) {
            return mSceneMap.get(hour);
        }

        return null;
    }

    @Override
    public void init(ISceneCallback callback) {
        mCallback = callback;
        mContext = CMSceneFactory.getApplication();
        mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        mIMediationMgr.addListener(mIMediationMgrListener);
        mISceneDataStore = CMSceneFactory.getInstance().createInstance(ISceneDataStore.class);
        mINotificationMgr = CMSceneFactory.getInstance().createInstance(INotificationMgr.class);
        SceneReceiver.register(mContext);
        invalidateAlarm();

        ICMTimer timer = CMLibFactory.getInstance().createInstance(ICMTimer.class);
        timer.start(BuildConfig.DEBUG ? 10000 : 3000, 0, new ICMTimerListener() {
            @Override
            public void onComplete(long lRepeatTime) {
                trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_RUN);
            }
        });
    }

    private IMediationMgrListener mIMediationMgrListener = new IMediationMgrListener() {
        @Override
        public void onAdLoaded(IMediationConfig iMediationConfig) {
            ISceneItem sceneItem = mSceneItemMap.remove(iMediationConfig.getAdKey());
            if (sceneItem == null) {
                return;
            }
            SceneLog.logSuccess("ad loaded", sceneItem.getCurrentTrigger(),
                    sceneItem.getSceneList().get(sceneItem.getSceneIndex()), sceneItem.getKey());
            showAlert(sceneItem);
        }

        @Override
        public void onAdFailed(IMediationConfig iMediationConfig, int nCode) {
            ISceneItem sceneItem = mSceneItemMap.remove(iMediationConfig.getAdKey());
            if (sceneItem == null) {
                return;
            }
            SceneLog.logSuccess("ad failed:" + nCode, sceneItem.getCurrentTrigger(),
                    sceneItem.getSceneList().get(sceneItem.getSceneIndex()), sceneItem.getKey());
            showAlert(sceneItem);
        }
    };

    /**
     * 设置下个小时的闹钟
     */
    @Override
    public void invalidateAlarm() {
        long alarmTime = UtilsDate.getTimeMillisForToday(UtilsDate.getCurrentHourOfDay() + 1, 0, 0);
        Intent intent = new Intent(SceneReceiver.getAlarmAction(mContext));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, SceneConstants.VALUE_INT_SCENE_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        UtilsAlarm.setAlarm(mContext, alarmTime, pendingIntent);
        SceneLog.logSuccess("set alarm:" + UtilsDate.getTimeDetailStr(alarmTime), null, null, null);
    }


    /**
     * 所有的触发都会调用这里
     *
     * @param trigger
     */
    @Override
    public void trigger(@SceneConstants.Trigger String trigger) {
        if (mCallback == null) {
            return;
        }
        SceneLog.logSuccess("trigger", trigger, null, null);
        //清除上个小时的旧数据
        checkAndCleanData();
        //判断是否在前台或者锁屏
        if (AppStatusTool.getInstance().isForeground()) {
            SceneLog.logFail("app is foreground when trigger start", trigger, null, null);
            return;
        }
        //首先获取当前小时
        int currentHour = UtilsDate.getCurrentHourOfDay();
        //判断云配是否有配置
        List<ISceneItem> sceneItemList = getSceneItemList(currentHour);
        if (UtilsCollection.isEmpty(sceneItemList)) {
            SceneLog.logFail("no scene item at current hour", trigger, null, null);
            return;
        }

        for (ISceneItem item : sceneItemList) {
            if (checkSceneItem(item, currentHour, trigger)) {
                //可以触发当前item 要触发的scene也在item里有index
                int sceneIndex = item.getSceneIndex();
                String scene = item.getSceneList().get(sceneIndex);

                //判断如果是锁屏 那么只是弹出notification
                if (UtilsScreen.isLocked(mContext) || !UtilsScreen.isScreenOn(mContext)) {
                    int notificationHour = mISceneDataStore.getNotificationHour();
                    if (item.isNotificationEnable() && TextUtils.equals(SceneConstants.Trigger.VALUE_STRING_TRIGGER_ALARM, trigger)
                            && currentHour != notificationHour && UtilsScene.isPullScene(scene)) {
                        SceneLog.logSuccess("call show notification", trigger, scene, item.getKey());
                        if (mINotificationMgr.showNotification(scene)) {
                            mISceneDataStore.setNotificationHour(currentHour);
                        }
                    }
                    continue;
                }
                // 防止重入
                Boolean isRequesting = mAdRequestStateMap.get(scene);
                if (null != isRequesting && isRequesting)
                    continue;
                if (mIMediationMgr.isAdLoading(mCallback.getAlertViewAdKey(scene))) {
                    continue;
                }

                String viewAdKey = mCallback.getAlertViewAdKey(scene);
                SceneLog.logSuccess("alert check true,next step is request ad", trigger, scene, item.getKey());
                if (mIMediationMgr.requestAdAsync(viewAdKey, SceneConstants.VALUE_STRING_ALERT_AD_SCENE)) {
                    mAdRequestStateMap.put(scene, true);
                    SceneLog.logSuccess("request_ad", trigger, scene, item.getKey());
                    mSceneItemMap.put(viewAdKey, item);
                } else {
                    SceneLog.logSuccess("can not request ad, just show alert", trigger, scene, item.getKey());
                    //请求失败 ，直接弹出
                    showAlert(item);
                }
            }
        }
    }

    @Override
    public String getRecommendScene() {
        int hour = UtilsDate.getCurrentHourOfDay();
        List<ISceneItem> sceneItemList = getSceneItemList(hour);
        if (UtilsCollection.isEmpty(sceneItemList)) {
            return null;
        }
        for (ISceneItem item : sceneItemList) {
            if (item == null || UtilsCollection.isEmpty(item.getSceneList())) {
                continue;
            }
            if (!item.supportTrigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_ALARM)) {
                continue;
            }
            for (String scene : item.getSceneList()) {
                if (TextUtils.isEmpty(scene) || item.isInProtectTime(scene)) {
                    continue;
                }
                return scene;
            }
        }
        return null;
    }

    @Override
    public Long getNextSceneTime(String scene, @SceneConstants.Trigger String trigger) {
        if (TextUtils.isEmpty(scene) || TextUtils.isEmpty(trigger)) {
            return null;
        }
        int currentHour = UtilsDate.getCurrentHourOfDay();
        //首先从当前时间的下个小时开始遍历到23点
        for (int i = currentHour + 1; i < 24; i++) {
            List<ISceneItem> sceneItemList = getSceneItemList(i);
            if (UtilsCollection.isEmpty(sceneItemList)) {
                continue;
            }
            for (ISceneItem item : sceneItemList) {
                if (item == null) {
                    continue;
                }
                if (!item.isForce() && !checkWakeupSleepTime(i)) {
                    continue;
                }
                List<String> sceneList = item.getSceneList();
                List<String> triggerList = item.getTriggerList();
                if (sceneList != null && triggerList != null && sceneList.contains(scene) && triggerList.contains(trigger)) {
                    return UtilsDate.getTimeMillisForToday(i, 0, 0);
                }
            }
        }
        //执行到这里说明今天没有符合条件的了，开始从头遍历，时间是明天
        for (int i = 0; i < 24; i++) {
            List<ISceneItem> sceneItemList = getSceneItemList(i);
            if (UtilsCollection.isEmpty(sceneItemList)) {
                continue;
            }
            for (ISceneItem item : sceneItemList) {
                if (item == null) {
                    continue;
                }
                if (!item.isForce() && !checkWakeupSleepTime(i)) {
                    continue;
                }
                List<String> sceneList = item.getSceneList();
                List<String> triggerList = item.getTriggerList();
                if (sceneList != null && triggerList != null && sceneList.contains(scene) && triggerList.contains(trigger)) {
                    return UtilsDate.getTimeInMillisForNextDay(UtilsDate.getTimeMillisForToday(i, 0, 0));
                }
            }
        }
        return null;
    }

    /**
     * 检查是否超过起床睡眠时间  返回fase说明超过了
     *
     * @param hour
     * @return
     */
    private boolean checkWakeupSleepTime(int hour) {
        Long wakeupTime = mCallback.getWakeupTime();
        Long sleepTime = mCallback.getSleepTime();
        if (wakeupTime != null && sleepTime != null) {
            long formattedWakeupTime = UtilsDate.getTimeInMillisForToday(wakeupTime);
            long formattedSleepTime = UtilsDate.getTimeInMillisForToday(sleepTime);
            long targetTime = UtilsDate.getTimeMillisForToday(hour, 0, 0);
            //起床睡眠时间合法&&sceneItem非强制执行&&当前时间小于起床时间或者大于等于睡眠时间
            return formattedSleepTime <= formattedWakeupTime || (targetTime >= formattedWakeupTime && targetTime < formattedSleepTime);
        }
        return true;
    }

    @Override
    public int getAlertCount() {
        return mISceneDataStore.getAlertCount();
    }

    @Override
    public INotificationConfig getNotificationConfig(String scene) {
        if (mCallback != null && mCallback.getNotificationConfig(scene) != null) {
            return mCallback.getNotificationConfig(scene);
        }
        return NotificationUiManager.getInstance().getNotificationUiConfig(scene);
    }

    @Override
    public IAlertConfig getAlertUiConfig(String scene) {
        if (mCallback != null && mCallback.getAlertUiConfig(scene) != null) {
            return mCallback.getAlertUiConfig(scene);
        }
        return AlertUiManager.getInstance().getDefaultAlertUiConfig(scene);
    }

    @Override
    public String getAdKey(String scene) {
        if (mCallback != null) {
            return mCallback.getAlertViewAdKey(scene);
        }
        return null;
    }

    @Override
    public ISceneCallback getCallBack() {
        return mCallback;
    }

    private void checkAndCleanData() {
        int currentHour = UtilsDate.getCurrentHourOfDay();
        int lastTriggerHour = mISceneDataStore.getLastTriggerHour();
        if (currentHour == lastTriggerHour) {
            return;
        }
        mISceneDataStore.setTriggerHour(currentHour);
        mISceneDataStore.setAlertCount(0);
        //清除当前时间段的show alert time
        List<ISceneItem> currentItemList = getSceneItemList(currentHour);
        if (UtilsCollection.isEmpty(currentItemList)) {
            return;
        }
        for (ISceneItem item : currentItemList) {
            if (item != null) {
                mISceneDataStore.setSceneIndex(item.getKey(), -1);
                mISceneDataStore.resetShowAlertTime(item.getKey());
                //这里会引起bug，广告回调没结果的时候进入下一小时的触发，这里直接变成-1，等广告回来就不知道触发的是谁了
                //暂时注释掉，等待广告库添加tag功能
//                item.setSceneIndex(-1);
            }
        }
    }

    /**
     * 检查这个item是否有scene要被触发
     *
     * @param item
     * @param hour
     * @param trigger
     * @return
     */
    private boolean checkSceneItem(ISceneItem item, int hour, String trigger) {
        if (item == null) {
            return false;
        }
        //开始检查每个item是否应该被触发

        //判断是否在潜伏时间
        if (item.isInSleepTime()) {
            SceneLog.logFail("in sleep time,install:" + UtilsInstall.getInstalledTime(mContext) + ",config:" + item.getSleepTime()
                    , trigger, null, item.getKey());
            return false;
        }
        //判断是否强制执行和有没有起床睡眠时间
        Long wakeupTime = mCallback.getWakeupTime();
        Long sleepTime = mCallback.getSleepTime();
        if (!item.isForce() && wakeupTime != null && sleepTime != null) {
            long formattedWakeupTime = UtilsDate.getTimeInMillisForToday(wakeupTime);
            long formattedSleepTime = UtilsDate.getTimeInMillisForToday(sleepTime);
            long targetTime = UtilsDate.getTimeMillisForToday(hour, 0, 0);
            //起床睡眠时间合法&&sceneItem非强制执行&&当前时间小于起床时间或者大于等于睡眠时间
            if (formattedSleepTime > formattedWakeupTime && (targetTime < formattedWakeupTime || targetTime >= formattedSleepTime)) {
                SceneLog.logFail("out of wakeup or sleep time,cur:" + hour + ",wakeup:" + formattedWakeupTime + ",sleep:" + formattedSleepTime
                        , trigger, null, item.getKey());
                return false;
            }
        }

        //1.检查是否支持当前时间
        if (!item.supportTime(hour)) {
            SceneLog.logFail("no support time:" + hour, trigger, null, item.getKey());
            return false;
        }
        //2.检查是否支持当前触发方式
        if (!item.supportTrigger(trigger)) {
            SceneLog.logFail("no support trigger:" + trigger, trigger, null, item.getKey());
            return false;
        }
        //3.检查是否超过range time
        if (!item.isInRangeTime()) {
            SceneLog.logFail("out of range time, range" + item.getRangeTime(), trigger, null, item.getKey());
            return false;
        }
        //4.检查是否在mutex时间内
        long lastShowAlertTime = mISceneDataStore.getLastShowAlertTime(item.getKey());
        if (item.isInMutexTime()) {
            SceneLog.logFail("in mutex time,last alert time:" + UtilsDate.getTimeDetailStr(lastShowAlertTime)
                    + ",current time:" + UtilsDate.getTimeDetailStr(System.currentTimeMillis()) + ",mutex time:" + item.getMutexTime(), trigger, null, item.getKey());
            return false;
        }
        //5.挑选应该触发的scene，检查是否在scene的protect时间内
        List<String> sceneList = item.getSceneList();
        if (UtilsCollection.isEmpty(sceneList)) {
            SceneLog.logFail("scene list is empty", trigger, null, item.getKey());
            return false;
        }
        int sceneIndex = mISceneDataStore.getSceneIndex(item.getKey());
        if (sceneIndex + 1 >= sceneList.size()) {
            SceneLog.logFail("all scene has show", trigger, null, item.getKey());
            return false;
        }
        for (int i = sceneIndex + 1; i < sceneList.size(); i++) {
            String scene = sceneList.get(i);
            if (TextUtils.isEmpty(scene)) {
                continue;
            }
            if (!mCallback.isSupportScene(scene)) {
                SceneLog.logFail("app do not support this scene", trigger, scene, item.getKey());
                continue;
            }
            //检查scene的保护时间
            if (!item.isInProtectTime(scene)) {
                //利用item记录本次应该触发那个scene
                item.setSceneIndex(i);
                item.setCurrentTrigger(trigger);
                return true;
            }
        }
        return false;
    }

    private void showAlert(ISceneItem item) {
        if (mCallback == null || item == null) {
            return;
        }
        String scene = null;
        try {
            scene = item.getSceneList().get(item.getSceneIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(scene)) {
            SceneLog.logFail("get scene fail when show alert", item.getCurrentTrigger(), null, item.getKey());
            return;
        }
        assert scene != null;
        //将广告请求状态置为false
        mAdRequestStateMap.put(scene, false);
        //检查show_with_ad配置和广告缓存
        if (item.isShowWithAd() && !mIMediationMgr.isAdLoaded(mCallback.getAlertViewAdKey(scene))) {
            SceneLog.logFail("config is show with ad,but no cache", item.getCurrentTrigger(), null, item.getKey());
            return;
        }
        //判断应用是否在前台
        if (AppStatusTool.getInstance().isForeground()) {
            SceneLog.logFail("app is foreground when start alert", item.getCurrentTrigger(), scene, item.getKey());
            return;
        }
        //成功触发，更新数据
        mISceneDataStore.setSceneIndex(item.getKey(), item.getSceneIndex());
        mISceneDataStore.updateShowAlertTime(item.getKey());
        int currentHourOfDay = UtilsDate.getCurrentHourOfDay();
        mISceneDataStore.setNotificationHour(currentHourOfDay);
        int alertCount = mISceneDataStore.getAlertCount();
        alertCount++;
        mISceneDataStore.setAlertCount(alertCount);

        //开启alert
        @SceneConstants.Trigger String currentTrigger = item.getCurrentTrigger();
        SceneLog.logSuccess("call show alert:" + alertCount + ",index:" + item.getSceneIndex(), item.getCurrentTrigger(), scene, item.getKey());
        if (mCallback.beforeShowAlert(scene, currentTrigger, alertCount)) {
            //上层做了处理
            SceneLog.logSuccess("app has deal this alert", item.getCurrentTrigger(), scene, item.getKey());
            return;
        }
        mCallback.preLoadAd();
        IAlertMgr iAlertMgr = CMSceneFactory.getInstance().createInstance(IAlertMgr.class, AlertMgrImpl.class);
        iAlertMgr.showAlert(scene, item.getCurrentTrigger(), alertCount);
    }
}
