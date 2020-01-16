package com.dema.versatile.scene.utils;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.scene.core.CMSceneFactory;

/**
 * Created by wangyu on 2019/8/26.
 */
public class SceneLog {

    public static final String VALUE_STRING_KEY1 = "scene";
    public static final String VALUE_STRING_KEY2 = "log";

    public static void log(String result, String reason, String action, String trigger, String scene, String key) {
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "result", result);
        UtilsJson.JsonSerialization(jsonObject, "reason", reason);
        UtilsJson.JsonSerialization(jsonObject, "action", action);
        UtilsJson.JsonSerialization(jsonObject, "trigger", trigger);
        UtilsJson.JsonSerialization(jsonObject, "scene", scene);
        UtilsJson.JsonSerialization(jsonObject, "key", key);
        UtilsLog.log(VALUE_STRING_KEY1, VALUE_STRING_KEY2, jsonObject);
    }

    public static void logSuccess(String action, String trigger, String scene, String key) {
        log("success", null, action, trigger, scene, key);
    }

    public static void logFail(String reason, String trigger, String scene, String key) {
        log("fail", reason, null, trigger, scene, key);
    }

    public static JSONObject logAlertShow(String type,String scene,String trigger,boolean isAdLoaded,int alertCount) {
        UtilsLog.alivePull(type, scene);
        JSONObject mJsonObject = new JSONObject();
        UtilsJson.JsonSerialization(mJsonObject, "type", type);
        UtilsJson.JsonSerialization(mJsonObject, "scene", scene);
        UtilsJson.JsonSerialization(mJsonObject, "trigger", trigger);
        UtilsJson.JsonSerialization(mJsonObject, "contains_ad", isAdLoaded);
        UtilsJson.JsonSerialization(mJsonObject, "show_id", UtilsEnv.getPhoneID(CMSceneFactory.getApplication()) + "_" + System.currentTimeMillis());
        UtilsJson.JsonSerialization(mJsonObject, "show_count", alertCount);
        UtilsLog.log("scene", "show", mJsonObject);
        return mJsonObject;
    }

    public static void logAlertClick(JSONObject jsonObject) {
        UtilsLog.log("scene", "click", jsonObject);
    }

}
