package com.dema.versatile.mediation.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.NativeAd;

import com.dema.versatile.mediation.BuildConfig;
import com.dema.versatile.mediation.core.in.IMediationConfig;


public class UtilsMopub {
    public final static String VALUE_STRING_MOPUB_BANNER_TEST_ID = "b195f8dd8ded45fe847ad89ed1d016da";
    public final static String VALUE_STRING_MOPUB_BANNER_MEDIUM_RECTANGLE_TEST_ID = "252412d5e9364a05ab77d9396346d73d";
    public final static String VALUE_STRING_MOPUB_NATIVE_TEST_ID = "e33263a0d07849858d20718e1f6bd262";
    public final static String VALUE_STRING_MOPUB_INTERSTITIAL_TEST_ID = "24534e1901884e398f1253216226017e";
    public final static String VALUE_STRING_MOPUB_REWARDED_VIDEO_TEST_ID = "920b6145fb1546cf8b5cf2ac34638bb7";
    public final static String VALUE_STRING_MOPUB_REWARDED_VIDEO_RICH_MEDIA_TEST_ID = "15173ac6d3e54c9389b9a5ddca69b34b";

    private static boolean sInited = false;

    public static void init(Context context, String strAdID) {
        if (null == context || TextUtils.isEmpty(strAdID) || sInited)
            return;

        try {
            sInited = true;
            SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(strAdID)
                    .withLogLevel(BuildConfig.DEBUG ? MoPubLog.LogLevel.DEBUG : MoPubLog.LogLevel.INFO)
                    .build();
            MoPub.initializeSdk(context, sdkConfiguration, new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    public static String getTestAdID(String strAdType, String strBannerSize) {
        if (TextUtils.isEmpty(strAdType))
            return "";

        switch (strAdType) {
            case IMediationConfig.VALUE_STRING_TYPE_BANNER: {
                if (TextUtils.isEmpty(strBannerSize))
                    return "";

                if (strBannerSize.equals(IMediationConfig.VALUE_STRING_BANNER_SIZE_SMALL)) {
                    return VALUE_STRING_MOPUB_BANNER_TEST_ID;
                }else if (strBannerSize.equals(IMediationConfig.VALUE_STRING_BANNER_SIZE_MIDDLE)) {
                    return VALUE_STRING_MOPUB_BANNER_TEST_ID;
                }else if (strBannerSize.equals(IMediationConfig.VALUE_STRING_BANNER_SIZE_LARGE)) {
                    return VALUE_STRING_MOPUB_BANNER_MEDIUM_RECTANGLE_TEST_ID;
                }
            }
            case IMediationConfig.VALUE_STRING_TYPE_NATIVE:
                return VALUE_STRING_MOPUB_NATIVE_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL:
                return VALUE_STRING_MOPUB_INTERSTITIAL_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_REWARDED_VIDEO:
                return VALUE_STRING_MOPUB_REWARDED_VIDEO_TEST_ID;
            default:
                break;
        }

        return "";
    }

    public static MoPubView.MoPubAdSize getBannerSize(Context context, String strSize) {
        if (null == context || TextUtils.isEmpty(strSize))
            return MoPubView.MoPubAdSize.HEIGHT_50;

        switch (strSize) {
            case IMediationConfig.VALUE_STRING_BANNER_SIZE_MIDDLE:
                return MoPubView.MoPubAdSize.HEIGHT_90;
            case IMediationConfig.VALUE_STRING_BANNER_SIZE_LARGE:
                return MoPubView.MoPubAdSize.HEIGHT_250;
            default:
                return MoPubView.MoPubAdSize.HEIGHT_50;
        }
    }

    public static View getDefaultNativeAdView(Context context, NativeAd nativeAd) {
        if (null == context || null == nativeAd)
            return null;

        AdapterHelper adapterHelper = new AdapterHelper(context, 0, 3);
        return adapterHelper.getAdView(null, null, nativeAd);
    }
}
