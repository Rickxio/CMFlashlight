package com.dema.versatile.mediation.core.im;

import android.text.TextUtils;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.ads.listeners.InterstitialAdEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dema.versatile.lib.utils.UtilsEncrypt;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IAdPlatformMgr;
import com.dema.versatile.mediation.core.in.IAdPlatformMgrListener;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.utils.UtilsAd;
import com.dema.versatile.mediation.utils.UtilsInmobi;

public class InmobiPlatformMgr implements IAdPlatformMgr {
    private List<Object> mListAd = null;

    public InmobiPlatformMgr() {
        _init();
    }

    private void _init() {
        mListAd = new ArrayList<>();
    }

    @Override
    public boolean requestBannerAdAsync(String strAdKey, String strAdID, String strAdBannerSize, Size size, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID) || TextUtils.isEmpty(strAdBannerSize))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            InMobiBanner inMobiBanner = new InMobiBanner(CMMediationFactory.getApplication(), Long.parseLong(strAdID));
            Size sizeBanner = UtilsInmobi.getBannerSize(CMMediationFactory.getApplication(), strAdBannerSize);
            if (null == sizeBanner)
                return false;

            inMobiBanner.setBannerSize(sizeBanner.getWidth(),sizeBanner.getHeight());
            inMobiBanner.setAnimationType(InMobiBanner.AnimationType.ANIMATION_OFF);
            inMobiBanner.setListener(new BannerAdEventListener() {
                @Override
                public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                    mListAd.remove(inMobiBanner);
                    inMobiBanner.setTag(sizeBanner);
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "loaded");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(inMobiBanner, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
                    mListAd.remove(inMobiBanner);
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsJson.JsonSerialization(jsonObject, "code", inMobiAdRequestStatus.getStatusCode().ordinal());
                    UtilsJson.JsonSerialization(jsonObject, "message", inMobiAdRequestStatus.getMessage());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(inMobiAdRequestStatus.getStatusCode().ordinal());
                    }
                }

                @Override
                public void onAdDisplayed(InMobiBanner inMobiBanner) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "clicked");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }
            });

            inMobiBanner.load();
            mListAd.add(inMobiBanner);
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "request");
            UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error error) {

        }

        return false;
    }

    @Override
    public boolean requestNativeAdAsync(String strAdKey, String strAdID, int[] arrayResLayoutID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        return false;
    }

    @Override
    public boolean requestInterstitialAdAsync(String strAdKey, String strAdID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            InMobiInterstitial inMobiInterstitial = new InMobiInterstitial(CMMediationFactory.getApplication(), Long.parseLong(strAdID), new InterstitialAdEventListener() {
                @Override
                public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
                    mListAd.remove(inMobiInterstitial);
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "loaded");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(inMobiInterstitial, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial, InMobiAdRequestStatus inMobiAdRequestStatus) {
                    mListAd.remove(inMobiInterstitial);
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "code", inMobiAdRequestStatus.getStatusCode().ordinal());
                    UtilsJson.JsonSerialization(jsonObject, "message", inMobiAdRequestStatus.getMessage());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(inMobiAdRequestStatus.getStatusCode().ordinal());
                    }
                }

                @Override
                public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "impression");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }

                @Override
                public void onAdClicked(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "clicked");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }
            });

            inMobiInterstitial.load();
            mListAd.add(inMobiInterstitial);
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "request");
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error error) {

        }

        return true;
    }

    @Override
    public boolean showBannerAd(AdBean adBean, ViewGroup VGContainer) {
        if (null == adBean || null == adBean.mObjectAd || null == adBean.mIAdItem)
            return false;

        JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(adBean.mAdKey, adBean.mIAdItem.getAdID(), adBean.mAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "impression");
        UtilsJson.JsonSerialization(jsonObject, "size", adBean.mIAdItem.getAdBannerSize());
        UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, jsonObject);

        return UtilsAd.showAdView(CMMediationFactory.getApplication(), (View) adBean.mObjectAd, VGContainer, adBean.mIAdItem.isNeedMask());
    }

    @Override
    public boolean showNativeAd(AdBean adBean, ViewGroup VGContainer, int[] arrayResLayoutID) {
        return false;
    }

    @Override
    public boolean showInterstitialAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        ((InMobiInterstitial) adBean.mObjectAd).show();
        return true;
    }

    @Override
    public boolean releaseAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        if (adBean.mObjectAd instanceof com.inmobi.ads.InMobiBanner) {
            ((com.inmobi.ads.InMobiBanner) adBean.mObjectAd).destroy();
        } else if (adBean.mObjectAd instanceof com.inmobi.ads.InMobiInterstitial) {

        }

        return true;
    }
}