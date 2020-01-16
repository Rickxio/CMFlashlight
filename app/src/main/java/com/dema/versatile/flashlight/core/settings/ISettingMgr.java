package com.dema.versatile.flashlight.core.settings;

import com.dema.versatile.lib.core.in.ICMMgr;

/**
 * Create on 2019/10/11 13:52
 *
 * @author XuChuanting
 */
public interface ISettingMgr extends ICMMgr {

    boolean isSceneOpen();


    /**
     * 设置场景开启状态
     * @param state
     */
    void setSceneState(boolean state);



    int getCloseCount();


    /**
     * 增加一次关闭次数
     * @return 增加后的关闭次数
     */
    int addCloseCount();
}
