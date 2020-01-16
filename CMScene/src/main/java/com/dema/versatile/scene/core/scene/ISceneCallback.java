package com.dema.versatile.scene.core.scene;

import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.core.alert.AlertInfoBean;

/**
 * Created by wangyu on 2019/8/23.
 */
public abstract class ISceneCallback {
    /**
     * 获取对应scene弹出的alert里面的view广告
     *
     * @param scene
     * @return
     */
    public abstract String getAlertViewAdKey(String scene);

    /**
     * 起床时间  如果没有就返回null
     *
     * @return
     */
    public Long getWakeupTime() {
        return null;
    }

    /**
     * 睡眠时间  如果没有就返回null
     *
     * @return
     */
    public Long getSleepTime() {
        return null;
    }


    /**
     * 预加载页面广告
     */
    public void preLoadAd() {
    }

    /**
     * 是否支持这个场景  如果返回false，将不会触发该场景
     *
     * @param scene
     * @return
     */
    public boolean isSupportScene(String scene) {
        return true;
    }

    /**
     * 获取通知栏ui配置
     *
     * @param scene
     * @return
     */
    public INotificationConfig getNotificationConfig(String scene) {
        return null;
    }

    /**
     * 获取alert ui配置
     *
     * @param scene
     * @return
     */
    public IAlertConfig getAlertUiConfig(String scene) {
        return null;
    }

    /**
     * 如果返回true，那么lib将不会开启任何alert
     * 如果上层不处理，请返回false
     *
     * @param scene
     * @param trigger
     * @param count
     * @return
     */
    public boolean beforeShowAlert(String scene, @SceneConstants.Trigger String trigger, int count) {
        return false;
    }

    public void onAlertShow(AlertInfoBean alertInfoBean) {

    }

    public void onAlertClick(AlertInfoBean alertInfoBean) {

    }

}
