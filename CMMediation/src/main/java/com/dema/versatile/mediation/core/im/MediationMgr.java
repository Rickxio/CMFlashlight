package com.dema.versatile.mediation.core.im;

import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dema.versatile.lib.core.im.CMObserverIntelligence;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IAdItem;
import com.dema.versatile.mediation.core.in.IAdPlatformMgr;
import com.dema.versatile.mediation.core.in.IAdPlatformMgrListener;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.mediation.core.in.IMediationMgrListener;
import com.dema.versatile.mediation.utils.UtilsAd;

public class MediationMgr extends CMObserverIntelligence<IMediationMgrListener> implements IMediationMgr {
    private Map<String, IAdPlatformMgr> mMapAdPlatformMgr = null;
    private Map<String, IMediationConfig> mMapMediationConfig = null;
    private Map<IMediationConfig, List<AdBean>> mMapMediationCache = null;
    private Map<IMediationConfig, List<AdBean>> mMapMediationAdViewUsedCache = null;
    private Map<IMediationConfig, Integer> mMapMediationRequestCount = null;
    private Map<String, Integer> mMapAdRequestIndex = null;

    public MediationMgr() {
        _init();
    }

    private void _init() {
        mMapAdPlatformMgr = new HashMap<>();
        mMapAdPlatformMgr.put(IMediationConfig.VALUE_STRING_PLATFORM_FACEBOOK, CMMediationFactory.getInstance().createInstance(IAdPlatformMgr.class, FacebookPlatformMgr.class));
        mMapAdPlatformMgr.put(IMediationConfig.VALUE_STRING_PLATFORM_ADMOB, CMMediationFactory.getInstance().createInstance(IAdPlatformMgr.class, AdmobPlatformMgr.class));
        mMapAdPlatformMgr.put(IMediationConfig.VALUE_STRING_PLATFORM_ADX, CMMediationFactory.getInstance().createInstance(IAdPlatformMgr.class, AdxPlatformMgr.class));
        mMapAdPlatformMgr.put(IMediationConfig.VALUE_STRING_PLATFORM_MOPUB, CMMediationFactory.getInstance().createInstance(IAdPlatformMgr.class, MopubPlatformMgr.class));
        mMapAdPlatformMgr.put(IMediationConfig.VALUE_STRING_PLATFORM_INMOBI, CMMediationFactory.getInstance().createInstance(IAdPlatformMgr.class, InmobiPlatformMgr.class));
        mMapMediationConfig = new HashMap<>();
        mMapMediationCache = new HashMap<>();
        mMapMediationAdViewUsedCache = new HashMap<>();
        mMapMediationRequestCount = new HashMap<>();
        mMapAdRequestIndex = new HashMap<>();
    }

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mMapMediationConfig = new HashMap<>();
        addAdConfig(jsonObject);
    }

    @Override
    public boolean addAdConfig(JSONObject jsonObject) {
        if (null == jsonObject)
            return false;

        Iterator iteratorKeys = jsonObject.keys();
        while (iteratorKeys.hasNext()) {
            String strKey = (String) iteratorKeys.next();
            if (TextUtils.isEmpty(strKey))
                continue;

            MediationConfig mediationConfig = UtilsJson.JsonUnserialization(jsonObject, strKey, IMediationConfig.class, MediationConfig.class);
            if (null == mediationConfig)
                continue;

            mediationConfig.setAdKey(strKey);
            mMapMediationConfig.put(strKey, mediationConfig);
        }

        return true;
    }

    @Override
    public boolean isAdLoaded(String strKey) {
        if (TextUtils.isEmpty(strKey))
            return false;

        IMediationConfig iMediationConfig = getMediationConfig(strKey);
        if (null == iMediationConfig)
            return false;

        List<AdBean> listAdBean = mMapMediationCache.get(iMediationConfig);
        return (null != listAdBean) && (!listAdBean.isEmpty());
    }

    @Override
    public boolean isAdLoading(String strKey) {
        if (TextUtils.isEmpty(strKey))
            return false;

        IMediationConfig iMediationConfig = getMediationConfig(strKey);
        if (null == iMediationConfig)
            return false;

        if (!mMapMediationRequestCount.containsKey(iMediationConfig))
            return false;

        return mMapMediationRequestCount.get(iMediationConfig) > 0;
    }

    @Override
    public boolean requestAdAsync(String strKey, String strScene) {
        return requestAdAsync(strKey, strScene, null, null);
    }

    @Override
    public boolean requestAdAsync(String strKey, String strScene, int[] arrayResLayoutID, Size size) {
        if (TextUtils.isEmpty(strKey) || TextUtils.isEmpty(strScene))
            return false;

        IMediationConfig iMediationConfig = getMediationConfig(strKey);

        if (null == iMediationConfig) {
            UtilsAd.logE("rick-requestAdAsync-s", "iMediationConfig = null");
            return false;
        }
        if(iMediationConfig.getAdKey().equals("view_alert")) {
            UtilsAd.logE("rick-requestAdAsync-s", iMediationConfig.getAdKey());
        }
        if (!iMediationConfig.isSupportRequestScene(strScene))
            return false;

        if (mMapMediationRequestCount.containsKey(iMediationConfig) && mMapMediationRequestCount.get(iMediationConfig) > 0)
            return false;

        boolean bResult = false;
        for (int nIndex = getMediationRequestAndLoadedCount(iMediationConfig); nIndex < iMediationConfig.getCacheCount(); nIndex++) {
            String strKeyTime = strKey + (System.currentTimeMillis() + nIndex);
            mMapAdRequestIndex.put(strKeyTime, 0);
            if (requestAd(iMediationConfig, strKeyTime, arrayResLayoutID, size)) {
                if (mMapMediationRequestCount.containsKey(iMediationConfig)) {
                    mMapMediationRequestCount.put(iMediationConfig, mMapMediationRequestCount.get(iMediationConfig) + 1);
                } else {
                    mMapMediationRequestCount.put(iMediationConfig, 1);
                }

                bResult = true;
            }
        }

        return bResult;
    }

    private boolean requestAd(IMediationConfig iMediationConfig, String strKeyTime, int[] arrayResLayoutID, Size size) {
        UtilsAd.logE("rick-requestAd-逻辑", Log.getStackTraceString(new Exception()));
        if (null == iMediationConfig || TextUtils.isEmpty(strKeyTime) || !mMapAdRequestIndex.containsKey(strKeyTime))
            return false;
        UtilsAd.logE("rick-requestAd-逻辑-1",iMediationConfig.getAdKey() );
        boolean bResult = false;
        while (true) {
            IAdItem iAdItem = iMediationConfig.getAdItem(mMapAdRequestIndex.get(strKeyTime));
            if (null == iAdItem)
                break;

            mMapAdRequestIndex.put(strKeyTime, mMapAdRequestIndex.get(strKeyTime) + 1);

            // 检查无效流量
            if (UtilsAd.isSpamUser(CMMediationFactory.getApplication(), iMediationConfig.getAdKey(), iAdItem.getAdPlatform(), iAdItem.getAdID(), iAdItem.getAdType()))
                continue;

            // 若有不支持的广告变现平台直接跳到下一个AdItem广告请求
            IAdPlatformMgr iAdPlatformMgr = mMapAdPlatformMgr.get(iAdItem.getAdPlatform());
            if (null == iAdPlatformMgr)
                continue;

            switch (iAdItem.getAdType()) {
                case IMediationConfig.VALUE_STRING_TYPE_BANNER:
                    bResult = iAdPlatformMgr.requestBannerAdAsync(iMediationConfig.getAdKey(), iAdItem.getAdID(), iAdItem.getAdBannerSize(), size, new AdPlatformListener(iMediationConfig, iAdItem, strKeyTime, arrayResLayoutID, size));
                    break;
                case IMediationConfig.VALUE_STRING_TYPE_NATIVE:
                    bResult = iAdPlatformMgr.requestNativeAdAsync(iMediationConfig.getAdKey(), iAdItem.getAdID(), arrayResLayoutID, new AdPlatformListener(iMediationConfig, iAdItem, strKeyTime, arrayResLayoutID, size));
                    break;
                case IMediationConfig.VALUE_STRING_TYPE_INTERSTITIAL:
                    bResult = iAdPlatformMgr.requestInterstitialAdAsync(iMediationConfig.getAdKey(), iAdItem.getAdID(), new AdPlatformListener(iMediationConfig, iAdItem, strKeyTime, arrayResLayoutID, size));
                    break;
            }

            if (bResult)
                break;
        }

        return bResult;
    }

    private int getMediationRequestAndLoadedCount(IMediationConfig iMediationConfig) {
        if (null == iMediationConfig)
            return 0;

        int nMediationConfigRequestNoLoadedCount = 0;
        if (mMapMediationRequestCount.containsKey(iMediationConfig)) {
            nMediationConfigRequestNoLoadedCount = mMapMediationRequestCount.get(iMediationConfig);
        }

        if (mMapMediationCache.containsKey(iMediationConfig)) {
            List<AdBean> listAdBean = mMapMediationCache.get(iMediationConfig);
            if (null != listAdBean) {
                nMediationConfigRequestNoLoadedCount += listAdBean.size();
            }
        }

        return nMediationConfigRequestNoLoadedCount;
    }

    @Override
    public boolean showAdView(String strKey, ViewGroup VGContainer) {
        return showAdView(strKey, VGContainer, null);
    }

    @Override
    public boolean showAdView(String strKey, ViewGroup VGContainer, int[] arrayResLayoutID) {
        if (TextUtils.isEmpty(strKey) || null == VGContainer)
            return false;

        IMediationConfig iMediationConfig = getMediationConfig(strKey);
        if (null == iMediationConfig)
            return false;

        List<AdBean> listAdBean = mMapMediationCache.get(iMediationConfig);
        if (null == listAdBean || listAdBean.isEmpty())
            return false;

        AdBean adBean = listAdBean.remove(0);
        if (null == adBean || null == adBean.mIAdItem)
            return false;

        if (mMapMediationAdViewUsedCache.containsKey(iMediationConfig)) {
            List<AdBean> listAdBeanUsed = mMapMediationAdViewUsedCache.get(iMediationConfig);
            if (null != listAdBeanUsed) {
                listAdBeanUsed.add(adBean);
            }
        } else {
            List<AdBean> listAdBeanUsed = new ArrayList<>();
            listAdBeanUsed.add(adBean);
            mMapMediationAdViewUsedCache.put(iMediationConfig, listAdBeanUsed);
        }

        String strPlatform = adBean.mIAdItem.getAdPlatform();
        if (TextUtils.isEmpty(strPlatform))
            return false;

        IAdPlatformMgr iAdPlatformMgr = mMapAdPlatformMgr.get(strPlatform);
        if (null == iAdPlatformMgr)
            return false;

        switch (adBean.mIAdItem.getAdType()) {
            case IMediationConfig.VALUE_STRING_TYPE_BANNER:
                return iAdPlatformMgr.showBannerAd(adBean, VGContainer);
            case IMediationConfig.VALUE_STRING_TYPE_NATIVE:
                return iAdPlatformMgr.showNativeAd(adBean, VGContainer, arrayResLayoutID);
        }

        return false;
    }

    @Override
    public boolean showInterstitialAd(String strKey, String strScene) {
        if (TextUtils.isEmpty(strKey) || TextUtils.isEmpty(strScene))
            return false;

        IMediationConfig iMediationConfig = getMediationConfig(strKey);
        if (null == iMediationConfig)
            return false;

        if (!iMediationConfig.isSupportShowScene(strScene))
            return false;

        List<AdBean> listAdBean = mMapMediationCache.get(iMediationConfig);
        if (null == listAdBean || listAdBean.isEmpty())
            return false;

        AdBean adBean = listAdBean.remove(0);
        if (null == adBean || null == adBean.mIAdItem)
            return false;

        if (mMapMediationAdViewUsedCache.containsKey(iMediationConfig)) {
            List<AdBean> listAdBeanUsed = mMapMediationAdViewUsedCache.get(iMediationConfig);
            if (null != listAdBeanUsed) {
                listAdBeanUsed.add(adBean);
            }
        } else {
            List<AdBean> listAdBeanUsed = new ArrayList<>();
            listAdBeanUsed.add(adBean);
            mMapMediationAdViewUsedCache.put(iMediationConfig, listAdBeanUsed);
        }

        String strPlatform = adBean.mIAdItem.getAdPlatform();
        if (TextUtils.isEmpty(strPlatform))
            return false;

        IAdPlatformMgr iAdPlatformMgr = mMapAdPlatformMgr.get(strPlatform);
        if (null == iAdPlatformMgr)
            return false;

        return iAdPlatformMgr.showInterstitialAd(adBean);
    }

    @Override
    public boolean releaseAd(String strKey) {
        if (TextUtils.isEmpty(strKey))
            return false;

        IMediationConfig iMediationConfig = getMediationConfig(strKey);
        if (null == iMediationConfig)
            return false;

        if (!mMapMediationAdViewUsedCache.containsKey(iMediationConfig))
            return false;

        List<AdBean> listAdBeanUsed = mMapMediationAdViewUsedCache.get(iMediationConfig);
        return destroyAd(listAdBeanUsed);
    }

    @Override
    public boolean releaseAllAd() {
        if (null == mMapMediationAdViewUsedCache)
            return false;

        for (Map.Entry<IMediationConfig, List<AdBean>> entry : mMapMediationAdViewUsedCache.entrySet()) {
            destroyAd(entry.getValue());
        }

        return true;
    }

    private boolean destroyAd(List<AdBean> listAdBeanUsed) {
        if (null == listAdBeanUsed)
            return false;

        for (AdBean adBean : listAdBeanUsed) {
            if (null == adBean || null == adBean.mIAdItem)
                continue;

            String strPlatform = adBean.mIAdItem.getAdPlatform();
            if (TextUtils.isEmpty(strPlatform))
                continue;

            IAdPlatformMgr iAdPlatformMgr = mMapAdPlatformMgr.get(strPlatform);
            if (null == iAdPlatformMgr)
                continue;

            iAdPlatformMgr.releaseAd(adBean);
        }

        listAdBeanUsed.clear();
        return true;
    }

    private IMediationConfig getMediationConfig(String strKey) {
        if (TextUtils.isEmpty(strKey))
            return null;

        return mMapMediationConfig.get(strKey);
    }

    private class AdPlatformListener extends IAdPlatformMgrListener {
        private IMediationConfig mIMediationConfig = null;
        private IAdItem mIAdItem = null;
        private String mKeyTime = null;
        private int[] mArrayResLayoutID = null;
        private Size mSize = null;

        public AdPlatformListener(IMediationConfig iMediationConfig, IAdItem iAdItem, String strKeyTime, int[] arrayResLayoutID, Size size) {
            mIMediationConfig = iMediationConfig;
            mIAdItem = iAdItem;
            mKeyTime = strKeyTime;
            mArrayResLayoutID = arrayResLayoutID;
            mSize = size;
        }

        @Override
        public void onAdLoaded(Object objectAd, String strAdKey, String strAdRequestID) {
            List<AdBean> listAdBean = null;
            if (mMapMediationCache.containsKey(mIMediationConfig)) {
                listAdBean = mMapMediationCache.get(mIMediationConfig);
            } else {
                listAdBean = new ArrayList<>();
                mMapMediationCache.put(mIMediationConfig, listAdBean);
            }

            // 防止Banner自动刷新
            if (null != mKeyTime) {
                listAdBean.add(new AdBean(mIAdItem, objectAd, strAdKey, strAdRequestID));
                mMapAdRequestIndex.remove(mKeyTime);
                mMapMediationRequestCount.put(mIMediationConfig, mMapMediationRequestCount.get(mIMediationConfig) - 1);
                if (mMapMediationRequestCount.get(mIMediationConfig) > 0)
                    return;
            }

            mKeyTime = null;
            for (IMediationMgrListener listener : getListenerList()) {
                listener.onAdLoaded(mIMediationConfig);
            }
        }

        @Override
        public void onAdFailed(int nCode) {
            if (requestAd(mIMediationConfig, mKeyTime, mArrayResLayoutID, mSize))
                return;

            // 防止Banner自动刷新
            if (null != mKeyTime) {
                mMapAdRequestIndex.remove(mKeyTime);
                mMapMediationRequestCount.put(mIMediationConfig, mMapMediationRequestCount.get(mIMediationConfig) - 1);
                if (mMapMediationRequestCount.get(mIMediationConfig) > 0)
                    return;
            }

            mKeyTime = null;
            for (IMediationMgrListener listener : getListenerList()) {
                listener.onAdFailed(mIMediationConfig, nCode);
            }
        }

        @Override
        public void onAdImpression() {
            for (IMediationMgrListener listener : getListenerList()) {
                listener.onAdImpression(mIMediationConfig);
            }
        }

        @Override
        public void onAdComplete() {
            for (IMediationMgrListener listener : getListenerList()) {
                listener.onAdComplete(mIMediationConfig);
            }
        }

        @Override
        public void onAdClicked() {
            // 检查无效流量
            if (null != mIMediationConfig && null != mIAdItem) {
                UtilsAd.updateSpamUser(CMMediationFactory.getApplication(), mIMediationConfig.getAdKey(), mIAdItem.getAdPlatform(), mIAdItem.getAdID(), mIAdItem.getAdType());
            }

            for (IMediationMgrListener listener : getListenerList()) {
                listener.onAdClicked(mIMediationConfig);
            }
        }
    }
}
