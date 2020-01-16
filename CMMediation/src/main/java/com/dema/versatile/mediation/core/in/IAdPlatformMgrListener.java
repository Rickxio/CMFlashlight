package com.dema.versatile.mediation.core.in;

public abstract class IAdPlatformMgrListener {
    public void onAdLoaded(Object objectAd, String strAdKey, String strAdRequestID) {
    }

    public void onAdFailed(int nCode) {
    }

    public void onAdImpression() {
    }

    public void onAdComplete() {
    }

    public void onAdClicked() {
    }
}
