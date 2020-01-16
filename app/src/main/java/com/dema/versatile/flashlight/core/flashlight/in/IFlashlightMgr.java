package com.dema.versatile.flashlight.core.flashlight.in;

import java.util.List;

public interface IFlashlightMgr {


    void register();

    void unregister();
    /**
     * 初始化数据
     * @return
     */
    List<IStrobeItem> initRvList();

    /**
     * 点击主开关
     */
    boolean clickSwitch();

    /**
     * 获取闪光灯状态
     * @return
     */
    boolean getLightState();

    void setLightState(boolean b);

    /**
     * 滑动recyclerview，返回中间的item的下表
     * @param position
     */
    void changeRVState(int position);

    int getSelectState();

    /**
     * 开灯
     */
    void openLight();

    /**
     * 关灯
     */
    void closeLight();

    void destroy();

    /**
     * 获取状态名称集合
     */
    String[] getItemString();

    /**
     * 获取开关状态
     * @return
     */
    boolean getSwitchState();

    /**
     * 设置开关状态
     * @param state
     */
    void setSwitchState(boolean state);

    void cleanTimeTask();

    boolean isOpenSos();

    void setLevel(int level);

}
