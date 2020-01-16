package com.dema.versatile.flashlight.core.config.intf;

import com.dema.versatile.lib.core.in.ICMJson;
import com.dema.versatile.lib.core.in.ICMObj;
import com.dema.versatile.lib.core.in.ICMObserver;

/**
 * Created by WangYu on 2018/8/17.
 */
public interface ISceneConfig extends ICMObj, ICMObserver, ICMJson {

    long getProtectTime();

    /**
     * 默认保护时间
     */
    long VALUE_LONG_DEFAULT_PROTECT_TIME = 30*60*1000;
}
