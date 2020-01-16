package com.dema.versatile.flashlight;

import android.util.Log;

import com.dema.versatile.flashlight.core.CoreFactory;
import com.dema.versatile.flashlight.core.alarm.IAppAlarmMgr;
import com.dema.versatile.flashlight.core.config.SceneAlertConfig1;
import com.dema.versatile.flashlight.core.config.intf.ICloudConfig;
import com.dema.versatile.flashlight.core.settings.ISettingMgr;
import com.dema.versatile.lib.utils.UtilsInstall;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsSystem;
import com.dema.versatile.logic.CMLogicFactory;
import com.dema.versatile.logic.core.config.in.IConfigMgr;
import com.dema.versatile.logic.tool.CMApplication;
import com.dema.versatile.logic.utils.UtilsLogic;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.mediation.utils.UtilsAd;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.alert.AlertInfoBean;
import com.dema.versatile.scene.core.scene.IAlertConfig;
import com.dema.versatile.scene.core.scene.INotificationConfig;
import com.dema.versatile.scene.core.scene.ISceneCallback;
import com.dema.versatile.scene.core.scene.ISceneMgr;

import org.json.JSONObject;

public class MainApplication extends CMApplication {
    @Override
    protected void initApplication() {
        UtilsLogic.sDomain = "api.shalacai.com:1224";
        UtilsLogic.sKeySecret = "a94f1167a8142ba2769d70144b409f";
        UtilsLogic.sConfigURL = "/config";
        UtilsLogic.sCountryURL = "";
        UtilsLogic.sLogURL = "/log";
        UtilsLogic.sCrashURL = "/api/v7/crash/" + BuildConfig.APPLICATION_ID;

        // 使用应用级BuildConfig
        UtilsLogic.sChannel = BuildConfig.FLAVOR;
        UtilsLogic.sIsDebug = BuildConfig.DEBUG;

      /*  RequestManager.registerConfig(new NetConfig() {
            @Override
            public String configBaseUrl() {
                return "http://123.56.182.65:1223";
            }

            @Override
            public Interceptor[] configInterceptors() {
                return new Interceptor[0];
            }

            @Override
            public long configConnectTimeoutMills() {
                return 10 * 1000;
            }

            @Override
            public long configReadTimeoutMills() {
                return 10 * 1000;
            }

            @Override
            public boolean configLogEnable() {
                return true;
            }
        });*/
    }

    @Override
    protected void loadConfig() {
        IConfigMgr iConfigMgr = CMLogicFactory.getInstance().createInstance(IConfigMgr.class);
        JSONObject jsonObjectConfig = iConfigMgr.getConfig();
        if (null == jsonObjectConfig) {
            return;
        }
        ICloudConfig iCloudConfig = CoreFactory.getInstance().createInstance(ICloudConfig.class);
        iCloudConfig.parseConfig(jsonObjectConfig);
    }

    @Override
    protected void requestAd() {
        UtilsAd.logE("rick-requestAd-application", Log.getStackTraceString(new Exception()));

        if (UtilsInstall.VALUE_INT_INSTALL_NEW_TYPE != UtilsInstall.getInstallType()) {
            return;
        }

        IMediationMgr iMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
        iMediationMgr.requestAdAsync(AdKey.VALUE_STRING_VIEW_MAIN, AdKey.VALUE_STRING_AD_REQUEST_SCENE_APPLICATION);
        iMediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_EXIT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_APPLICATION);
        iMediationMgr.requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_RESULT, AdKey.VALUE_STRING_AD_REQUEST_SCENE_APPLICATION);
    }

    @Override
    public void onCreate() {
        CoreFactory.setApplication(this);
        UtilsJson.addFactory(CoreFactory.getInstance());
        super.onCreate();
        if (UtilsSystem.isMainProcess(this)) {
            CoreFactory.getInstance().createInstance(IAppAlarmMgr.class).init();
        }
        initScene();
    }

    private void initScene() {
        //设置alert统一的颜色样式，可在各自的alert里面重写对应的getXXX方法进行覆盖
//        AlertUiManager.getInstance()
//                .setBackgroundRes(R.drawable.bg_dialog_common)
//                .setCloseIconRes(R.drawable.ic_dialog_close)
//                .setTitleColor(Color.RED)
//                .setContentColor(Color.BLUE)
//                .setButtonBackgroundRes(R.drawable.ripple_button_background)
//                .setButtonTextColor(Color.GREEN);
        //初始化Scene库
        CMSceneFactory.setApplication(this);
        ISceneMgr iSceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
        iSceneMgr.init(new ISceneCallback() {
            @Override
            public String getAlertViewAdKey(String scene) {
                return AdKey.VALUE_STRING_VIEW_ALERT;
            }

            @Override
            public Long getWakeupTime() {
                //如果app不支持设置起床睡眠时间就返回null，返回的数值只需保证时分秒正确，不需要考虑日期，scene库获取的时候会做转化，变成当前的时间
                //需和睡眠时间同步设置，起床和睡眠时间只设置一个是不会生效的
                return null;
            }

            @Override
            public Long getSleepTime() {
                //如果app不支持设置起床睡眠时间就返回null，返回的数值只需保证时分秒正确，不需要考虑日期，scene库获取的时候会做转化，变成当前的时间
                //需和起床时间同步设置，起床和睡眠时间只设置一个是不会生效的
                return null;
            }

            @Override
            public void preLoadAd() {
                //这里可以预加载广告，会在请求alert广告的时候一起回调
                IMediationMgr mediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
                mediationMgr
                        .requestAdAsync(AdKey.VALUE_STRING_INTERSTITIAL_RESULT,
                                AdKey.VALUE_STRING_AD_REQUEST_SCENE);
            }

            @Override
            public boolean isSupportScene(String scene) {
                //定制app支持那些scene
                return CoreFactory.getInstance().createInstance(ISettingMgr.class).isSceneOpen();
            }

            @Override
            public INotificationConfig getNotificationConfig(String scene) {
                //根据不同的scene返回不同的config
                return null;
            }

            @Override
            public IAlertConfig getAlertUiConfig(String scene) {
                //没有特殊定制就返回null,这里可以根据scene定制不同场景对应不同的config，也可以把scene传到自定义的config
                //里面，在里面进行判断
//                if ("pull_drink".equals(scene)) {
//                    return new HealthAlertConfig(scene);
//                }

//                ICMABTest icmabTest = CMLibFactory.getInstance().createInstance(ICMABTest.class);
//                int hitIndex = icmabTest.getHitIndex();
//                if (BuildConfig.DEBUG) {
//                    Log.d("aaa", "getAlertUiConfig: "+hitIndex);
//                }
//                switch (hitIndex) {
//                    case 2:
//                        return new SceneAlertConfig(scene);
//                    case 3:
//                        return new SceneAlertConfig1(scene);
//                }
                return new SceneAlertConfig1(scene);
            }

            @Override
            public void onAlertShow(AlertInfoBean alertInfoBean) {

            }
        });
    }
}
