package com.dema.versatile.lib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMLog;

public class UtilsLog {
    public static final String VALUE_STRING_LOG_KEY1_ALIVE = "alive";       // 留存
    public static final String VALUE_STRING_LOG_KEY2_NEW = "new";           // 新装
    public static final String VALUE_STRING_LOG_KEY2_UPDATE = "update";     // 更新
    public static final String VALUE_STRING_LOG_KEY2_RUN = "run";           // 运行
    public static final String VALUE_STRING_LOG_KEY2_START = "start";       // 启动
    public static final String VALUE_STRING_LOG_KEY2_ACTION = "action";     // 激活
    public static final String VALUE_STRING_LOG_KEY2_PULL = "pull";         // 拉活
    public static final String VALUE_STRING_LOG_KEY2_SESSION = "session";   // 会话

    public static final String VALUE_STRING_LOG_UPDATE_CONTENT_FROM_VERSION_CODE = "from_version_code";       // 更新来源版本号Key名称
    public static final String VALUE_STRING_LOG_START_CONTENT_TYPE = "type";        // 启动类型Key名称    icon/notification/pull_alert
    public static final String VALUE_STRING_LOG_START_CONTENT_SCENE = "scene";      // 启动场景Key名称
    public static final String VALUE_STRING_LOG_PULL_CONTENT_TYPE = "type";         // 拉活类型Key名称    notification/pull_alert/tips_alert
    public static final String VALUE_STRING_LOG_PULL_CONTENT_SCENE = "scene";       // 拉活场景Key名称
    public static final String VALUE_STRING_LOG_SESSION_CONTENT_NAME = "name";      // 会话Activity类名
    public static final String VALUE_STRING_LOG_SESSION_CONTENT_TIME = "time";      // 会话Activity时长

    private static boolean sIsNeedSendLog = false;
    private static boolean sIsNeedLocalLog = false;
    private static String sLogURL = null;
    private static String sCrashURL = null;
    private static String sSeparator = "---***---";
    private static String sLogPath = null;
    private static String sCrashPath = null;

    public static void init(Context context, boolean bIsNeedSendLog, boolean bIsNeedLocalLog,
                            String strLogURL, String strCrashURL, String strSeparator) {
        if (null == context)
            return;

        sIsNeedSendLog = bIsNeedSendLog;
        sIsNeedLocalLog = bIsNeedLocalLog;
        sLogURL = strLogURL;
        sCrashURL = strCrashURL;
        if (!TextUtils.isEmpty(strSeparator)) {
            sSeparator = strSeparator;
        }

        String strProcessName = UtilsSystem.getMyProcessName(context);
        if (TextUtils.isEmpty(strProcessName)) {
            sLogPath = "log.dat";
            sCrashPath = "crash.dat";
        } else {
            sLogPath = strProcessName + ".log.dat";
            sCrashPath = strProcessName + ".crash.dat";
        }
    }

    public static String getLogURL() {
        return sLogURL;
    }

    public static String getCrashURL() {
        return sCrashURL;
    }

    public static String getLogPath() {
        return sLogPath;
    }

    public static void setLogPath(String strLogPath) {
        sLogPath = strLogPath;
    }

    public static String getCrashPath() {
        return sCrashPath;
    }

    public static void setCrashPath(String strCrashPath) {
        sCrashPath = strCrashPath;
    }

    public static String getSeparator() {
        return sSeparator;
    }

    public static void aliveLog(String strKey2, JSONObject jsonObjectContent) {
        log(VALUE_STRING_LOG_KEY1_ALIVE, strKey2, jsonObjectContent);
    }

    public static void aliveStart(String strType) {
        aliveStart(strType, null);
    }

    public static void aliveStart(String strType, String strScene) {
        if (TextUtils.isEmpty(strType))
            return;

        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, VALUE_STRING_LOG_START_CONTENT_TYPE, strType);
        UtilsJson.JsonSerialization(jsonObject, VALUE_STRING_LOG_START_CONTENT_SCENE, strScene);
        aliveLog(VALUE_STRING_LOG_KEY2_START, jsonObject);
    }

    public static void alivePull(String strType) {
        alivePull(strType, null);
    }

    public static void alivePull(String strType, String strScene) {
        if (TextUtils.isEmpty(strType))
            return;

        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, VALUE_STRING_LOG_PULL_CONTENT_TYPE, strType);
        UtilsJson.JsonSerialization(jsonObject, VALUE_STRING_LOG_PULL_CONTENT_SCENE, strScene);
        aliveLog(VALUE_STRING_LOG_KEY2_PULL, jsonObject);
    }

    public static void log(String strKey1, String strKey2, JSONObject jsonObjectContent) {
        long t = System.currentTimeMillis();
        if (sIsNeedSendLog) {
            ICMLog iCMLog = CMLibFactory.getInstance().createInstance(ICMLog.class);
            iCMLog.log(strKey1, strKey2, jsonObjectContent, t);
        }

        logD("superlog", "---\n" + "c1:" + strKey1 + "\n" +
                "c2:" + (TextUtils.isEmpty(strKey2) ? "null" : strKey2) + "\n" +
                "c3:" + (null == jsonObjectContent ? "null" : jsonObjectContent.toString())+"\n"+
                "time:"+t
                );
    }

    public static void crash(Throwable throwable) {
        if (sIsNeedSendLog) {
            ICMLog iCMLog = CMLibFactory.getInstance().createInstance(ICMLog.class);
            iCMLog.crash(throwable);
        }

        logD("superlog", "[crash]\n" + "c3:" + throwable);
    }

    public static void send() {
        ICMLog iCMLog = CMLibFactory.getInstance().createInstance(ICMLog.class);
        iCMLog.send();
    }

    public static void logD(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.d(strTag, strLog);
    }

    public static void logI(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.i(strTag, strLog);
    }

    public static void logE(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.e(strTag, strLog);
    }

    public static void logV(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.v(strTag, strLog);
    }

    public static void logW(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.w(strTag, strLog);
    }
}
