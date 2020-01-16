package com.dema.versatile.lib.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.facebook.applinks.AppLinkData;

import com.dema.versatile.lib.utils.UtilsEncrypt;
import com.dema.versatile.lib.utils.UtilsEnv;

public class ReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 本地如果有referrer 通过广告获取的referrer不覆盖通过其他方式获取的referrer
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String strReferrer = sp.getString("referrer", "");
        if (!TextUtils.isEmpty(strReferrer))
            return;

        strReferrer = intent.getStringExtra("referrer");
        if (TextUtils.isEmpty(strReferrer))
            return;

        analyzeReferrer(context, strReferrer);
    }

    public static void init(Context context) {
        if (null == context)
            return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String strReferrer = sp.getString("referrer", "");
        if (TextUtils.isEmpty(strReferrer)) {
            detectGooglePlayReferrer(context);
            detectFacebookReferrer(context);
        } else {
            UtilsEnv.setReferrer(strReferrer);
        }
    }

    private static void detectGooglePlayReferrer(final Context context) {
        try {
            InstallReferrerClient installReferrerClient = InstallReferrerClient.newBuilder(context).build();
            installReferrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    switch (responseCode) {
                        case InstallReferrerClient.InstallReferrerResponse.OK:
                            try {
                                ReferrerDetails response = installReferrerClient.getInstallReferrer();
//                                response.getReferrerClickTimestampSeconds();
//                                response.getInstallBeginTimestampSeconds();
                                String strReferrer = response.getInstallReferrer();
                                analyzeReferrer(context, strReferrer);
                            } catch (Exception e) {
                            } finally {
                                try {
                                    installReferrerClient.endConnection();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            break;
                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            // API not available on the current Play Store app
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            // Connection could not be established
                            break;
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void detectFacebookReferrer(final Context context) {
        if (null == context)
            return;

        try {
            AppLinkData.fetchDeferredAppLinkData(context, appLinkData -> {
                if (null == appLinkData
                        || null == appLinkData.getTargetUri()
                        || TextUtils.isEmpty(appLinkData.getTargetUri().toString()))
                    return;

                String strReferrer = "utm_source=facebook&utm_medium=" + appLinkData.getTargetUri().toString();
                analyzeReferrer(context, strReferrer);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean analyzeReferrer(Context context, String strReferrer) {
        if (null == context || TextUtils.isEmpty(strReferrer))
            return false;

        // Referrer解码
        strReferrer = UtilsEncrypt.urlDecode(strReferrer);
        if (TextUtils.isEmpty(strReferrer) || strReferrer.contains("not set"))
            return false;

        // 将referrer写入本地
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("referrer", strReferrer).apply();

        // 打点使用
        UtilsEnv.setReferrer(strReferrer);
        return true;
    }
}
