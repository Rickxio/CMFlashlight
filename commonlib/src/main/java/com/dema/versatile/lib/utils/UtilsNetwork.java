package com.dema.versatile.lib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONObject;

import static com.dema.versatile.lib.utils.UtilsLog.logD;

public class UtilsNetwork {
    public static final int VALUE_INT_FAIL_CODE = -1;
    public static final int VALUE_INT_SUCCESS_CODE = 1;

    public static final int VALUE_INT_NETWORK_UNKNOWN_TYPE = 0;
    public static final int VALUE_INT_NETWORK_WIFI_TYPE = 1;
    public static final int VALUE_INT_NETWORK_2G_TYPE = 2;
    public static final int VALUE_INT_NETWORK_3G_TYPE = 3;
    public static final int VALUE_INT_NETWORK_4G_TYPE = 4;

    public static final String VALUE_STRING_HTTP_TYPE = "http";

    private static String sDomainName = null;

    public static void init(String strDomainName) {
        sDomainName = strDomainName;
    }

    public static int getNetworkType(Context context) {
        try {
            if (null == context)
                return VALUE_INT_NETWORK_UNKNOWN_TYPE;

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null == cm)
                return VALUE_INT_NETWORK_UNKNOWN_TYPE;

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (null == networkInfo)
                return VALUE_INT_NETWORK_UNKNOWN_TYPE;

            int nType = networkInfo.getType();
            int nSubType = networkInfo.getSubtype();
            if (ConnectivityManager.TYPE_WIFI == nType || ConnectivityManager.TYPE_WIMAX == nType || ConnectivityManager.TYPE_ETHERNET == nType) {
                return VALUE_INT_NETWORK_WIFI_TYPE;
            } else if (ConnectivityManager.TYPE_MOBILE == nType) {
                switch (nSubType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return VALUE_INT_NETWORK_2G_TYPE;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return VALUE_INT_NETWORK_3G_TYPE;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return VALUE_INT_NETWORK_4G_TYPE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return VALUE_INT_NETWORK_UNKNOWN_TYPE;
    }

    public static String getURL() {
        return VALUE_STRING_HTTP_TYPE + "://" + sDomainName;
    }

    public static String getURL(String strURL) {
        if (TextUtils.isEmpty(strURL))
            return null;
        String url = getURL() + (strURL.startsWith("/") ? strURL : "/" + strURL);
        logD("superlog", "getURL= "+url);
        return url;
    }

    public static JSONObject getBasicRequestParam(Context context) {
        if (null == context)
            return null;

        JSONObject jsonObject = UtilsEnv.getBasicInfo(context);
        if (null == jsonObject)
            return null;

        UtilsJson.JsonSerialization(jsonObject, "installed_time", String.valueOf(UtilsInstall.getInstalledTime(context)));
        UtilsJson.JsonSerialization(jsonObject, "local_hour", UtilsTime.getDateStringHh(System.currentTimeMillis()));
        UtilsJson.JsonSerialization(jsonObject, "virtual", UtilsSystem.isVpn() || UtilsSystem.isEmulator() || UtilsSystem.isRoot());
        UtilsJson.JsonSerialization(jsonObject, "ab_test", UtilsEnv.getABTestID());
        return jsonObject;
    }
}
