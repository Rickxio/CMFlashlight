package com.dema.versatile.mediation.core.in;

public abstract class IMediationMgrListener {
    public void onAdLoaded(IMediationConfig iMediationConfig) {
    }

    public void onAdFailed(IMediationConfig iMediationConfig, int nCode) {
    }

    public void onAdImpression(IMediationConfig iMediationConfig) {
    }

    public void onAdComplete(IMediationConfig iMediationConfig) {
    }

    public void onAdClicked(IMediationConfig iMediationConfig) {
    }
}
