package com.dema.versatile.flashlight.core.log;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;

public class RateLog {

    private static final String KEY = "rate";

    public static void showReport(){
        UtilsLog.log(KEY,"show",null);
    }

    public static void fiveStarReport(){
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "type", "5star");
        UtilsLog.log(KEY,"click",jsonObject);
    }

    public static void submitReport(int starNumber){
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "type", "submit");
        UtilsJson.JsonSerialization(jsonObject, "star", String.valueOf(starNumber));
        UtilsLog.log(KEY,"click",jsonObject);
    }

    public static void closeReport(int starNumber){
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "type", "close");
        UtilsJson.JsonSerialization(jsonObject, "star", String.valueOf(starNumber));
        UtilsLog.log(KEY,"click",jsonObject);
    }
}
