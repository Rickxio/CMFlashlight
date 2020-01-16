package com.dema.versatile.mediation.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import com.dema.versatile.lib.utils.UtilsSize;
import com.dema.versatile.mediation.R;
import com.dema.versatile.mediation.core.in.IMediationConfig;

public class UtilsAdmob {
    public static final String VALUE_STRING_ADMOB_APP_TEST_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String VALUE_STRING_ADMOB_BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String VALUE_STRING_ADMOB_NATIVE_ADVANCED_TEST_ID = "ca-app-pub-3940256099942544/2247696110";
    public static final String VALUE_STRING_ADMOB_INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final String VALUE_STRING_ADMOB_REWARDED_VIDEO_TEST_ID = "ca-app-pub-3940256099942544/5224354917";

    public static void init(Context context) {
        if (null == context)
            return;

//        try {
//            MobileAds.initialize(context, new OnInitializationCompleteListener() {
//                @Override
//                public void onInitializationComplete(InitializationStatus initializationStatus) {
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static String getTestAdID(String strAdType) {
        if (TextUtils.isEmpty(strAdType))
            return "";

        switch (strAdType) {
            case IMediationConfig.VALUE_STRING_TYPE_BANNER:
                return VALUE_STRING_ADMOB_BANNER_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_NATIVE:
                return VALUE_STRING_ADMOB_NATIVE_ADVANCED_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL:
                return VALUE_STRING_ADMOB_INTERSTITIAL_TEST_ID;
            case IMediationConfig.VALUE_STRING_TYPE_REWARDED_VIDEO:
                return VALUE_STRING_ADMOB_REWARDED_VIDEO_TEST_ID;
            default:
                break;
        }

        return "";
    }

    public static AdSize getBannerSize(Context context, String strSize, Size size) {
        if (null == context || TextUtils.isEmpty(strSize))
            return AdSize.getCurrentOrientationBannerAdSizeWithWidth(context,
                    UtilsSize.pxToDp(context, UtilsSize.getScreenWidth(context)));

        switch (strSize) {
            case IMediationConfig.VALUE_STRING_BANNER_SIZE_MIDDLE:
                return AdSize.LARGE_BANNER;
            case IMediationConfig.VALUE_STRING_BANNER_SIZE_LARGE:
                return AdSize.MEDIUM_RECTANGLE;
            default:
                return AdSize.getCurrentOrientationBannerAdSizeWithWidth(context,
                        UtilsSize.pxToDp(context, null == size ? UtilsSize.getScreenWidth(context) : size.getWidth()));
        }
    }

    public static View getDefaultNativeAdView(Context context, UnifiedNativeAd nativeAd, int[] arrayResLayoutID) {
        if (null == context || null == nativeAd)
            return null;

        UnifiedNativeAdView vAd = (UnifiedNativeAdView) View.inflate(context,
                (null == arrayResLayoutID || arrayResLayoutID.length < 1) ? R.layout.layout_admob_native_ad : arrayResLayoutID[0], null);

        MediaView mediaView = vAd.findViewById(R.id.ad_media);
        vAd.setMediaView(mediaView);
        vAd.setIconView(vAd.findViewById(R.id.ad_app_icon));
        vAd.setHeadlineView(vAd.findViewById(R.id.ad_headline));
        vAd.setBodyView(vAd.findViewById(R.id.ad_body));
        vAd.setCallToActionView(vAd.findViewById(R.id.ad_call_to_action));

        if (null == nativeAd.getIcon()) {
            vAd.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) vAd.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            vAd.getIconView().setVisibility(View.VISIBLE);
        }

        ((TextView) vAd.getHeadlineView()).setText(nativeAd.getHeadline());

        if (null == nativeAd.getBody()) {
            vAd.getBodyView().setVisibility(View.GONE);
        } else {
            vAd.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) vAd.getBodyView()).setText(nativeAd.getBody());
        }

        if (null == nativeAd.getCallToAction()) {
            vAd.getCallToActionView().setVisibility(View.GONE);
        } else {
            vAd.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) vAd.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        vAd.setNativeAd(nativeAd);
        return vAd;
    }

    public static String getErrorMessageByErrorCode(int errorCode) {
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                return "internal error";
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return "invalid request";
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return "network error";
            case AdRequest.ERROR_CODE_NO_FILL:
                return "no fill";
            default:
                return "unknown error";
        }
    }
}
