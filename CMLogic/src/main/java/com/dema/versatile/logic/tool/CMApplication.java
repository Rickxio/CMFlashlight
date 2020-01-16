package com.dema.versatile.logic.tool;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.dema.versatile.lib.utils.UtilsApp;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsInstall;
import com.dema.versatile.lib.utils.UtilsSystem;
import com.dema.versatile.logic.CMLogicFactory;
import com.dema.versatile.logic.core.config.in.IConfigMgr;
import com.dema.versatile.logic.core.config.in.IConfigMgrListener;
import com.dema.versatile.logic.utils.UtilsLogic;
import com.dema.versatile.mediation.utils.UtilsAd;

/**
 * 此类主要用途
 * 1.封装CMLib，CMMediation相关类初始化工作
 * 2.配合CMSplashActivity封装Config逻辑
 */
public abstract class CMApplication extends Application {
    private static CMApplication sApplication = null;
    private static IConfigMgr sIConfigMgr = null;
    private static IConfigMgrListener sIConfigMgrListener = null;

    public static CMApplication getApplication() {
        return sApplication;
    }

    protected abstract void initApplication();

    // 解析云控逻辑
    protected abstract void loadConfig();

    // 广告预加载逻辑
    protected abstract void requestAd();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        setWebViewPath(base);
    }

    public void setWebViewPath(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                String strProcessName = UtilsSystem.getMyProcessName(context);
                if (!TextUtils.equals(strProcessName, UtilsApp.getMyAppPackageName(context))) {
                    WebView.setDataDirectorySuffix(strProcessName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication = this;

        // 初始化UtilsLogic参数
        initApplication();

        // 使用UtilsLogic参数进行初始化
        UtilsLogic.init(this);

        // 主进程
        if (UtilsSystem.isMainProcess(this)) {
            sIConfigMgr = CMLogicFactory.getInstance().createInstance(IConfigMgr.class);
            sIConfigMgrListener = new IConfigMgrListener() {
                @Override
                public void onLoadConfigAsyncComplete(boolean bIsLoaded) {
                }

                @Override
                public void onRequestConfigAsyncComplete(boolean bIsLoaded) {
                    sIConfigMgr.removeListener(sIConfigMgrListener);
                    // 新装用户预加载广告
                    if (UtilsInstall.VALUE_INT_INSTALL_NEW_TYPE == UtilsInstall.getInstallType()) {
                        requestAd();
                    }
                    UtilsAd.logE("rick-解析云控逻辑", Log.getStackTraceString(new Exception()));
                    // 解析云控逻辑
                    loadConfig();
                }
            };

            if (UtilsInstall.VALUE_INT_INSTALL_NEW_TYPE == UtilsInstall.getInstallType()
                    || UtilsInstall.VALUE_INT_INSTALL_UPDATE_TYPE == UtilsInstall.getInstallType()) {
                UtilsAd.logE("rick-新装或更新用户提前请求配置", Log.getStackTraceString(new Exception()));
                // 新装或更新用户提前请求配置 忽略保护时间
                if (sIConfigMgr.requestConfigAsync(true)) {
                    sIConfigMgr.addListener(sIConfigMgrListener);
                }
            } else {
                UtilsAd.logE("rick-读取本地缓存配置", Log.getStackTraceString(new Exception()));
                // 非新增或更新用户优先读取本地缓存配置
                 sIConfigMgr.loadConfigSync();
                // 解析云控逻辑
                loadConfig();
                // 请求配置 判断保护时间
                if (sIConfigMgr.requestConfigAsync(false)) {
                    sIConfigMgr.addListener(sIConfigMgrListener);
                }
            }

            // 请求国家信息
            if (TextUtils.isEmpty(UtilsEnv.getIPCountry(this))) {
                sIConfigMgr.requestCountryAsync();
            }
        }
    }
}
