package com.dema.versatile.scene.core.scene;

import com.dema.versatile.lib.core.in.ICMJson;
import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.scene.SceneConstants;

/**
 * Created by wangyu on 2019/8/22.
 */
public interface ISceneMgr extends ICMMgr, ICMJson {

    void init(ISceneCallback callback);

    void invalidateAlarm();

    void trigger(@SceneConstants.Trigger String trigger);

    /**
     * 获取当前时间推荐的scene
     *
     * @return
     */
    String getRecommendScene();

    /**
     * 获取下一个对应trigger和scene的时间（至少是下一小时，返回整点时间）
     * @param scene
     * @param trigger
     * @return
     */
    Long getNextSceneTime(String scene, @SceneConstants.Trigger String trigger);

    int getAlertCount();

    INotificationConfig getNotificationConfig(String scene);

    IAlertConfig getAlertUiConfig(String scene);

    String getAdKey(String scene);

    ISceneCallback getCallBack();
}
