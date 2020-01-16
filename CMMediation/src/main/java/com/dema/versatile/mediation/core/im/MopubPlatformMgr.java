package com.dema.versatile.mediation.core.im;

import android.text.TextUtils;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.MediaViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.MoPubVideoNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsEncrypt;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.R;
import com.dema.versatile.mediation.core.in.IAdPlatformMgr;
import com.dema.versatile.mediation.core.in.IAdPlatformMgrListener;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.utils.UtilsAd;
import com.dema.versatile.mediation.utils.UtilsMopub;
import com.dema.versatile.mediation.view.AdActivity;

public class MopubPlatformMgr implements IAdPlatformMgr {
    public MopubPlatformMgr() {
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean requestBannerAdAsync(String strAdKey, String strAdID, String strAdBannerSize, Size size, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID) || TextUtils.isEmpty(strAdBannerSize))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            MoPubView moPubView = new MoPubView(CMMediationFactory.getApplication());
            moPubView.setAdUnitId(strAdID);
            moPubView.setAdSize(UtilsMopub.getBannerSize(CMMediationFactory.getApplication(), strAdBannerSize));
            moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
                @Override
                public void onBannerLoaded(MoPubView banner) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "loaded");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(moPubView, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                    moPubView.destroy();
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsJson.JsonSerialization(jsonObject, "code", errorCode.getIntCode());
                    UtilsJson.JsonSerialization(jsonObject, "message", errorCode.toString());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(errorCode.getIntCode());
                    }
                }

                @Override
                public void onBannerClicked(MoPubView banner) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "clicked");
                    UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }

                @Override
                public void onBannerExpanded(MoPubView banner) {
                }

                @Override
                public void onBannerCollapsed(MoPubView banner) {
                }
            });

            moPubView.loadAd();
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "request");
            UtilsJson.JsonSerialization(jsonObject, "size", strAdBannerSize);
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean requestNativeAdAsync(String strAdKey, String strAdID, int[] arrayResLayoutID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID))
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            MoPubNative moPubNative = new MoPubNative(CMMediationFactory.getApplication(), strAdID, new MoPubNative.MoPubNativeNetworkListener() {
                @Override
                public void onNativeLoad(NativeAd nativeAd) {
                    if (null == nativeAd)
                        return;

                    nativeAd.setMoPubNativeEventListener(new NativeAd.MoPubNativeEventListener() {
                        @Override
                        public void onImpression(View view) {
                            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "impression");
                            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                            if (null != iAdPlatformMgrListener) {
                                iAdPlatformMgrListener.onAdImpression();
                            }
                        }

                        @Override
                        public void onClick(View view) {
                            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "clicked");
                            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                            if (null != iAdPlatformMgrListener) {
                                iAdPlatformMgrListener.onAdClicked();
                            }
                        }
                    });

                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "loaded");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(nativeAd, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onNativeFail(NativeErrorCode errorCode) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "code", errorCode.getIntCode());
                    UtilsJson.JsonSerialization(jsonObject, "message", errorCode.toString());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(errorCode.getIntCode());
                    }
                }
            });

            MoPubStaticNativeAdRenderer staticAdRender = new MoPubStaticNativeAdRenderer(
                    new ViewBinder.Builder((null == arrayResLayoutID || arrayResLayoutID.length < 1) ? R.layout.layout_mopub_native_ad : arrayResLayoutID[0])
                            .titleId(R.id.native_title)
                            .textId(R.id.native_text)
                            .mainImageId(R.id.native_main_image)
                            .iconImageId(R.id.native_icon_image)
                            .callToActionId(R.id.native_cta)
                            .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                            .build());
            MoPubVideoNativeAdRenderer videoAdRenderer = new MoPubVideoNativeAdRenderer(
                    new MediaViewBinder.Builder((null == arrayResLayoutID || arrayResLayoutID.length < 2) ? R.layout.layout_mopub_native_video_ad : arrayResLayoutID[1])
                            .titleId(R.id.native_title)
                            .textId(R.id.native_text)
                            .mediaLayoutId(R.id.native_media_layout)
                            .iconImageId(R.id.native_icon_image)
                            .callToActionId(R.id.native_cta)
                            .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                            .build());
            moPubNative.registerAdRenderer(staticAdRender);
            moPubNative.registerAdRenderer(videoAdRenderer);

            moPubNative.makeRequest();
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_NATIVE, "request");
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //  若传入一个生命周期不覆盖插屏广告的activity就无法响应点击的回调函数
    @Override
    public boolean requestInterstitialAdAsync(String strAdKey, String strAdID, IAdPlatformMgrListener iAdPlatformMgrListener) {
        if (TextUtils.isEmpty(strAdID) || null == AdActivity.getInstance())
            return false;

        try {
            String strAdRequestID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(CMMediationFactory.getApplication()) + System.currentTimeMillis());
            MoPubInterstitial moPubInterstitial = new MoPubInterstitial(AdActivity.getInstance(), strAdID);
            moPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                @Override
                public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "loaded");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdLoaded(moPubInterstitial, strAdKey, strAdRequestID);
                    }
                }

                @Override
                public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                    moPubInterstitial.destroy();
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "failed");
                    UtilsJson.JsonSerialization(jsonObject, "code", errorCode.getIntCode());
                    UtilsJson.JsonSerialization(jsonObject, "message", errorCode.toString());
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdFailed(errorCode.getIntCode());
                    }
                }

                @Override
                public void onInterstitialShown(MoPubInterstitial interstitial) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "impression");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdImpression();
                    }
                }

                @Override
                public void onInterstitialClicked(MoPubInterstitial interstitial) {
                    JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "clicked");
                    UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
                    if (null != iAdPlatformMgrListener) {
                        iAdPlatformMgrListener.onAdClicked();
                    }
                }

                @Override
                public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                }
            });

            moPubInterstitial.load();
            JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(strAdKey, strAdID, strAdRequestID, IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL, "request");
            UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean showBannerAd(AdBean adBean, ViewGroup VGContainer) {
        if (null == adBean || null == adBean.mObjectAd || null == adBean.mIAdItem)
            return false;

        JSONObject jsonObject = UtilsAd.getBaseAdLogJsonObject(adBean.mAdKey, adBean.mIAdItem.getAdID(), adBean.mAdRequestID, IMediationConfig.VALUE_STRING_TYPE_BANNER, "impression");
        UtilsJson.JsonSerialization(jsonObject, "size", adBean.mIAdItem.getAdBannerSize());
        UtilsAd.log(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, jsonObject);

        return UtilsAd.showAdView(CMMediationFactory.getApplication(), (View) adBean.mObjectAd, VGContainer, adBean.mIAdItem.isNeedMask());
    }

    @Override
    public boolean showNativeAd(AdBean adBean, ViewGroup VGContainer, int[] arrayResLayoutID) {
        if (null == adBean || null == adBean.mObjectAd || null == adBean.mIAdItem)
            return false;

        return UtilsAd.showAdView(CMMediationFactory.getApplication(),
                UtilsMopub.getDefaultNativeAdView(CMMediationFactory.getApplication(), (NativeAd) adBean.mObjectAd),
                VGContainer, adBean.mIAdItem.isNeedMask());
    }

    @Override
    public boolean showInterstitialAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        ((MoPubInterstitial) adBean.mObjectAd).show();
        return true;
    }

    @Override
    public boolean releaseAd(AdBean adBean) {
        if (null == adBean || null == adBean.mObjectAd)
            return false;

        if (adBean.mObjectAd instanceof com.mopub.mobileads.MoPubView) {
            ((com.mopub.mobileads.MoPubView) adBean.mObjectAd).destroy();
        } else if (adBean.mObjectAd instanceof com.mopub.nativeads.NativeAd) {
            ((com.mopub.nativeads.NativeAd) adBean.mObjectAd).destroy();
        } else if (adBean.mObjectAd instanceof com.mopub.mobileads.MoPubInterstitial) {
            ((com.mopub.mobileads.MoPubInterstitial) adBean.mObjectAd).destroy();
        } else {
            return false;
        }

        return true;
    }
}
