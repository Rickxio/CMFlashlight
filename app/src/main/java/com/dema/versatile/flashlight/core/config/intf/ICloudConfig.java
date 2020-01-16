package com.dema.versatile.flashlight.core.config.intf;

import org.json.JSONObject;

import java.util.List;

import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.lib.core.in.ICMObserver;

public interface ICloudConfig extends ICMMgr, ICMObserver {

    List<String> getSceneList();

    ISceneConfig getSceneConfig(String scene);

    void parseConfig(JSONObject jsonObject);
}
