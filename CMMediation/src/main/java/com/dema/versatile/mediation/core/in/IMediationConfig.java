package com.dema.versatile.mediation.core.in;

import java.io.Serializable;

import com.dema.versatile.lib.core.in.ICMJson;
import com.dema.versatile.lib.core.in.ICMObj;

public interface IMediationConfig extends ICMObj, ICMJson, Serializable {
    String getAdKey();

    int getCacheCount();

    boolean isSupportRequestScene(String strScene);

    boolean isSupportShowScene(String strScene);

    IAdItem getAdItem(int nIndex);

    int getAdItemCount();

    String VALUE_STRING_PLATFORM_UNKNOWN = "unknown";
    String VALUE_STRING_PLATFORM_FACEBOOK = "facebook";
    String VALUE_STRING_PLATFORM_ADMOB = "admob";
    String VALUE_STRING_PLATFORM_ADX = "adx";
    String VALUE_STRING_PLATFORM_MOPUB = "mopub";
    String VALUE_STRING_PLATFORM_INMOBI = "inmobi";

    String VALUE_STRING_TYPE_UNKNOWN = "unknown";
    String VALUE_STRING_TYPE_BANNER = "banner";
    String VALUE_STRING_TYPE_NATIVE = "native";
    String VALUE_STRING_TYPE_INTERSTITIAL = "interstitial";
    String VALUE_STRING_TYPE_REWARDED_VIDEO = "rewarded_video";

    String VALUE_STRING_BANNER_SIZE_SMALL = "small";
    String VALUE_STRING_BANNER_SIZE_MIDDLE = "middle";
    String VALUE_STRING_BANNER_SIZE_LARGE = "large";
}
