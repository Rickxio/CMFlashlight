package com.dema.versatile.mediation.core.in;

import android.util.Size;
import android.view.ViewGroup;

import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.mediation.core.im.AdBean;

public interface IAdPlatformMgr extends ICMMgr {
    boolean requestBannerAdAsync(String strAdKey, String strAdID, String strAdBannerSize, Size size, IAdPlatformMgrListener iAdPlatformMgrListener);

    boolean requestNativeAdAsync(String strAdKey, String strAdID, int[] arrayResLayoutID, IAdPlatformMgrListener iAdPlatformMgrListener);

    boolean requestInterstitialAdAsync(String strAdKey, String strAdID, IAdPlatformMgrListener iAdPlatformMgrListener);

    boolean showBannerAd(AdBean adBean, ViewGroup VGContainer);

    boolean showNativeAd(AdBean adBean, ViewGroup VGContainer, int[] arrayResLayoutID);

    boolean showInterstitialAd(AdBean adBean);

    boolean releaseAd(AdBean adBean);
}
