package com.dema.versatile.flashlight;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dema.versatile.flashlight.main.MainActivity;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.logic.tool.CMSplashActivity;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.mediation.utils.UtilsAd;

public class SplashActivity extends CMSplashActivity {
    @Override
    protected long getWaitTime() {
        return 1800;
    }

    @Override
    protected void requestAd() {
        UtilsAd.logE("rick-requestAd", Log.getStackTraceString(new Exception()));
        IMediationMgr iMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        iMediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_EXIT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_SPLASH);
        iMediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_SPLASH);

    }

    @Override
    protected void nextStep() {
        Intent intent = new Intent(this, MainActivity.class);
        if (getScene() != null) {
            intent.putExtra(MainActivity.EXTRA_SCENE, getScene());
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        UtilsLog.log("splash", "create", null);

    }
}
