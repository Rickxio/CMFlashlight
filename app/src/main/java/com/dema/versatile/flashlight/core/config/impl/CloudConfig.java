package com.dema.versatile.flashlight.core.config.impl;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.config.intf.ICloudConfig;
import com.dema.versatile.flashlight.core.config.intf.ISceneConfig;
import com.dema.versatile.lib.core.im.CMObserver;

public class CloudConfig extends CMObserver implements ICloudConfig {
    private Map<String, ISceneConfig> mSceneMap;
    private List<String> mListScene;

    public CloudConfig() {
        mSceneMap = new HashMap<>();
        mListScene = new ArrayList<>();
        mListScene.add("boost");
        mListScene.add("clean");
        mListScene.add("cool");
        mListScene.add("battery");
        mListScene.add("network");
    }

    @Override
    public List<String> getSceneList() {
        return mListScene;
    }

    @Override
    public ISceneConfig getSceneConfig(String scene) {
        return mSceneMap.get(scene);
    }

    @Override
    public void parseConfig(JSONObject jsonObject) {
        if (jsonObject.has("config")) {
            try {
                JSONObject config = jsonObject.getJSONObject("config");
                Iterator<String> iterator = config.keys();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    JSONObject sceneConfig = config.getJSONObject(key);
                    ISceneConfig iSceneConfig = CoreFactory.getInstance().createInstance(ISceneConfig.class);
                    iSceneConfig.Deserialization(sceneConfig);
                    mSceneMap.put(key,iSceneConfig);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
       /* if (jsonObject.has("scene_list")) {
            mListScene.clear();
            UtilsJson.JsonUnserialization(jsonObject, "scene_list", mListScene, String.class, null, null);
        }*/
    }


}
