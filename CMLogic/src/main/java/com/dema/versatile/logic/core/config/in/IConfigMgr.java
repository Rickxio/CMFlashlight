package com.dema.versatile.logic.core.config.in;

import org.json.JSONObject;

import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.lib.core.in.ICMObserver;

public interface IConfigMgr extends ICMMgr, ICMObserver<IConfigMgrListener> {
    /**
     * 是否已经拉取配置
     *
     * @return
     */
    boolean isConfigLoaded();

    /**
     * 是否正在拉取配置
     *
     * @return
     */
    boolean isConfigLoading();

    /**
     * 获取配置信息
     *
     * @return
     */
    JSONObject getConfig();

    JSONObject getAdConfig();

    JSONObject getABTestConfig();

    /**
     * 异步拉取本地缓存配置
     *
     * @return
     */
    boolean loadConfigAsync();

    /**
     * 同步拉取本地缓存配置
     *
     * @return
     */
    boolean loadConfigSync();

    /**
     * 异步拉取网络配置
     *
     * @return
     */
    boolean requestConfigAsync(boolean bIsIgnoreProtectTime);

    /**
     * 异步拉取IP对应国家信息
     *
     * @return
     */
    boolean requestCountryAsync();
}
