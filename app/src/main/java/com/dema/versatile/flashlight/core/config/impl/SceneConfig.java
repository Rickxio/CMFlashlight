package com.dema.versatile.flashlight.core.config.impl;


import org.json.JSONObject;

import com.dema.versatile.flashlight.core.config.intf.ISceneConfig;
import com.dema.versatile.lib.core.im.CMObserver;
import com.dema.versatile.lib.utils.UtilsJson;

/**
 * Created by WangYu on 2018/8/17.
 */
public class SceneConfig extends CMObserver implements ISceneConfig {

    private long mProtectTime = VALUE_LONG_DEFAULT_PROTECT_TIME;

    @Override
    public long getProtectTime() {
        return mProtectTime;
    }

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        mProtectTime = UtilsJson.JsonUnserialization(jsonObject,"protect_time",mProtectTime);
        mProtectTime = mProtectTime * 60 * 1000;
    }
}
