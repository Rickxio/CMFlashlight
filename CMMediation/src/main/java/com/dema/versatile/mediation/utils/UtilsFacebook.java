package com.dema.versatile.mediation.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;

import java.util.Arrays;

import com.dema.versatile.mediation.R;
import com.dema.versatile.mediation.core.in.IMediationConfig;

public class UtilsFacebook {
    public static final String VALUE_STRING_IMG_16_9_APP_INSTALL_TEST_TYPE = "IMG_16_9_APP_INSTALL";
    public static final String VALUE_STRING_IMG_16_9_LINK_TEST_TYPE = "IMG_16_9_LINK";
    public static final String VALUE_STRING_VID_HD_16_9_46S_APP_INSTALL_TEST_TYPE = "VID_HD_16_9_46S_APP_INSTALL";
    public static final String VALUE_STRING_VID_HD_16_9_46S_LINK_TEST_TYPE = "VID_HD_16_9_46S_LINK";
    public static final String VALUE_STRING_VID_HD_16_9_15S_APP_INSTALL_TEST_TYPE = "VID_HD_16_9_15S_APP_INSTALL";
    public static final String VALUE_STRING_VID_HD_16_9_15S_LINK_TEST_TYPE = "VID_HD_16_9_15S_LINK";
    public static final String VALUE_STRING_VID_HD_9_16_39S_APP_INSTALL_TEST_TYPE = "VID_HD_9_16_39S_APP_INSTALL";
    public static final String VALUE_STRING_VID_HD_9_16_39S_LINK_TEST_TYPE = "VID_HD_9_16_39S_LINK";
    public static final String VALUE_STRING_CAROUSEL_IMG_SQUARE_APP_INSTALL_TEST_TYPE = "CAROUSEL_IMG_SQUARE_APP_INSTALL";
    public static final String VALUE_STRING_CAROUSEL_IMG_SQUARE_LINK_TEST_TYPE = "CAROUSEL_IMG_SQUARE_LINK";

    public static void init(Context context) {
        if (null == context)
            return;

        try {
            if (AudienceNetworkAds.isInAdsProcess(context)) {
                AudienceNetworkAds.initialize(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTestAdID(String strAdType) {
        if (TextUtils.isEmpty(strAdType))
            return "";

        switch (strAdType) {
            case IMediationConfig.VALUE_STRING_TYPE_BANNER:
            case IMediationConfig.VALUE_STRING_TYPE_NATIVE:
            case IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL:
                return VALUE_STRING_IMG_16_9_APP_INSTALL_TEST_TYPE + "#" + "0";
            case IMediationConfig.VALUE_STRING_TYPE_REWARDED_VIDEO:
                return VALUE_STRING_VID_HD_9_16_39S_APP_INSTALL_TEST_TYPE + "#" + "0";
            default:
                break;
        }

        return "";
    }

    public static AdSize getBannerSize(Context context, String strSize) {
        if (null == context || TextUtils.isEmpty(strSize))
            return AdSize.BANNER_HEIGHT_50;

        switch (strSize) {
            case IMediationConfig.VALUE_STRING_BANNER_SIZE_MIDDLE:
                return AdSize.BANNER_HEIGHT_90;
            case IMediationConfig.VALUE_STRING_BANNER_SIZE_LARGE:
                return AdSize.RECTANGLE_HEIGHT_250;
            default:
                return AdSize.BANNER_HEIGHT_50;
        }
    }

    public static View getDefaultNativeAdView(Context context, NativeAd nativeAd, int[] arrayResLayoutID) {
        if (null == context || null == nativeAd)
            return null;

        nativeAd.unregisterView();

        LayoutInflater inflater = LayoutInflater.from(context);
        View vAd = inflater.inflate((null == arrayResLayoutID || arrayResLayoutID.length < 1) ? R.layout.layout_facebook_native_ad : arrayResLayoutID[0], null);

        NativeAdLayout nativeAdLayout = vAd.findViewById(R.id.native_ad_container);
        LinearLayout adChoicesContainer = vAd.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        AdIconView nativeAdIcon = vAd.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = vAd.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = vAd.findViewById(R.id.native_ad_media);
        TextView nativeAdBody = vAd.findViewById(R.id.native_ad_body);
        Button nativeAdCallToAction = vAd.findViewById(R.id.native_ad_call_to_action);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

        nativeAd.registerViewForInteraction(
                vAd,
                nativeAdMedia,
                nativeAdIcon,
                Arrays.asList(nativeAdCallToAction));

        return vAd;
    }
}
