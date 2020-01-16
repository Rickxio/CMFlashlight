package com.dema.versatile.flashlight.core.log;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;

public class MainLog {

    public static void switchClick(String status,String type){
        JSONObject object = new JSONObject();
        UtilsJson.JsonSerialization(object,"status",status);
        UtilsJson.JsonSerialization(object,"type",type);
        UtilsLog.log("Flashlight","click",object);
    }

}
