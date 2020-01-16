package com.dema.versatile.flashlight.core.protect;


import java.util.List;

import com.dema.versatile.flashlight.main.function.Function;
import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.lib.core.in.ICMObserver;

/**
 * Create by XuChuanting
 * on 2018/8/17-10:31
 * 优化保护时间管理
 */
public interface IProtectMgr extends ICMMgr, ICMObserver {

    /**
     * 更新优化时间
     *
     * @param type 优化类型
     */
    void updateOptimizeTime(@Function.Type int type);


    /**
     * @param type 优化类型
     * @return 是否在保护时间内
     */
    boolean isUnderProtection(@Function.Type int type);

    /**
     * 获取上次优化时间
     *
     * @param type 优化类型
     * @return {@link #updateOptimizeTime(int)}
     */
    long getLastOptimizeTime(@Function.Type int type);

    /**
     * 返回推荐的场景列表
     *
     * @return List<Integer></> @Function.Type
     */
    List<Integer> getRecommendList();
}
