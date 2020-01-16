package com.dema.versatile.logic.core.config.im;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.im.CMABTest;
import com.dema.versatile.lib.core.im.CMObserverIntelligence;
import com.dema.versatile.lib.core.in.ICMABTest;
import com.dema.versatile.lib.core.in.ICMHttp;
import com.dema.versatile.lib.core.in.ICMHttpListener;
import com.dema.versatile.lib.core.in.ICMHttpResult;
import com.dema.versatile.lib.core.in.ICMThreadPool;
import com.dema.versatile.lib.core.in.ICMThreadPoolListener;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsFile;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.utils.UtilsNetwork;
import com.dema.versatile.lib.utils.UtilsTime;
import com.dema.versatile.logic.CMLogicFactory;
import com.dema.versatile.logic.core.config.in.IConfigMgr;
import com.dema.versatile.logic.core.config.in.IConfigMgrListener;
import com.dema.versatile.logic.utils.UtilsLogic;
import com.dema.versatile.mediation.core.im.MediationMgr;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.mediation.utils.UtilsAd;
import com.dema.versatile.scene.core.scene.ISceneMgr;
import com.dema.versatile.scene.core.scene.SceneMgrImpl;

public class ConfigMgr extends CMObserverIntelligence<IConfigMgrListener> implements IConfigMgr {
    private Context mContext = null;
    private ICMThreadPool mICMThreadPool = null;
    private ICMHttp mICMHttp = null;
    private boolean mIsConfigLoaded = false;
    private boolean mIsConfigLoading = false;
    private JSONObject mJSONObjectConfig = null;
    private JSONObject mAdJsonConfig = null;
    private JSONObject mAbTestConfig = null;

    private static final String VALUE_STRING_CONFIG_FILE = "config.dat";
    private static final String VALUE_STRING_AD_FILE = "ad.dat";
    private static final String VALUE_STRING_AB_TEST_FILE = "ab_test.dat";

    public ConfigMgr() {
        mContext = CMLogicFactory.getApplication();
        _init();
    }

    private void _init() {
        mICMThreadPool = CMLibFactory.getInstance().createInstance(ICMThreadPool.class);
        mICMHttp = CMLibFactory.getInstance().createInstance(ICMHttp.class);
    }

    @Override
    public boolean isConfigLoaded() {
        return mIsConfigLoaded;
    }

    @Override
    public boolean isConfigLoading() {
        return mIsConfigLoading;
    }

    @Override
    public JSONObject getConfig() {
        return mJSONObjectConfig;
    }

    @Override
    public JSONObject getAdConfig() {
        return mAdJsonConfig;
    }

    @Override
    public JSONObject getABTestConfig() {
        return mAbTestConfig;
    }

    @Override
    public boolean loadConfigAsync() {
        if (!UtilsFile.isExists(UtilsFile.makePath(mContext.getFilesDir().getAbsolutePath(), VALUE_STRING_CONFIG_FILE)))
            return false;

        if (mIsConfigLoading)
            return false;

        mIsConfigLoading = true;
        final boolean[] arrayIsLoaded = new boolean[]{false};
        mICMThreadPool.run(new ICMThreadPoolListener() {
            @Override
            public void onRun() {
                JSONObject jsonObjectConfig = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_CONFIG_FILE);
                JSONObject jsonObjectAd = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_AD_FILE);
                JSONObject jsonObjectABTest = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_AB_TEST_FILE);
                arrayIsLoaded[0] = loadConfig(jsonObjectConfig, jsonObjectAd, jsonObjectABTest, false);

                JSONObject jsonObjectDebug = new JSONObject();
                try {
                    jsonObjectDebug.put("success", arrayIsLoaded[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.log("debug", "load_config", jsonObjectDebug);
            }

            @Override
            public void onComplete() {
                mIsConfigLoaded = arrayIsLoaded[0];
                mIsConfigLoading = false;

                for (IConfigMgrListener listener : getListenerList())
                    listener.onLoadConfigAsyncComplete(arrayIsLoaded[0]);
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }

    @Override
    public boolean loadConfigSync() {
        boolean isNoExist = !UtilsFile.isExists(UtilsFile.makePath(mContext.getFilesDir().getAbsolutePath(), VALUE_STRING_CONFIG_FILE));

        UtilsAd.logE("rick-loadConfigSync:"+isNoExist+","+mIsConfigLoading,"");
        if (isNoExist)
            return false;

        if (mIsConfigLoading)
            return false;


        JSONObject jsonObjectConfig = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_CONFIG_FILE);
        JSONObject jsonObjectAd = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_AD_FILE);
        JSONObject jsonObjectABTest = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_AB_TEST_FILE);
        mIsConfigLoaded = loadConfig(jsonObjectConfig, jsonObjectAd, jsonObjectABTest, false);

        JSONObject jsonObjectDebug = new JSONObject();
        try {
            jsonObjectDebug.put("success", mIsConfigLoaded);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UtilsLog.log("debug", "load_config-local", jsonObjectDebug);

        for (IConfigMgrListener listener : getListenerList())
            listener.onLoadConfigAsyncComplete(mIsConfigLoaded);

        return mIsConfigLoaded;
    }

    @Override
    public boolean requestConfigAsync(boolean bIsIgnoreProtectTime) {
        if (TextUtils.isEmpty(UtilsLogic.sConfigURL))
            return false;

        if (mIsConfigLoading)
            return false;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        long lRequestConfigTime = sp.getLong("request_config_time", 0);
        long lTime = System.currentTimeMillis();
        if (!bIsIgnoreProtectTime && (lTime - lRequestConfigTime < UtilsTime.VALUE_LONG_TIME_ONE_HOUR))
            return false;

        mIsConfigLoading = true;
        final boolean[] arrayIsLoaded = new boolean[]{false};
        mICMThreadPool.run(new ICMThreadPoolListener() {
            @Override
            public void onRun() {
                JSONObject jsonObjectParam = UtilsNetwork.getBasicRequestParam(mContext);
                if (null == jsonObjectParam)
                    return;

                Map<String, String> mapParam = new HashMap<>();
                mapParam.put("basic", jsonObjectParam.toString());
                UtilsLog.logD("rick-config-mapParam", jsonObjectParam.toString());
                ICMHttpResult iCMHttpResult = mICMHttp.requestToBufferByPostSync(UtilsNetwork.getURL(UtilsLogic.sConfigURL), mapParam, null,false);
                if (null == iCMHttpResult || !iCMHttpResult.isSuccess() || null == iCMHttpResult.getBuffer()) {
                    JSONObject jsonObjectDebug = new JSONObject();
                    try {
                        jsonObjectDebug.put("success", false);
                        String exception = "";
                        if (iCMHttpResult == null) {
                            exception = "null";
                        } else if (!TextUtils.isEmpty(iCMHttpResult.getException())) {
                            exception = iCMHttpResult.getException();
                            if (exception.length() > 300) {
                                exception = exception.substring(0, 300);
                            }
                        }
                        jsonObjectDebug.put("exception", exception);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String defaultConfig = "{\"ab_test\":{},\"config\":{\"scene\":{\"default\":{\"time\":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23],\"force\":true,\"scene\":[\"pull_boost\",\"pull_battery\",\"pull_cool\",\"pull_clean\",\"pull_network\"],\"trigger\":[\"run\",\"alarm\"],\"mutex_time\":12,\"range_time\":50,\"sleep_time\":60,\"protect_time\":{\"pull_cool\":60,\"pull_boost\":60,\"pull_clean\":60,\"pull_battery\":60,\"pull_network\":60},\"show_with_ad\":true,\"alarm_notification\":false},\"scene_list\":[{\"key\":\"run_alarm_boost\",\"time\":[0,5,10,15,20],\"scene\":[\"pull_boost\",\"pull_boost\",\"pull_boost\",\"pull_boost\"],\"trigger\":[\"run\",\"alarm\"]},{\"key\":\"run_alarm_clean\",\"time\":[1,6,11,16,21],\"scene\":[\"pull_clean\",\"pull_clean\",\"pull_clean\",\"pull_clean\"],\"trigger\":[\"run\",\"alarm\"]},{\"key\":\"run_alarm_battery\",\"time\":[2,7,12,17,22],\"scene\":[\"pull_battery\",\"pull_battery\",\"pull_battery\",\"pull_battery\"],\"trigger\":[\"run\",\"alarm\"]},{\"key\":\"run_alarm_cool\",\"time\":[3,8,13,18,23],\"scene\":[\"pull_cool\",\"pull_cool\",\"pull_cool\",\"pull_cool\"],\"trigger\":[\"run\",\"alarm\"]},{\"key\":\"run_alarm_cool\",\"time\":[4,9,14,19],\"scene\":[\"pull_network\",\"pull_network\",\"pull_network\",\"pull_network\"],\"trigger\":[\"run\",\"alarm\"]},{\"key\":\"call_end_boost\",\"scene\":[\"pull_boost\",\"pull_boost\",\"pull_boost\",\"pull_boost\"],\"trigger\":[\"call_end\"]},{\"key\":\"charge_cool\",\"scene\":[\"pull_cool\",\"pull_cool\",\"pull_cool\",\"pull_cool\"],\"trigger\":[\"charge_start\",\"charge_end\"]},{\"key\":\"clean_lock\",\"scene\":[\"pull_clean\",\"pull_clean\",\"pull_clean\",\"pull_clean\"],\"trigger\":[\"unlock\"]},{\"key\":\"network\",\"scene\":[\"pull_network\",\"pull_network\",\"pull_network\",\"pull_network\"],\"trigger\":[\"network\"]},{\"key\":\"clean_app\",\"scene\":[\"pull_clean\",\"pull_clean\",\"pull_clean\",\"pull_clean\"],\"trigger\":[\"app_install\",\"app_update\",\"app_uninstall\"]}]},\"config\":{\"cool\":{\"protect_time\":10},\"boost\":{\"protect_time\":10},\"clean\":{\"protect_time\":10},\"battery\":{\"protect_time\":10},\"network\":{\"protect_time\":10},\"optimize\":{\"protect_time\":10},\"deep_clean\":{\"protect_time\":10}}},\"ad\":{\"interstitial_result\":{\"show_scene\":[\"animation_cancel\",\"animation_complete\",\"result_back\",\"cancel\",\"complete\",\"back\"],\"cache_count\":1,\"request_scene\":[\"animation_create\",\"splash\",\"application\",\"scene\",\"main_create\"],\"ad_list\":[{\"mask_rate\":0,\"type\":\"interstitial\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/1644611168\"},{\"mask_rate\":0,\"type\":\"interstitial\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/3037583882\"},{\"mask_rate\":0,\"type\":\"interstitial\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/4921511193\"}]},\"interstitial_exit\":{\"show_scene\":[\"main_exit\"],\"cache_count\":1,\"request_scene\":[\"application\",\"main_create\"],\"ad_list\":[{\"mask_rate\":0,\"type\":\"interstitial\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/1644611168\"},{\"mask_rate\":0,\"type\":\"interstitial\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/3037583882\"},{\"mask_rate\":0,\"type\":\"interstitial\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/4921511193\"}]},\"view_main\":{\"show_scene\":[],\"cache_count\":1,\"request_scene\":[\"splash\",\"application\",\"main_create\"],\"ad_list\":[{\"banner_size\":\"large\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"inmobi\",\"id\":\"1571337392472\"},{\"banner_size\":\"small\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"mopub\",\"id\":\"7df5a9bb1d7d4cf1a89cbf8dd31d6416\"},{\"banner_size\":\"small\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/4239463843\"},{\"banner_size\":\"small\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/6321372718\"},{\"banner_size\":\"small\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/6101880861\"}]},\"view_alert\":{\"show_scene\":[],\"cache_count\":1,\"request_scene\":[\"scene\",\"main_create\"],\"ad_list\":[{\"banner_size\":\"large\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/7802825404\"},{\"banner_size\":\"large\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/4653067824\"},{\"banner_size\":\"large\",\"mask_rate\":0,\"type\":\"banner\",\"platform\":\"admob\",\"id\":\"ca-app-pub-9195446338192028/6924858004\"}]}}}";
                    try {
                        JSONObject jsonObjectData = new JSONObject(defaultConfig);
                        JSONObject jsonObjectConfig = jsonObjectData.getJSONObject("config");
                        JSONObject jsonObjectAd = jsonObjectData.getJSONObject("ad");
                        JSONObject jsonObjectABTest = jsonObjectData.getJSONObject("ab_test");
                        arrayIsLoaded[0] = loadConfig(jsonObjectConfig, jsonObjectAd, jsonObjectABTest, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UtilsLog.log("debug", "request_config-error", jsonObjectDebug);
                    return;
                }

                int nCode = UtilsNetwork.VALUE_INT_FAIL_CODE;
                JSONObject jsonObject = null;
                JSONObject jsonObjectData = null;
                try {
                    jsonObject = new JSONObject(new String(iCMHttpResult.getBuffer()));
                    nCode = UtilsJson.JsonUnserialization(jsonObject, "code", nCode);
                    jsonObjectData = jsonObject.getJSONObject("data");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode) {
                    try {
                        JSONObject jsonObjectConfig = jsonObjectData.getJSONObject("config");
                        JSONObject jsonObjectAd = jsonObjectData.getJSONObject("ad");
                        JSONObject jsonObjectABTest = jsonObjectData.getJSONObject("ab_test");
                        arrayIsLoaded[0] = loadConfig(jsonObjectConfig, jsonObjectAd, jsonObjectABTest, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                JSONObject jsonObjectDebug = new JSONObject();
                try {
                    jsonObjectDebug.put("success", iCMHttpResult.isSuccess());
                    jsonObjectDebug.put("exception", iCMHttpResult.getException());
                    jsonObjectDebug.put("content", new String(iCMHttpResult.getBuffer()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.log("debug", "request_config", jsonObjectDebug);
            }

            @Override
            public void onComplete() {
                if (arrayIsLoaded[0]) {
                    sp.edit().putLong("request_config_time", lTime).apply();
                }

                mIsConfigLoaded = arrayIsLoaded[0];
                mIsConfigLoading = false;

                for (IConfigMgrListener listener : getListenerList())
                    listener.onRequestConfigAsyncComplete(arrayIsLoaded[0]);
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }

    private boolean loadConfig(JSONObject jsonObjectConfig, JSONObject jsonObjectAd, JSONObject jsonObjectABTest, boolean bIsNeedSave) {
        try {
            UtilsAd.logE("rick-loadConfig-写入", Log.getStackTraceString(new Exception()));
            // 保存config信息
            if (null != jsonObjectConfig) {
                mJSONObjectConfig = jsonObjectConfig;

                // 反序列化scene信息
                UtilsJson.JsonUnserialization(jsonObjectConfig, "scene", ISceneMgr.class, SceneMgrImpl.class);

                if (bIsNeedSave) {
                    UtilsJson.saveJsonToFileWithEncrypt(mContext, VALUE_STRING_CONFIG_FILE, jsonObjectConfig);
                }
            }

            // 反序列化广告
            if (null != jsonObjectAd) {
                mAdJsonConfig = jsonObjectAd;
                UtilsJson.JsonUnserialization(jsonObjectAd, IMediationMgr.class, MediationMgr.class);

                if (bIsNeedSave) {
                    UtilsJson.saveJsonToFileWithEncrypt(mContext, VALUE_STRING_AD_FILE, jsonObjectAd);
                }
            }

            // 反序列化ABTest
            if (null != jsonObjectABTest) {
                mAbTestConfig = jsonObjectABTest;
                if (bIsNeedSave) {
                    CMABTest cmABTestNew = new CMABTest();
                    cmABTestNew.Deserialization(jsonObjectABTest);
                    JSONObject jsonObjectABTestOld = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_AB_TEST_FILE);
                    CMABTest cmABTestOld = new CMABTest();
                    cmABTestOld.Deserialization(jsonObjectABTestOld);
                    if (cmABTestOld.getHitAppVersion() < cmABTestNew.getHitAppVersion()) {
                        UtilsJson.JsonUnserialization(jsonObjectABTest, ICMABTest.class, CMABTest.class);
                        UtilsJson.saveJsonToFileWithEncrypt(mContext, VALUE_STRING_AB_TEST_FILE, jsonObjectABTest);
                    }
                } else {
                    CMABTest cmABTestOld = new CMABTest();
                    cmABTestOld.Deserialization(jsonObjectABTest);
                    if (!TextUtils.isEmpty(cmABTestOld.getHitKey())) {
                        UtilsJson.JsonUnserialization(jsonObjectABTest, ICMABTest.class, CMABTest.class);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean requestCountryAsync() {
        String strCountry = UtilsEnv.getIPCountry(mContext);
        if (!TextUtils.isEmpty(strCountry))
            return false;

        if (TextUtils.isEmpty(UtilsLogic.sCountryURL))
            return false;

        mICMHttp.requestToBufferByPostAsync(UtilsNetwork.getURL(UtilsLogic.sCountryURL), null, null, null, new ICMHttpListener() {
            @Override
            public void onRequestToBufferByPostAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, ICMHttpResult iCMHttpResult) {
                if (null == iCMHttpResult || !iCMHttpResult.isSuccess() || null == iCMHttpResult.getBuffer()) {
                    JSONObject jsonObjectDebug = new JSONObject();
                    try {
                        jsonObjectDebug.put("success", false);
                        jsonObjectDebug.put("exception", iCMHttpResult == null ? "null" : iCMHttpResult.getException());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    UtilsLog.log("debug", "request_country", jsonObjectDebug);
                    return;
                }

                int nCode = UtilsNetwork.VALUE_INT_FAIL_CODE;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new String(iCMHttpResult.getBuffer()));
                    nCode = UtilsJson.JsonUnserialization(jsonObject, "code", nCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode) {
                    try {
                        if (jsonObject.has("data")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String strCountry = data.getString("country");
                            UtilsEnv.setIPCountry(mContext, strCountry);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                JSONObject jsonObjectDebug = new JSONObject();
                try {
                    jsonObjectDebug.put("success", iCMHttpResult.isSuccess());
                    jsonObjectDebug.put("exception", iCMHttpResult.getException());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.log("debug", "request_country", jsonObjectDebug);
            }
        });

        return true;
    }
}
