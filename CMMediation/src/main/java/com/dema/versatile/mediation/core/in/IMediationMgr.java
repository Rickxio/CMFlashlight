package com.dema.versatile.mediation.core.in;

import android.content.Context;
import android.util.Size;
import android.view.ViewGroup;

import org.json.JSONObject;

import com.dema.versatile.lib.core.in.ICMJson;
import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.lib.core.in.ICMObserver;

public interface IMediationMgr extends ICMMgr, ICMObserver<IMediationMgrListener>, ICMJson {
    boolean addAdConfig(JSONObject jsonObject);

    boolean isAdLoaded(String strKey);

    boolean isAdLoading(String strKey);

    // 默认请求
    boolean requestAdAsync(String strKey, String strScene);

    /*
        arrayResLayoutID: Mopub自定义Native请求
        size:Admob Adaptive Banner自定义宽度请求
     */
    boolean requestAdAsync(String strKey, String strScene, int[] arrayResLayoutID, Size size);

    // 默认展示
    boolean showAdView(String strKey, ViewGroup VGContainer, Context context);

    /*
        arrayResLayoutID: Admob/Facebook自定义Native展示
     */
    boolean showAdView(String strKey, ViewGroup VGContainer, int[] arrayResLayoutID, Context context);

    boolean showInterstitialAd(String strKey, String strScene, Context context);

    boolean releaseAd(String strKey);

    boolean releaseAllAd();
}
