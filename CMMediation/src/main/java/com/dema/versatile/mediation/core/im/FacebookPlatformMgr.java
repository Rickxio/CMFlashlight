package com.dema.versatile.mediation.core.im;

import android.text.TextUtils;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsEncrypt;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IAdPlatformMgr;
import com.dema.versatile.mediation.core.in.IAdPlatformMgrListener;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.utils.UtilsAd;
import com.dema.versatile.mediation.utils.UtilsFacebook;

public class FacebookPlatformMgr implements IAdPlatformMgr {
    public FacebookPlatformMgr() {
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean requestBannerAdAsync(String strAdKey, String strAdID, String strAdBannerSize, Size size, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            AdView adView = new AdView(CMMediationFactory.getApplication(), strAdID, UtilsFacebook.getBannerSize(CMMediationFactory.getApplication(), strAdBannerSize));
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "loaded");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(adView, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsJson.JsonSerialization(jsonObject, "code", adError.getErrorCode());
                    UtilsJson.JsonSerialization(jsonObject, "message", adError.getErrorMessage());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(adError.getErrorCode());
                    }
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "impression");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "clicked");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }
            });

            adView.loadAd();
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "request");
            UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error error) {

        }

        return false;
    }

    @Override
    public boolean requestNativeAdAsync(String strAdKey, String strAdID, int[] arrayResLayoutID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            NativeAd nativeAd = new NativeAd(CMMediationFactory.getApplication(), strAdID);
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onAdLoaded(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "loaded");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(nativeAd, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onMediaDownloaded(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "code", adError.getErrorCode());
                    UtilsJson.JsonSerialization(jsonObject, "message", adError.getErrorMessage());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(adError.getErrorCode());
                    }
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "impression");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "clicked");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }
            });

            nativeAd.loadAd();
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "request");
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error error) {

        }

        return false;
    }

    @Override
    public boolean requestInterstitialAdAsync(String strAdKey, String strAdID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            InterstitialAd interstitialAd = new InterstitialAd(CMMediationFactory.getApplication(), strAdID);
            interstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onAdLoaded(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "loaded");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(interstitialAd, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "code", adError.getErrorCode());
                    UtilsJson.JsonSerialization(jsonObject, "message", adError.getErrorMessage());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(adError.getErrorCode());
                    }
                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "impression");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "clicked");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "impression");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }
            });

            interstitialAd.loadAd();
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "request");
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error error) {

        }

        return false;
    }

    @Override
    public boolean showBannerAd(AdBean adBean, ViewGroup VGContainer) {
        if (null == adBean || null == adBean.mObjectAd || null == adBean.mIAdItem)
            return false;

        return UtilsAd.showAdView(CMMediationFactory.getApplication(), (View) adBean.mObjectAd, VGContainer, adBean.mIAdItem.isNeedMask());
    }

    @Override
    public boolean showNativeAd(AdBean adBean, ViewGroup VGContainer, int[] arrayResLayoutID) {
        if (null == adBean || null == adBean.mObjectAd || null == adBean.mIAdItem)
            return false;

        return UtilsAd.showAdView(CMMediationFactory.getApplication(),
                UtilsFacebook.getDefaultNativeAdView(CMMediationFactory.getApplication(), (NativeAd) adBean.mObjectAd, arrayResLayoutID),
                VGContainer, adBean.mIAdItem.isNeedMask());
    }

    @Override
    public boolean showInterstitialAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        ((InterstitialAd) adBean.mObjectAd).show();
        return true;
    }

    @Override
    public boolean releaseAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        if (adBean.mObjectAd instanceof com.facebook.ads.AdView) {
            ((com.facebook.ads.AdView) adBean.mObjectAd).destroy();
        } else if (adBean.mObjectAd instanceof com.facebook.ads.NativeAd) {
            ((com.facebook.ads.NativeAd) adBean.mObjectAd).destroy();
        } else if (adBean.mObjectAd instanceof com.facebook.ads.InterstitialAd) {
            ((com.facebook.ads.InterstitialAd) adBean.mObjectAd).destroy();
        } else {
            return false;
        }

        return true;
    }
}
