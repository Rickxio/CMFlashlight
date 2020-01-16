package com.dema.versatile.mediation.utils;

import android.text.TextUtils;

import com.dema.versatile.mediation.core.in.IMediationConfig;

public class UtilsAdx {
    public static final String VALUE_STRING_ADX_BANNER_TEST_ID = "/6499/example/banner";
    public static final String VALUE_STRING_ADX_NATIVE_TEST_ID = "/6499/example/native";
    public static final String VALUE_STRING_ADX_INTERSTITIAL_TEST_ID = "/6499/example/interstitial";
    public static final String VALUE_STRING_ADX_REWARDED_VIDEO_TEST_ID = "/6499/example/rewarded-video";

    public static String getTestAdID(String strAdType) {
        if (TextUtils.isEmpty(strAdType))
            return "";

        switch (strAdType) {
            case IMediationConfig.VALUE_STRING_TYPE_BANNER:
                return VALUE_STRING_ADX_BANNER_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_NATIVE:
                return VALUE_STRING_ADX_NATIVE_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL:
                return VALUE_STRING_ADX_INTERSTITIAL_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_REWARDED_VIDEO:
                return VALUE_STRING_ADX_REWARDED_VIDEO_TEST_ID;
            default:
                break;
        }

        return "";
    }
}
